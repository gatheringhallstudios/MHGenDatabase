package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.features.items.ItemComponentFragment;
import com.ghstudios.android.features.items.ItemDetailFragment;
import com.ghstudios.android.features.items.ItemLocationFragment;
import com.ghstudios.android.features.items.ItemMonsterFragment;
import com.ghstudios.android.features.items.ItemQuestFragment;
import com.ghstudios.android.features.combining.CombiningListFragment;

public class ItemDetailPagerAdapter extends FragmentPagerAdapter {
	
	private long itemId;

    // Tab titles
    // TODO: Reenable arena quest tab
    private String[] tabs = { "Detail", "Combining", "Usage", "Monster", "Quest", "Location"};

	public ItemDetailPagerAdapter(FragmentManager fm, long id) {
		super(fm);
		this.itemId = id;
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Item detail
			return ItemDetailFragment.newInstance(itemId);
        case 1:
            return CombiningListFragment.newInstance(itemId);		//JOE: Maybe check type to see if this is needed..?
		case 2:
			// List of Armor, Decoration, and Weapon the Item can be used for
			return ItemComponentFragment.newInstance(itemId);
		case 3:
			// Monster drops
			return ItemMonsterFragment.newInstance(itemId);
		case 4:
			// Quest rewards
			return ItemQuestFragment.newInstance(itemId);
		case 5:
			// Location drops; gathering
			return ItemLocationFragment.newInstance(itemId);
		//JOE: No wyporium in MHGen
        //case 6:
            // Wyporium trade info
        //    return ItemTradeFragment.newInstance(itemId);
//		case 5:
//			// ArenaQuest rewards
//            //TODO reenable when arena quests are complete.
//			return ItemArenaFragment.newInstance(itemId);
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
		return tabs.length;
	}

}