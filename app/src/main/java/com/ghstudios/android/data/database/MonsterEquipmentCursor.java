package com.ghstudios.android.data.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.MonsterEquipment;

/**
 * Created by Carlos on 1/24/2017.
 */

public class MonsterEquipmentCursor extends CursorWrapper {
    public MonsterEquipmentCursor(Cursor c) {
        super(c);
    }

    public MonsterEquipment getMonsterItem() {
        MonsterEquipment monItem = new MonsterEquipment();

        // todo: this type of logic is often copy pasted and should be centralized
        Item item = new Item();
        item.setId(getLong(getColumnIndex(S.COLUMN_ITEMS_ID)));
        item.setName(getString(getColumnIndex(S.COLUMN_ITEMS_NAME)));
        item.setType(getString(getColumnIndex(S.COLUMN_ITEMS_TYPE)));
        item.setSubType(getString(getColumnIndex(S.COLUMN_ITEMS_SUB_TYPE)));
        item.setRarity(getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY)));
        item.setFileLocation(getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME)));

        monItem.setItem(item);
        monItem.setRank(getString(getColumnIndex(S.COLUMN_HUNTING_REWARDS_RANK)));
        return monItem;
    }
}
