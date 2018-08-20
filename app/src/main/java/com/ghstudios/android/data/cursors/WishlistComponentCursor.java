package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Converters;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.WishlistComponent;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "wishlist_component"
 * table. The {@link getWishlistComponent()} method will give you a WishlistComponent instance
 * representing the current row.
 */
public class WishlistComponentCursor extends CursorWrapper {

	public WishlistComponentCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a WishlistComponent object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public WishlistComponent getWishlistComponent() {
		if (isBeforeFirst() || isAfterLast())
			return null;

		WishlistComponent wishlistComponent = new WishlistComponent();
		
		long id = getLong(getColumnIndex(S.COLUMN_WISHLIST_COMPONENT_ID));
		long wishlist_id = getLong(getColumnIndex(S.COLUMN_WISHLIST_COMPONENT_WISHLIST_ID));
		int quantity = getInt(getColumnIndex(S.COLUMN_WISHLIST_COMPONENT_QUANTITY));
		int notes = getInt(getColumnIndex(S.COLUMN_WISHLIST_COMPONENT_NOTES));
		
		wishlistComponent.setId(id);
		wishlistComponent.setWishlistId(wishlist_id);
		wishlistComponent.setQuantity(quantity);
		wishlistComponent.setNotes(notes);

		// Get the Item
		Item item = new Item();
		
		long itemId = getLong(getColumnIndex(S.COLUMN_WISHLIST_COMPONENT_COMPONENT_ID));
		String itemName = getString(getColumnIndex(S.COLUMN_ITEMS_NAME));
		String type = getString(getColumnIndex(S.COLUMN_ITEMS_TYPE));
		String sub_type = getString(getColumnIndex(S.COLUMN_ITEMS_SUB_TYPE));
		int rarity = getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY));
		String fileLocation = getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME));
		item.setIconColor(getInt(getColumnIndex(S.COLUMN_ITEMS_ICON_COLOR)));

		item.setId(itemId);
		item.setName(itemName);
		item.setType(Converters.getItemTypeConverter().deserialize(type));
		item.setSubType(sub_type);
		item.setRarity(rarity);
		item.setFileLocation(fileLocation);
		
		wishlistComponent.setItem(item);
		
		return wishlistComponent;
	}

}