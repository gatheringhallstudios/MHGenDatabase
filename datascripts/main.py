import csv
import db
import json
from os.path import join

import sqlalchemy.orm

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
    armor_by_id = {a._id:a for a in session.query(db.Item).filter(db.Item.type == 'Armor')}
    
    # Monster Items
    monster_to_id = {}
    for monster in session.query(db.Monster):
        monster_to_id[monster.name] = monster._id

    def create_result(armor, monster_name):
        return {
            'Item Id': armor._id,
            'Item Name': armor.name,
            'Monster': monster_name,
            'Components': None
        }

    # Try to create armor to monster assocation. We don't have that in any file
    def find_embedded_monster_name(name):
        name_lower = name.lower()
        for monster_name, monster_id in monster_to_id.items():
            if monster_name.lower() in name_lower:
                return monster_name
    
    results = []
    for armor in armor_by_id.values():
        if armor.rarity == 11:
            # todo: support deviant armor
            continue

        # First pass - check for monster name substring
        monster_name = find_embedded_monster_name(armor.name)
        if monster_name:
            results.append(create_result(armor, monster_name))
            continue

        

    save_csv(results, 'source_data/monster_armor.csv')

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
