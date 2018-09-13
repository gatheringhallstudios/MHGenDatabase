package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.data.cursors.ItemToMaterialCursor;

/**
 * Created by Joseph on 7/7/2016.
 */
public class ItemToMaterialListCursorLoader extends SQLiteCursorLoader {

    long id;

    public ItemToMaterialListCursorLoader(Context c,long id){
        super(c);
        this.id = id;
    }

    @Override
    protected ItemToMaterialCursor loadCursor() {
        return new ItemToMaterialCursor(DataManager.get().queryItemsForMaterial(id));
    }
}
