import db

import sqlalchemy.orm
from sqlalchemy.orm import selectinload, joinedload, subqueryload

from util import save_csv

class MonsterDataCollection:
    def __init__(self, session: sqlalchemy.orm.Session):
        self.monster_by_id = {}
        self.monster_by_name = {}
        
        for monster in session.query(db.Monster):
            self.monster_by_id[monster._id] = monster
            self.monster_by_name[monster.name] = monster

    @property
    def monsters(self):
        return self.monster_by_id.values()

    def by_name(self, name):
        return self.monster_by_name[name]

    def by_id(self, id):
        return self.monster_by_id[id]

class MonsterChain:
    "A chain of monsters, named after the first one. Create using monster DB objects"
    def __init__(self, entries):
        self.name = entries[0].name
        self.entries = entries

        self.names = [entry.name for entry in self.entries]
        self.ids = [entry._id for entry in self.entries]
        self.ids_set = set(self.ids)

    def contains_ids(self, other_ids):
        return set(other_ids).issubset(self.ids_set)

    def by_id(self, other_ids):
        "Returns multiple monster objects by id"
        results = []
        for entry in self.entries:
            if entry._id in other_ids:
                results.append(entry)
        return results

class MonsterChainList:
    def __init__(self, chains):
        self.chains = chains

        # Mapping for monster -> participating chain idx. Used for some lookups
        self.monster_to_chain = {}
        for chain in chains:
            for monster_id in chain.ids:
                self.monster_to_chain[monster_id] = chain

    def __iter__(self):
        return self.chains.__iter__()

    def chain_by_id(self, *ids):
        "Returns a chain containing one or more ids"
        if len(ids) == 0:
            return None

        chain = self.monster_to_chain.get(ids[0])
        if len(ids) == 1 or chain.contains_ids(ids):
            return chain

        return None

special_mappings = {}

# Try to create armor to monster assocation. We don't have that in any file
def find_embedded_monster_name(monster_data, name, extra_mappings={}):
    global special_mappings

    if not special_mappings:
        # Special notes: Gore Magala not here due to chaotic gore
        special_mappings = {
            'C.Fatalis': 'Fatalis',
            'Lao-Shan': 'Lao-Shan Lung',
            'Kushala': 'Kushala Daora',
            'Daora': 'Kushala Daora',
            'S.Rathalos': 'Silver Rathalos',
            'S.Rath': 'Silver Rathalos',
            'Silver Sol': 'Silver Rathalos',
            'G.Rathian': 'Gold Rathian',
            'Golden Lune': 'Gold Rathian',
            'S.Magala': 'Shagaru Magala',
            'R.Ludroth': 'Royal Ludroth',
            'Garuga': 'Yian Garuga',
            'Kut-Ku': 'Yian Kut-Ku',
            'Kaiser': 'Teostra',
            'Kecha': 'Kecha Wacha',
            'Shogun': 'Shogun Ceanataur',
            'Cham ': 'Chameleos' # space after cham is on purpose
        }

        # Deviant monsters
        for monster in monster_data.monsters:
            if int(monster._class) == 2:
                first_word, sep, rest = monster.name.partition(' ')
                special_mappings[first_word.strip()] = monster.name
    
    name_lower = name.lower()

    # Check extra mappings first
    for lookup_name, monster_name in extra_mappings.items():
        if lookup_name.lower() in name_lower:
            return monster_name

    # Then check global special mappings
    for lookup_name, monster_name in special_mappings.items():
        if lookup_name.lower() in name_lower:
            return monster_name

    # Check all monsters after
    for monster in monster_data.monsters:
        if monster.name.lower() in name_lower:
            return monster.name

def create_item_to_monster_mapping(session: sqlalchemy.orm.Session, monster_data: MonsterDataCollection, monster_chains):
    # Item to monster names. First get all of them. An item may drop from multiple monsters
    item_to_monster_ids_multi = {}
    gather_item_ids = session.query(db.Gathering.item_id).distinct()
    for reward in session.query(db.HuntingReward).filter(~db.HuntingReward.item_id.in_(gather_item_ids)):
        item_to_monster_ids_multi.setdefault(reward.item_id, set())
        item_to_monster_ids_multi[reward.item_id].add(reward.monster_id)

    # Figure out ticket items by doing a monster name check
    ticket_items = session.query(db.Item).filter(db.Item.name.like('%Ticket%'))
    for ticket in ticket_items:
        monster_name = find_embedded_monster_name(monster_data, ticket.name)
        if monster_name:
            monster_id = monster_data.by_name(monster_name)._id
            item_to_monster_ids_multi.setdefault(ticket._id, set())
            item_to_monster_ids_multi[ticket._id].add(monster_id)
        else:
            print("Could not map " + ticket.name)

    # Map out special items special hyper items:
    hyper_items = session.query(db.Item).filter(db.Item.name.like('Hyper%'))
    for item in hyper_items:
        monster_name = find_embedded_monster_name(monster_data, item.name, {
            'Duram': 'Duramboros',
            'Brach': 'Brachydios',
            'Tetsu': 'Tetsucabra',
            'S.Queen': 'Seltas Queen',
            'Narga': 'Nargacuga',
            'G.Magala': 'Gore Magala'
        })
        if monster_name:
            item_to_monster_ids_multi.setdefault(item._id, set())
            item_to_monster_ids_multi[item._id].add(monster_data.by_name(monster_name)._id)
        else:
            print("Could not map " + item.name)

    # Try to filter only to items that drop from only a single monster. Note: Some monsters are subspecies. Use the monster chains to figure that out
    item_to_monster_ids = {}
    for item_id, monster_ids in item_to_monster_ids_multi.items():
        if len(monster_ids) == 1:
            item_to_monster_ids[item_id] = next(iter(monster_ids))
            continue
        
        # If this item exclusively by a chain of monsters, pick the first one
        for chain in monster_chains:
            if chain.contains_ids(monster_ids):
                item_to_monster_ids[item_id] = chain.by_id(monster_ids)[0]._id

    return item_to_monster_ids

def create_monster_chains(monster_data, list_of_list_of_names):
    return MonsterChainList([
        MonsterChain([monster_data.by_name(name) for name in chain]) for chain in list_of_list_of_names
    ])

def create_subspecies_chains(monster_data):
    # Create monster chains. These list subspecies.
    # First we hardcode them with names and then turn them to MonsterChain objects
    monster_chains = [
        ["Deviljho", "Savage Deviljho"],
        ["Gore Magala", "Chaotic Gore Magala"],
        ["Rathian", "Gold Rathian", "Dreadqueen Rathian"],
        ["Rathalos", "Silver Rathalos", "Dreadking Rathalos"],
        ["Rajang", "Furious Rajang"],
        ["Brachydios", "Raging Brachydios"]
    ]
    
    for deviant in filter(lambda m: m._class == '2', monster_data.monsters):
        tag, sep, base = deviant.name.partition(' ')
        if not any(base in c for c in monster_chains):
            print(f"Added chain for deviant {deviant.name} to base monster {base}")
            monster_chains.append([base, deviant.name])

    return create_monster_chains(monster_data, monster_chains)

def create_bossvariant_chains(monster_data):
    return create_monster_chains(monster_data, [
        ['Conga', 'Congalala'],
        ['Hermitaur', 'Daimyo Hermitaur'],
        ['Ceanataur', 'Shogun Ceanataur'],
        ['Maccao', 'Great Maccao'],
        ['Velociprey', 'Velocidrome'],
        ['Genprey', 'Gendrome'],
        ['Ioprey', 'Iodrome']
    ])

def create_monster_armor_csv(session: sqlalchemy.orm.Session):
    "Create an armor -> monster mapping file using several heuristics."

    monster_data = MonsterDataCollection(session)

    monster_chains = create_subspecies_chains(monster_data)
    monster_boss_chains = create_bossvariant_chains(monster_data)

    # Create mapping from item id to relevant monster id
    item_to_monster_ids = create_item_to_monster_mapping(session, monster_data, monster_chains)

    disqualified_families = [
        "Hunter's ", "Bone", "Leather", "Damascus", "Chainmail"
    ]

    def create_result(armor, monster_name=None, components=None):
        return {
            'Item Id': armor._id,
            'Item Name': armor.item.name,
            'Monster': monster_name,
            'Components': '\n'.join(components or [])
        }

    def process_armor(armor):
        components = list(armor.item.components)

        key_items = list(filter(lambda c: c.key, components))
        key_item = key_items[0].component_item if key_items else None
        key_item_id = key_item._id if key_item else None

        # First pass - check for monster name substring. 
        monster_name = find_embedded_monster_name(monster_data, armor.item.name)
        if monster_name:
            return (monster_name, None)

        # Second pass, check the components, see if anything is possibly unanimaous
        item_names = []
        monster_ids = []
        large_monster_ids = []
        for component in components:
            item_name = component.component_item.name
            monster_id = item_to_monster_ids.get(component.component_item_id)
            if monster_id:
                monster = monster_data.by_id(monster_id)
                monster_name = monster.name
                item_name = f'{item_name} [{monster_name}]'
                
                monster_ids.append(monster_id)
                if monster._class != '1':
                    large_monster_ids.append(monster_id)

            item_names.append(item_name)

        # If there is a mix of small and large monster, take only large ones
        if large_monster_ids:
            monster_ids = large_monster_ids

        # inner function to handle the acquisition of the result
        def resolve_monster_from_participant_key(participant):
            monster_id = participant[1]
            if participant[0] == 'chain':
                chain = monster_chains.chain_by_id(monster_id)
                monster_id = list(chain.by_id(monster_ids))[-1]._id
            
            return monster_data.by_id(monster_id)

        # Try to count how many of each key there is. Chains share a key.
        # Find what the first and largest participations are.
        participations = {}
        for monster_id in monster_ids:
            key = ('monster', monster_id)
            chain = monster_chains.chain_by_id(monster_id)
            if chain:
                key = ('chain', chain.ids[0])
            participations.setdefault(key, 0)
            participations[key] += 1

        participations = list(participations.items())
        participations.sort(key=lambda p: p[1], reverse=True)

        # If there's only one participant, must appear in at least 2 items, or must be the key
        if len(participations) == 1:
            participant, count = participations[0]
            monster = resolve_monster_from_participant_key(participant)
            key_monster_id = item_to_monster_ids.get(key_item_id)
            if count >= 2 or (len(components) < 4 and key_monster_id == monster._id):
                return (monster.name, item_names)
        
        # If there's more than one participant... must be >= 2 items, or if equal take the key item one
        # Instant failure if the key is another monster
        if len(participations) > 1 and participations[0][1] >= 2:
            participant_best, count_best = participations[0]
            participant_second_best, count_second_best = participations[1]

            if count_best == count_second_best and key_item_id in item_to_monster_ids:
                monster_id = item_to_monster_ids[key_item_id]
                monster_name = monster_data.by_id(monster_id).name
                return (monster_name, item_names)
            elif count_best > count_second_best:
                monster = resolve_monster_from_participant_key(participant_best)
                if key_item_id not in item_to_monster_ids or item_to_monster_ids[key_item_id] == monster._id:
                    return (monster.name, item_names)

        # If we get here, it failed to resolve
        return (None, item_names)

    all_armor_families = (
        session.query(db.ArmorFamily)
        .options(
            joinedload(db.ArmorFamily.armor_pieces)
            .joinedload(db.Armor.item)
            .selectinload(db.Item.components))
        ).all()
    all_armor_families.sort(key=lambda f: f.armor_pieces[0]._id)

    all_results = []

    for family in all_armor_families:
        # If this family is disqualified, add failed results and continue
        if any((d in family.name) for d in disqualified_families):
            print("Disqualified family - " + family.name)
            for armor in family.armor_pieces:
                all_results.append(create_result(armor))
            continue

        sub_results = []
        for armor in family.armor_pieces:
            (monster_name, components) = process_armor(armor)
            sub_results.append([armor, monster_name, components])

        # Second pass, check results of this family. They must all be same monster or it will fail
        #monsters = set([r['Monster'] for r in results])
        for (armor, monster_name, components) in sub_results:
            all_results.append(create_result(armor, monster_name, components))

    # Assemble resolve mapping for next step (we'd have done it earlier, but resolving can happen in multiple places)

    # Go through armor families now, find unresolved subinstances and report them
    # families = session.query(db.ArmorFamily).options(selectinload(db.ArmorFamily.armor_pieces))
    # for family in families:
    #     resolved_monsters = [armor_to_monster.get(a._id) for a in family.armor_pieces]
    #     unique_monsters = set(resolved_monsters)
    #     if len(unique_monsters) <= 1:
    #         continue
    #     print(family.name + ": " + " ,".join((str(m) for m in unique_monsters)))
        
    save_csv(all_results, 'source_data/monster_armor.csv')

if __name__ == '__main__':
    session = db.read_db()
    create_monster_armor_csv(session)