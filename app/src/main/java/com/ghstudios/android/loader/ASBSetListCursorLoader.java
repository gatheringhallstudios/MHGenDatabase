package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;
import com.ghstudios.android.data.DataManager;

public class ASBSetListCursorLoader extends SQLiteCursorLoader {

    public ASBSetListCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor loadCursor() {
        return DataManager.get().queryASBSets();
    }
}
