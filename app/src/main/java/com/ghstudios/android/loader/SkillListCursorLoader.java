package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class SkillListCursorLoader extends SQLiteCursorLoader {
	private long id; 		// Skill Tree id

	public SkillListCursorLoader(Context context, long id) {
		super(context);
		this.id = id;
	}

	@Override
	protected Cursor loadCursor() {
		// Query the list of skills from a skill tree
		return DataManager.get().querySkillsFromTree(id);
	}
}
