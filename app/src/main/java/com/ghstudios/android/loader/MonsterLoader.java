package com.ghstudios.android.loader;

import com.ghstudios.android.data.classes.Monster;
import com.ghstudios.android.data.database.DataManager;

import android.content.Context;

public class MonsterLoader extends DataLoader<Monster> {
	private long mMonsterId;
	
	public MonsterLoader(Context context, long monsterId) {
		super(context);
		mMonsterId = monsterId;
	}
	
	@Override
	public Monster loadInBackground() {
		// Query the specific monster
		return DataManager.get(getContext()).getMonster(mMonsterId);
	}
}
