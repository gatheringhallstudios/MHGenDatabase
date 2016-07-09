package com.ghstudios.android.ui.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.general.GenericActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

/**
 * Created by E410474 on 7/7/2016.
 */
public class MaterialDetailActivity extends GenericActivity {

    public static final String EXTRA_MATERIAL_ITEM_ID = "MATERIAL_ID";
    long id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra(EXTRA_MATERIAL_ITEM_ID,0);
        setTitle(DataManager.get(getApplicationContext()).getItem(id).getName());

        // Tag as top level activity
        //super.setAsTopLevel();
    }

    @Override
    protected Fragment createFragment() {
        super.detail = MaterialDetailItemFragment.newInstance(getIntent().getLongExtra(EXTRA_MATERIAL_ITEM_ID,0));
        return super.detail;
    }

    @Override
    protected MenuSection getSelectedSection() {
        return MenuSection.ITEMS;
    }
}
