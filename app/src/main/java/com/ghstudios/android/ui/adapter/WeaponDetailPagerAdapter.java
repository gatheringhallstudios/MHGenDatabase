package com.ghstudios.android.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.ui.detail.ComponentListFragment;
import com.ghstudios.android.ui.detail.WeaponBladeDetailFragment;
import com.ghstudios.android.ui.detail.WeaponBowDetailFragment;
import com.ghstudios.android.ui.detail.WeaponBowgunDetailFragment;
import com.ghstudios.android.ui.detail.WeaponSongFragment;
import com.ghstudios.android.ui.detail.WeaponTreeFragment;

public class WeaponDetailPagerAdapter extends FragmentPagerAdapter {
	
	private long weaponId;
    private Context mcontext;
	String wtype;

    // Tab titles
    private String[] tabs = { "Detail", "Family Tree", "Components"};

	public WeaponDetailPagerAdapter(FragmentManager fm, Context context, long id, String wtype) {
		super(fm);
		this.weaponId = id;
        this.mcontext = context;
		this.wtype = wtype;

		if(wtype.equals("Hunting Horn")){
			tabs = new String[]{"Detail","Melodies","Family Tree","Components"};
		}

	}

	@Override
	public Fragment getItem(int index) {

        //String wtype = DataManager.get(mcontext).getWeapon(weaponId).getWtype();

		switch (tabs[index]) {
		case "Detail":
            switch(wtype){
                case "Light Bowgun":
                case "Heavy Bowgun":
                    return WeaponBowgunDetailFragment.newInstance(weaponId);
                case "Bow":
                    return WeaponBowDetailFragment.newInstance(weaponId);
                default:
                    return WeaponBladeDetailFragment.newInstance(weaponId);
            }
		case "Family Tree":
			// Weapon tree
			return WeaponTreeFragment.newInstance(weaponId);
		case "Components":
			// Weapon Components
			return ComponentListFragment.newInstance(weaponId);
		case "Melodies":
			return WeaponSongFragment.newInstance(weaponId);
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
		// get weapon count - equal to number of tabs
		return tabs.length;
	}

}