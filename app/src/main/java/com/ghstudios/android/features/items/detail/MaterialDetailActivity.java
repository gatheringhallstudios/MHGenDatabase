package com.ghstudios.android.features.items.detail;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

/**
 * Used to display items that are considered arbitrary materials, such as "Rathalos Item".
 * Created by Joseph on 7/7/2016.
 */
public class MaterialDetailActivity extends GenericActivity {

    public static final String EXTRA_MATERIAL_ITEM_ID = "MATERIAL_ID";
    long id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getLongExtra(EXTRA_MATERIAL_ITEM_ID,0);
        setTitle(DataManager.get().getItem(id).getName());

        // Tag as top level activity
        //super.setAsTopLevel();
    }

    @Override
    protected Fragment createFragment() {
        long itemId = getIntent().getLongExtra(EXTRA_MATERIAL_ITEM_ID,0);
        return MaterialDetailItemFragment.newInstance(itemId);
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ITEMS;
    }
}
