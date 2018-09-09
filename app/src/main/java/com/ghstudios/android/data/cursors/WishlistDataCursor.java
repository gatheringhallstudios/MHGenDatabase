package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Converters;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.WishlistData;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "wishlist_data"
 * table. The {@link getWishlistData()} method will give you a WishlistData instance
 * representing the current row.
 */
public class WishlistDataCursor extends CursorWrapper {

	public WishlistDataCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a WishlistData object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public WishlistData getWishlistData() {
		if (isBeforeFirst() || isAfterLast())
			return null;

		WishlistData wishlistData = new WishlistData();
		
		long id = getLong(getColumnIndex(S.COLUMN_WISHLIST_DATA_ID));
		long wishlist_id = getLong(getColumnIndex(S.COLUMN_WISHLIST_DATA_WISHLIST_ID));
		int quantity = getInt(getColumnIndex(S.COLUMN_WISHLIST_DATA_QUANTITY));
		int satisfied = getInt(getColumnIndex(S.COLUMN_WISHLIST_DATA_SATISFIED));
		String path = getString(getColumnIndex(S.COLUMN_WISHLIST_DATA_PATH));
		
		wishlistData.setId(id);
		wishlistData.setWishlistId(wishlist_id);
		wishlistData.setQuantity(quantity);
		wishlistData.setSatisfied(satisfied);
		wishlistData.setPath(path);

		// Get the Item
		Item item = new Item();
		
		long itemId = getLong(getColumnIndex(S.COLUMN_WISHLIST_DATA_ITEM_ID));
		String itemName = getString(getColumnIndex(S.COLUMN_ITEMS_NAME));
		String type = getString(getColumnIndex(S.COLUMN_ITEMS_TYPE));
		String sub_type = getString(getColumnIndex(S.COLUMN_ITEMS_SUB_TYPE));
		int rarity = getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY));
		int buy = getInt(getColumnIndex(S.COLUMN_ITEMS_BUY));
		String fileLocation = getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME));

		item.setId(itemId);
		item.setName(itemName);
		item.setType(Converters.getItemTypeConverter().deserialize(type));
		item.setSubType(sub_type);
		item.setRarity(rarity);
		item.setBuy(buy);
		item.setFileLocation(fileLocation);
		item.setIconColor(getInt(getColumnIndex(S.COLUMN_ITEMS_ICON_COLOR)));
		
		wishlistData.setItem(item);
		
		return wishlistData;
	}

}