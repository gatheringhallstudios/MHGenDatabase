package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.ui.list.ArmorExpandableListFragment;

public class ArmorExpandableListPagerAdapter extends FragmentPagerAdapter {

    // Tab titles
    private String[] tabs = { "Blade", "Gunner", "Both" };

	public ArmorExpandableListPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
            // Armor list for both Blademaster
            return ArmorExpandableListFragment.newInstance(Armor.ARMOR_TYPE_BLADEMASTER);
		case 1:
            // Armor list for both Gunner
            return ArmorExpandableListFragment.newInstance(Armor.ARMOR_TYPE_GUNNER);
		case 2:
            // Armor list for both Blademaster and Gunner
            return ArmorExpandableListFragment.newInstance(Armor.ARMOR_TYPE_BOTH);
		default:
			return null;
		}
	}

    @Override
    public CharSequence getPageTitle(int index) {
        return tabs[index];
    }

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}