package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Combining;
import com.ghstudios.android.data.classes.Converters;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "combining"
 * table. The getCombining() method will give you a Combining instance
 * representing the current row.
 */
public class CombiningCursor extends CursorWrapper {

	public CombiningCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a Combining objects configured for the current row, or null if the
	 * current row is invalid.
	 */
	public Combining getCombining() {
		if (isBeforeFirst() || isAfterLast())
			return null;
		
		String created = "crt";
		String mat1 = "mat1";
		String mat2 = "mat2";

		Combining combining = new Combining();

		long combiningId = getLong(getColumnIndex(S.COLUMN_COMBINING_ID));
		int amount_made_min = getInt(getColumnIndex(S.COLUMN_COMBINING_AMOUNT_MADE_MIN));
		int amount_made_max = getInt(getColumnIndex(S.COLUMN_COMBINING_AMOUNT_MADE_MAX));
		int percentage = getInt(getColumnIndex(S.COLUMN_COMBINING_PERCENTAGE));
		
		combining.setId(combiningId);
		combining.setAmountMadeMin(amount_made_min);
		combining.setAmountMadeMax(amount_made_max);
		combining.setPercentage(percentage);
		
		Item created_item = itemHelper(created);	// Get the resulted Item
		Item item1 = itemHelper(mat1);				// Get the first material Item
		Item item2 = itemHelper(mat2);				// Get the second material Item

		combining.setCreatedItem(created_item);
		combining.setItem1(item1);
		combining.setItem2(item2);

		return combining;
	}
	
	/*
	 * Helper method to get the data for an Item
	 */
	private Item itemHelper(String prefix) {
		Item item = new Item();

		long item_id = getLong(getColumnIndex(prefix + S.COLUMN_ITEMS_ID));
		String item_name = getString(getColumnIndex(prefix + S.COLUMN_ITEMS_NAME));
		String item_jpnName = getString(getColumnIndex(prefix + S.COLUMN_ITEMS_JPN_NAME));
		String item_type = getString(getColumnIndex(prefix + S.COLUMN_ITEMS_TYPE));
		int item_rarity = getInt(getColumnIndex(prefix + S.COLUMN_ITEMS_RARITY));
		int item_carry_capacity = getInt(getColumnIndex(prefix + S.COLUMN_ITEMS_CARRY_CAPACITY));
		int item_buy = getInt(getColumnIndex(prefix + S.COLUMN_ITEMS_BUY));
		int item_sell = getInt(getColumnIndex(prefix + S.COLUMN_ITEMS_SELL));
		String item_description = getString(getColumnIndex(prefix + S.COLUMN_ITEMS_DESCRIPTION));
		String item_fileLocation = getString(getColumnIndex(prefix + S.COLUMN_ITEMS_ICON_NAME));
		int item_color = getInt(getColumnIndex(prefix + S.COLUMN_ITEMS_ICON_COLOR));

		item.setId(item_id);
		item.setName(item_name);
		item.setJpnName(item_jpnName);
		item.setType(Converters.getItemTypeConverter().deserialize(item_type));
		item.setRarity(item_rarity);
		item.setCarryCapacity(item_carry_capacity);
		item.setBuy(item_buy);
		item.setSell(item_sell);
		item.setDescription(item_description);
		item.setFileLocation(item_fileLocation);
		item.setIconColor(item_color);

		return item;
	}
}