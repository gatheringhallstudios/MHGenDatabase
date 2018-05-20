package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.loader.QuestListCursorLoader;
import com.ghstudios.android.features.quests.QuestExpandableListFragment;

public class QuestExpandableListPagerAdapter extends FragmentPagerAdapter {

    // TODO reenable when dlc quests are complete
    private String[] tabs = {
            QuestListCursorLoader.HUB_CARAVAN,
            QuestListCursorLoader.HUB_GUILD,
            QuestListCursorLoader.HUB_EVENT,
			QuestListCursorLoader.HUB_PERMIT
	};

	public QuestExpandableListPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
			case 0: return QuestExpandableListFragment.newInstance("Village");
			case 1: return QuestExpandableListFragment.newInstance("Guild");
			case 2: return QuestExpandableListFragment.newInstance("Event");
			case 3: return QuestExpandableListFragment.newInstance("Permit");
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
		return 4;
	}

}