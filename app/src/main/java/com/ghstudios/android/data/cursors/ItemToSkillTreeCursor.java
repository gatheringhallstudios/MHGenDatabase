package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Converters;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemToSkillTree;
import com.ghstudios.android.data.classes.SkillTree;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "item_to_skill_tree"
 * table. The getItemToSkillTree() method will give you an ItemToSkillTree instance
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

		// Get the SkillTree, ItemToSkillTree requires a SkillTree to exist before construction

		long skillTreeId = getLong(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID));
		String skillTreeName = getString(getColumnIndex("s" + S.COLUMN_SKILL_TREES_NAME));
		int skillTreePoints = getInt(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE));

		SkillTree skillTree = new SkillTree();
		skillTree.setId(skillTreeId);
		skillTree.setName(skillTreeName);

		// Create the ItemToSkillTree
		ItemToSkillTree itemToSkillTree = new ItemToSkillTree(skillTree, skillTreePoints);
		
		long id = getLong(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_ID));
		itemToSkillTree.setId(id);

		// Get the Item
		Item item = new Item();
		
		long itemId = getLong(getColumnIndex(S.COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID));
		String itemName = getString(getColumnIndex("i" + S.COLUMN_ITEMS_NAME));
		String type = getString(getColumnIndex(S.COLUMN_ITEMS_TYPE));
		int rarity = getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY));
		String fileLocation = getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME));

		item.setId(itemId);
		item.setName(itemName);
		item.setType(Converters.getItemTypeConverter().deserialize(type));
		item.setRarity(rarity);
		item.setFileLocation(fileLocation);
		item.setIconColor(getInt(getColumnIndex(S.COLUMN_ITEMS_ICON_COLOR)));
		
		itemToSkillTree.setItem(item);
		
		return itemToSkillTree;
	}

}