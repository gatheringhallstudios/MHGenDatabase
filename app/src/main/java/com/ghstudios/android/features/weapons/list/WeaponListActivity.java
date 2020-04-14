package com.ghstudios.android.features.weapons.list;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

public class WeaponListActivity extends GenericActivity {
    public static final String EXTRA_WEAPON_TYPE =
            "com.daviancorp.android.android.ui.list.weapon_type";

    private String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        type = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getString(EXTRA_WEAPON_TYPE);
        }

        super.onCreate(savedInstanceState);
        setTitle(type);

    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.WEAPONS;
    }

    @Override
    protected Fragment createFragment() {
        return WeaponListFragment.newInstance(type);
    }

}
