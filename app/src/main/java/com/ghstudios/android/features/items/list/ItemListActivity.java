package com.ghstudios.android.features.items.list;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

public class ItemListActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_items);

        // Tag as top level activity
        super.setAsTopLevel();
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ITEMS;
    }

    @Override
    protected Fragment createFragment() {
        return new ItemListFragment();
    }
}
