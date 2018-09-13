package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class SkillTreeListCursorLoader extends SQLiteCursorLoader {

	public SkillTreeListCursorLoader(Context context) {
		super(context);
	}

	@Override
	protected Cursor loadCursor() {
		// Query the list of all skill trees
		return DataManager.get().querySkillTrees();
	}
}
