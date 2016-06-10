package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.classes.ArenaQuest;
import com.ghstudios.android.data.database.DataManager;

public class ArenaQuestLoader extends DataLoader<ArenaQuest> {
	private long mArenaQuestId;
	
	public ArenaQuestLoader(Context context, long id) {
		super(context);
		mArenaQuestId = id;
	}
	
	@Override
	public ArenaQuest loadInBackground() {
		// Query the specific arena quest
		return DataManager.get(getContext()).getArenaQuest(mArenaQuestId);
	}
}
