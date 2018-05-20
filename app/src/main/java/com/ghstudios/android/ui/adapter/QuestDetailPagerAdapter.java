package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.features.quests.QuestDetailFragment;
import com.ghstudios.android.features.quests.QuestRewardFragment;

public class QuestDetailPagerAdapter extends FragmentPagerAdapter {
	
	private long questId;

    // Tab titles
    private String[] tabs = { "Detail", "Rewards"};

	public QuestDetailPagerAdapter(FragmentManager fm, long id) {
		super(fm);
		this.questId = id;
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Quest detail
			return QuestDetailFragment.newInstance(questId);
		case 1:
			// Quest rewards
			return QuestRewardFragment.newInstance(questId);
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
		return 2;
	}

}