import csv
import json
from os.path import join

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
    """Saves a dict list as a CSV, doing some last minute validations. 
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