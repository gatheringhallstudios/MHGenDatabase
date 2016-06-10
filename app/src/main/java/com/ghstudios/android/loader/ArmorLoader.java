package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.data.database.DataManager;

public class ArmorLoader extends DataLoader<Armor> {
	private long mArmorId;
	
	public ArmorLoader(Context context, long armorId) {
		super(context);
		mArmorId = armorId;
	}
	
	@Override
	public Armor loadInBackground() {
		// Query the specific armor
		return DataManager.get(getContext()).getArmor(mArmorId);
	}
}
