package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.ui.detail.ComponentListFragment;
import com.ghstudios.android.features.decorations.DecorationDetailFragment;
import com.ghstudios.android.features.items.ItemToSkillFragment;

public class DecorationDetailPagerAdapter extends FragmentPagerAdapter {
	
	private long decorationId;

    // Tab titles
    private String[] tabs = { "Detail", "Skills", "Components" };

	public DecorationDetailPagerAdapter(FragmentManager fm, long id) {
		super(fm);
		this.decorationId = id;
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Decoration detail
			return DecorationDetailFragment.newInstance(decorationId);
		case 1:
			// Decoration skills
			return ItemToSkillFragment.newInstance(decorationId, "Decoration");
		case 2:
			// Item components to make Decoration
			return ComponentListFragment.newInstance(decorationId);
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