package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.database.DataManager;

/**
 * Created by Carlos on 1/22/2017.
 */
public class MonsterEquipmentListCursorLoader extends SQLiteCursorLoader {
    private long monsterId;

    public MonsterEquipmentListCursorLoader(Context context, long monsterId) {
        super(context);
        this.monsterId = monsterId;
    }

    @Override
    public Cursor loadCursor() {
        // Query the specific armor
        return DataManager.get(getContext()).queryMonsterEquipment(monsterId);
    }
}