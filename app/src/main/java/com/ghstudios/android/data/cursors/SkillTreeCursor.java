package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.SkillTree;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "skill_trees"
 * table. The {@link getSkillTree()} method will give you a SkillTree instance
 * representing the current row.
 */
public class SkillTreeCursor extends CursorWrapper {

	public SkillTreeCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a SkillTree object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public SkillTree getSkillTree() {
		if (isBeforeFirst() || isAfterLast())
			return null;
		
		SkillTree skillTree = new SkillTree();

		long skillTreeId = getLong(getColumnIndex(S.COLUMN_SKILL_TREES_ID));
		String name = getString(getColumnIndex(S.COLUMN_SKILL_TREES_NAME));
		//String jpnName = getString(getColumnIndex(S.COLUMN_SKILL_TREES_JPN_NAME));
		
		skillTree.setId(skillTreeId);
		skillTree.setName(name);
		//skillTree.setJpnName(jpnName);

		return skillTree;
	}
}