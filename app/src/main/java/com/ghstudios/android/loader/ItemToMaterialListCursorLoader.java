package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.data.database.ItemToMaterialCursor;

/**
 * Created by E410474 on 7/7/2016.
 */
public class ItemToMaterialListCursorLoader extends SQLiteCursorLoader {

    long id;

    public ItemToMaterialListCursorLoader(Context c,long id){
        super(c);
        this.id = id;
    }

    @Override
    protected ItemToMaterialCursor loadCursor() {
        return new ItemToMaterialCursor(DataManager.get(getContext()).queryItemsForMaterial(id));
    }
}
