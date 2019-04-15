import db

import sqlalchemy.orm
from sqlalchemy.orm import selectinload

from util import read_csv, save_json_asset

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
        monster_names = [m.strip() for m in monster_name.split(',')]
        if any(name not in monster_to_id for name in monster_names):
            print("No monster named " + monster_name)
            continue

        item_entry = session.query(db.Item).\
            filter(db.Item._id == item_id).\
            one()

        for monster_name in monster_names:
            monster_id = monster_to_id[monster_name]
            output.setdefault(monster_id, { 'armor': [], 'weapons': [] })

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
