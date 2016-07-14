package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.data.database.PalicoWeaponCursor;

/**
 * Created by Joseph on 7/10/2016.
 */
public class PalicoWeaponListCursorLoader extends SQLiteCursorLoader {

    public PalicoWeaponListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        PalicoWeaponCursor cursor = DataManager.get(getContext()).queryPalicoWeapons();
        return cursor;
    }

}
