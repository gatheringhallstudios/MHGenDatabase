package com.ghstudios.android.ui.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.general.GenericActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class WeaponSelectionListActivity extends GenericActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.weapons);

        // Tag as top level activity
        super.setAsTopLevel();
    }

    @Override
    protected MenuSection getSelectedSection() {
        return MenuSection.WEAPONS;
    }

    @Override
    protected Fragment createFragment() {
        super.detail = new WeaponSelectionListFragment();
        return super.detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

}
