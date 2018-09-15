package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.data.DataManager;

public class WeaponLoader extends DataLoader<Weapon> {
	private long mWeaponId;
	
	public WeaponLoader(Context context, long weaponId) {
		super(context);
		mWeaponId = weaponId;
	}
	
	@Override
	public Weapon loadInBackground() {
		// Query the specific weapon
		return DataManager.get().getWeapon(mWeaponId);
	}
}
