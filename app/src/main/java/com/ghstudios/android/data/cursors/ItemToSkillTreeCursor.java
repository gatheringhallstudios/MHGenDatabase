package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemToSkillTree;
import com.ghstudios.android.data.classes.SkillTree;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "item_to_skill_tree"
 * table. The {@link getItemToSkillTree()} method will give you a ItemToSkillTree instance
 * representing the current row.
 */
public class ItemToSkillTreeCursor extends CursorWrapper {

	public ItemToSkillTreeCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a ItemToSkillTree object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public ItemToSkillTree getItemToSkillTree() {
		if (isBeforeFirst() || isAfterLast())
			return null;

		ItemToSkillTree itemToSkillTree = new ItemToSkillTree();
		
		long id = getLong(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_ID));
		int points = getInt(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE));
		
		itemToSkillTree.setId(id);
		itemToSkillTree.setPoints(points);

		// Get the Item
		Item item = new Item();
		
		long itemId = getLong(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID));
		String itemName = getString(getColumnIndex("i" + S.COLUMN_ITEMS_NAME));
		String type = getString(getColumnIndex(S.COLUMN_ITEMS_TYPE));
		int rarity = getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY));
		String fileLocation = getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME));

		item.setId(itemId);
		item.setName(itemName);
		item.setType(type);
		item.setRarity(rarity);
		item.setFileLocation(fileLocation);
		
		itemToSkillTree.setItem(item);
		
		// Get the SkillTree
		SkillTree skillTree = new SkillTree();

		long skillTreeId = getLong(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID));
		String skillTreeName = getString(getColumnIndex("s" + S.COLUMN_SKILL_TREES_NAME));

		skillTree.setId(skillTreeId);
		skillTree.setName(skillTreeName);
		
		itemToSkillTree.setSkillTree(skillTree);
		
		return itemToSkillTree;
	}

}