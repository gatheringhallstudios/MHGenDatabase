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

def save_json_asset(obj, filename):
    path = join('../app/src/main/assets/', filename)
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(obj, f)

def bind_monster_weapons(session : sqlalchemy.orm.Session):
    "Creates an association from monsters to monster items and places it in the assets folder"
    monster_to_id = {}

    output = {}
    entries = read_csv('source_data/monster_weapon.csv')
    for entry in entries:
        item_id = int(entry['Item Id'])
        monster_name = entry['Monster']
        if not monster_name:
            continue
        if monster_name not in monster_to_id:
            result = session.query(db.Monster).\
                filter(db.Monster.name == monster_name).\
                one_or_none()
            if not result:
                print("No monster named " + monster_name)
                continue
            monster_to_id[monster_name] = result._id
        
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
    bind_monster_weapons(session)