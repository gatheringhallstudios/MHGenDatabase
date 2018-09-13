package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.data.cursors.PalicoWeaponCursor;

/**
 * Created by Joseph on 7/10/2016.
 */
public class PalicoWeaponListCursorLoader extends SQLiteCursorLoader {

    public PalicoWeaponListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        PalicoWeaponCursor cursor = DataManager.get().queryPalicoWeapons();
        return cursor;
    }

}
