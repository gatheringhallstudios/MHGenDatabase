import csv
import db
import json
from os.path import join

import sqlalchemy.orm
from sqlalchemy.orm import selectinload

def read_csv(location):
    "Reads a csv file as an object list without additional processing"
    with open(location, encoding="utf-8") as f:
        reader = csv.DictReader(f)
        items = list(reader)

        # CSV does not distinguish between empty string and null
        # Set empties to null
        for item in items:
            for key, value in item.items():
                if value == '':
                    item[key] = None
            
        return items

def determine_fields(obj_list):
    """
    Returns the set of all possible keys in the object list
    """
    fields = []
    for obj in obj_list:
        for key in obj.keys():
            if key not in fields:
                fields.append(key)

    return fields

def save_csv(obj_list, location):
    """Saves a dict list as a  CSV, doing some last minute validations. 
    Fields are auto-determined"""

    fields = determine_fields(obj_list)
    with open(location, 'w', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fields, lineterminator='\n')
        writer.writeheader()
        writer.writerows(obj_list)

def save_json_asset(obj, filename):
    path = join('../app/src/main/assets/', filename)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(obj, f, indent="\t")

def create_monster_armor_csv(session: sqlalchemy.orm.Session):
    # We don't have an armor -> monster mapping file. Try to run a few possible heuristics
    all_armor = (session.query(db.Item)
        .filter(db.Item.type == 'Armor')
        .options(selectinload(db.Item.components))
        .all())
    armor_by_id = {a._id:a for a in all_armor}
    
    # Monster Items
    monster_map = {}
    deviant_monster_starter_to_name = {}
    for monster in session.query(db.Monster):
        monster_map[monster._id] = monster.name

        # Map names like "Drilltusk". Used for hooking onto deviant armor key items
        if int(monster._class) == 2:
            first_word, sep, rest = monster.name.partition(' ')
            deviant_monster_starter_to_name[first_word.strip()] = monster.name

    # Item to monster names. First get all, then filter to only those with a SINGLE monster association
    item_to_monster_ids = {}
    gather_item_ids = session.query(db.Gathering.item_id).distinct()
    print(gather_item_ids.all())
    for reward in session.query(db.HuntingReward).filter(~db.HuntingReward.item_id.in_(gather_item_ids)):
        item_to_monster_ids.setdefault(reward.item_id, set())
        item_to_monster_ids[reward.item_id].add(reward.monster_id)
    item_to_monster_ids = {p[0]:next(iter(p[1])) for p in item_to_monster_ids.items() if len(p[1]) == 1}

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
        for monster_id, monster_name in monster_map.items():
            if monster_name.lower() in name_lower:
                return monster_name
    
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
                monster_name = monster_map[monster_id]
                monster_ids.add(monster_id)
                item_name = f'{item_name} [{monster_name}]'

            item_names.append(item_name)

        unresolved.append(create_result(armor, components=item_names))
        
    save_csv(results + unresolved, 'source_data/monster_armor.csv')

def bind_monster_weapons(session: sqlalchemy.orm.Session):
    "Creates an association from monsters to monster items and places it in the assets folder"

    # Loads monster data, mapped by name
    monster_to_id = {}
    for monster in session.query(db.Monster):
        monster_to_id[monster.name] = monster._id

    # Beginning of output data. Note that currently only weapons are mapped
    output = {}
    entries = read_csv('source_data/monster_weapon.csv')
    entries.extend(read_csv('source_data/monster_armor.csv'))
    for entry in entries:
        item_id = int(entry['Item Id'])
        monster_name = entry['Monster']
        if not monster_name:
            continue
        if monster_name not in monster_to_id:
            print("No monster named " + monster_name)
            continue
        
        monster_id = monster_to_id[monster_name]
        output.setdefault(monster_id, { 'armor': [], 'weapons': [] })

        item_entry = session.query(db.Item).\
            filter(db.Item._id == item_id).\
            one()
        if item_entry.type == 'Weapon':
            output[monster_id]['weapons'].append(item_id)
        elif item_entry.type == 'Armor':
            output[monster_id]['armor'].append(item_id)
        else:
            raise Exception("Cannot handle item type " + item_entry.type)

    # Ensure all entries are sorted
    for value in output.values():
        for subvalue in value.values():
            subvalue.sort()

    save_json_asset(output, 'monster_items.json')

if __name__ == '__main__':
    session = db.read_db()
    create_monster_armor_csv(session)
    bind_monster_weapons(session)
