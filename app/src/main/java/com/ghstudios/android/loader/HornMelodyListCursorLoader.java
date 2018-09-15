package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.data.DataManager;

public class HornMelodyListCursorLoader extends SQLiteCursorLoader {
    private long weaponId;

    public HornMelodyListCursorLoader(Context context, long weaponId) {
        super(context);
        this.weaponId = weaponId;
    }

    @Override
    protected Cursor loadCursor() {
        // Query the list of skills from a skill tree
        Weapon w = DataManager.get().getWeapon(weaponId);
        return DataManager.get().queryMelodiesFromNotes(w.getHornNotes());
    }
}
