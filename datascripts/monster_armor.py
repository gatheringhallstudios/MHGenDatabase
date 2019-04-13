import db

import sqlalchemy.orm
from sqlalchemy.orm import selectinload

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

def create_monster_armor_csv(session: sqlalchemy.orm.Session):
    # We don't have an armor -> monster mapping file. Try to run a few possible heuristics
    all_armor = (session.query(db.Item)
        .filter(db.Item.type == 'Armor')
        .options(selectinload(db.Item.components))
        .all())
    armor_by_id = {a._id:a for a in all_armor}

    monster_data = MonsterDataCollection(session)

    # Create monster chains. These list subspecies.
    # First we hardcode them with names and then turn them to ids
    monster_chains = [
        ["Deviljho", "Savage Deviljho"],
        ["Gore Magala", "Chaotic Gore Magala"],
        ["Rathian", "Gold Rathian"],
        ["Rathalos", "Silver Rathalos"],
        ["Rajang", "Furious Rajang"],
        ["Brachydios", "Raging Brachydios"]
    ]
    monster_chains = [
        [monster_data.by_name(name)._id for name in chain] for chain in monster_chains
    ]
    
    # Monster Items
    deviant_monster_starter_to_name = {}
    for monster in monster_data.monsters:
        if int(monster._class) == 2:
            first_word, sep, rest = monster.name.partition(' ')
            deviant_monster_starter_to_name[first_word.strip()] = monster.name

    # Item to monster names. First get all of them. An item may drop from multiple monsters
    item_to_monster_ids_multi = {}
    gather_item_ids = session.query(db.Gathering.item_id).distinct()
    for reward in session.query(db.HuntingReward).filter(~db.HuntingReward.item_id.in_(gather_item_ids)):
        item_to_monster_ids_multi.setdefault(reward.item_id, set())
        item_to_monster_ids_multi[reward.item_id].add(reward.monster_id)

    # Try to filter only to items that drop from only a single monster. Note: Some monsters are subspecies. Use the monster chains to figure that out
    item_to_monster_ids = {}
    for item_id, monster_ids in item_to_monster_ids_multi.items():
        if len(monster_ids) == 1:
            item_to_monster_ids[item_id] = next(iter(monster_ids))
            continue
        
        # If this item exclusively by a chain of monsters, pick the first one
        for chain in monster_chains:
            if monster_ids.issubset(set(chain)):
                item_to_monster_ids[item_id] = next(filter(lambda m: m in monster_ids, chain))

    def create_result(armor, monster_name=None, components=[]):
        return {
            'Item Id': armor._id,
            'Item Name': armor.name,
            'Monster': monster_name,
            'Components': '\n'.join(components)
        }

    # Try to create armor to monster assocation. We don't have that in any file
    def find_embedded_monster_name(name):
        name_lower = name.lower()
        for monster in monster_data.monsters:
            if monster.name.lower() in name_lower:
                return monster.name
    
    results = []
    unresolved = []
    for armor in armor_by_id.values():
        components = list(armor.components)

        key_items = list(filter(lambda c: c.key, components))
        key_item = key_items[0].component_item if key_items else None

        # Handle Deviant Armor
        if armor.rarity == 11:
            if not key_item: 
                raise Exception("Key Item is required for deviant armor " + armor.name)
            first_word, sep, rest = key_item.name.partition(' ')
            first_word.strip()

            monster_name = deviant_monster_starter_to_name[first_word]
            result = create_result(armor, monster_name, [key_item.name])
            
            results.append(result)
            continue

        # First pass - check for monster name substring
        monster_name = find_embedded_monster_name(armor.name)
        if monster_name:
            results.append(create_result(armor, monster_name))
            continue

        # Second pass, check the components, see if anything is possibly unanimaous
        item_names = []
        monster_ids = set()
        for component in components:
            item_name = component.component_item.name
            monster_id = item_to_monster_ids.get(component.component_item_id)
            if monster_id:
                monster_name = monster_data.by_id(monster_id).name
                monster_ids.add(monster_id)
                item_name = f'{item_name} [{monster_name}]'

            item_names.append(item_name)

        

        unresolved.append(create_result(armor, components=item_names))
        
    save_csv(results + unresolved, 'source_data/monster_armor.csv')

if __name__ == '__main__':
    session = db.read_db()
    create_monster_armor_csv(session)