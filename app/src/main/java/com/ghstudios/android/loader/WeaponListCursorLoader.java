package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

/*
 *  Refer to MonsterListPagerAdapter and MonsterListFragment on 
 *  how to call this loader
 */
public class WeaponListCursorLoader extends SQLiteCursorLoader {
	private String type; // "Great Sword", "Long Sword", "Sword and Shield",
						 // "Dual Blades", "Hammer", "Hunting Horn", "Lance",
						 // "Gunlance", "Switch Axe", "Light Bowgun",
						 // "Heavy Bowgun", "Bow"

	public WeaponListCursorLoader(Context context, String type) {
		super(context);
		this.type = type;
	}

	@Override
	protected Cursor loadCursor() {
		if (type == null) {
			// Query the list of all weapons
			return DataManager.get().queryWeapon();
		} else {
			// Query the list of weapons based on type
			return DataManager.get().queryWeaponType(type);
		}
	}
}
