package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Monster;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "monsters"
 * table. The {@link getMonster()} method will give you a Monster instance
 * representing the current row.
 */
public class MonsterCursor extends CursorWrapper {

	public MonsterCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a Monster object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public Monster getMonster() {
		if (isBeforeFirst() || isAfterLast())
			return null;
		
		Monster monster = new Monster();

		long monsterId = getLong(getColumnIndex(S.COLUMN_MONSTERS_ID));
		String name = getString(getColumnIndex(S.COLUMN_MONSTERS_NAME));
		String jpnName = getString(getColumnIndex(S.COLUMN_MONSTERS_JPN_NAME));
		String monsterClass = getString(getColumnIndex(S.COLUMN_MONSTERS_CLASS));
		String trait = getString(getColumnIndex(S.COLUMN_MONSTERS_TRAIT));
		String file_location = getString(getColumnIndex(S.COLUMN_MONSTERS_FILE_LOCATION));
		String signature_move = getString(getColumnIndex(S.COLUMN_MONSTERS_SIGNATURE_MOVE));
		
		monster.setId(monsterId);
		monster.setName(name);
		monster.setJpnName(jpnName);
		monster.setMonsterClass(monsterClass);
		monster.setTrait(trait);
		monster.setFileLocation(file_location);
		monster.setSignatureMove(signature_move);

		return monster;
	}
}