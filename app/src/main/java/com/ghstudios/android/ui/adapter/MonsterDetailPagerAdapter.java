package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.loader.HuntingRewardListCursorLoader;
import com.ghstudios.android.ui.detail.MonsterDamageFragment;
import com.ghstudios.android.ui.detail.MonsterEquipmentFragment;
import com.ghstudios.android.ui.detail.MonsterHabitatFragment;
import com.ghstudios.android.ui.detail.MonsterQuestFragment;
import com.ghstudios.android.ui.detail.MonsterRewardFragment;
import com.ghstudios.android.ui.detail.MonsterStatusFragment;
import com.ghstudios.android.ui.detail.MonsterSummaryFragment;

import java.util.ArrayList;

public class MonsterDetailPagerAdapter extends FragmentPagerAdapter {
	
	private long monsterId;

	public MonsterDetailPagerAdapter(FragmentManager fm, long id) {
		super(fm);
		this.monsterId = id;

		tabs = new ArrayList<>();
		tabs.add("Summary");
		tabs.add("Damage");
		tabs.add("Status");
		tabs.add("Habitat");
		tabs.add("Low-Rank");
		tabs.add("High-Rank");
		//tabs.add("G-Rank");	//No G Rank in MHGen...
		tabs.add("Equipment");
		tabs.add("Quest");
	}

    // Tab titles
    private ArrayList<String> tabs;

	public void RemoveTab(String name){
		for(int i=0;i<tabs.size();i++)
			if(tabs.get(i).equals(name)) {
				tabs.remove(i);
				return;
			}
	}

	@Override
	public Fragment getItem(int index) {

		String title = tabs.get(index);
		if(title.equals("Summary"))
			return MonsterSummaryFragment.newInstance(monsterId);
		else if(title.equals("Damage"))
			return MonsterDamageFragment.newInstance(monsterId);
		else if(title.equals("Status"))
			return MonsterStatusFragment.newInstance(monsterId);
		else if(title.equals("Habitat"))
			return MonsterHabitatFragment.newInstance(monsterId);
		else if(title.equals("Low-Rank"))
			return MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_LR);
		else if(title.equals("High-Rank"))
			return MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_HR);
		else if(title.equals("G-Rank"))
			return MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_G);
		else if(title.equals("Equipment"))
			return MonsterEquipmentFragment.newInstance(monsterId);
		else if(title.equals("Quest"))
			return MonsterQuestFragment.newInstance(monsterId);
		else return null;

	}

    @Override
    public CharSequence getPageTitle(int index) {
        return tabs.get(index);
    }

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return tabs.size();
	}

}