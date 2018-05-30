package com.ghstudios.android.features.armorsetbuilder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

public class ASBSetListActivity extends GenericActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.activity_asb_sets);

        // Tag as top level activity
        super.setAsTopLevel();
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR_SET_BUILDER;
    }

    @Override
    protected Fragment createFragment() {
        return new ASBSetListFragment();
    }
}
