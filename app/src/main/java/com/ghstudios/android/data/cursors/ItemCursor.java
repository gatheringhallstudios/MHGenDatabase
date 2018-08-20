package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Converters;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "items"
 * table. The getItem() method will give you an Item instance
 * representing the current row.
 */
public class ItemCursor extends CursorWrapper {

	public ItemCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns an Item object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public Item getItem() {
		if (isBeforeFirst() || isAfterLast())
			return null;

		Item item = new Item();

		long itemId = getLong(getColumnIndex(S.COLUMN_ITEMS_ID));
		String name = getString(getColumnIndex(S.COLUMN_ITEMS_NAME));
		String jpnName = getString(getColumnIndex(S.COLUMN_ITEMS_JPN_NAME));
		String type = getString(getColumnIndex(S.COLUMN_ITEMS_TYPE));
        String sub_type = getString(getColumnIndex(S.COLUMN_ITEMS_SUB_TYPE));
		int rarity = getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY));
		int carry_capacity = getInt(getColumnIndex(S.COLUMN_ITEMS_CARRY_CAPACITY));
		int buy = getInt(getColumnIndex(S.COLUMN_ITEMS_BUY));
		int sell = getInt(getColumnIndex(S.COLUMN_ITEMS_SELL));
		String description = getString(getColumnIndex(S.COLUMN_ITEMS_DESCRIPTION));
		String fileLocation = getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME));
		int color = getInt(getColumnIndex(S.COLUMN_ITEMS_ICON_COLOR));
		boolean account = getInt(getColumnIndex(S.COLUMN_ITEMS_ACCOUNT)) == 1;

		item.setId(itemId);
		item.setName(name);
		item.setJpnName(jpnName);
		item.setType(Converters.getItemTypeConverter().deserialize(type));
        item.setSubType(sub_type);
		item.setRarity(rarity);
		item.setCarryCapacity(carry_capacity);
		item.setBuy(buy);
		item.setSell(sell);
		item.setDescription(description);
		item.setFileLocation(fileLocation);
		item.setIconColor(color);
		item.setAccountItem(account);

		return item;
	}
}
