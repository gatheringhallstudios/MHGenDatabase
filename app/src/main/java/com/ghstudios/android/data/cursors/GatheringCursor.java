package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Gathering;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "gathering"
 * table. The {@link getGathering()} method will give you a Gathering instance
 * representing the current row.
 */
public class GatheringCursor extends CursorWrapper {

	public GatheringCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a Gathering object configured for the current row, or null if the
	 * current row is invalid.
	 */
	public Gathering getGathering() {
		if (isBeforeFirst() || isAfterLast())
			return null;

		Gathering gathering = new Gathering();
		
		long id = getLong(getColumnIndex(S.COLUMN_GATHERING_ID));
		String area = getString(getColumnIndex(S.COLUMN_GATHERING_AREA));
		String site = getString(getColumnIndex(S.COLUMN_GATHERING_SITE));
		String rank = getString(getColumnIndex(S.COLUMN_GATHERING_RANK));
        Float rate = getFloat(getColumnIndex(S.COLUMN_GATHERING_RATE));
		int group = getInt(getColumnIndex(S.COLUMN_GATHERING_GROUP));
		int fixed = getInt(getColumnIndex(S.COLUMN_GATHERING_FIXED));
		int rare = getInt(getColumnIndex(S.COLUMN_GATHERING_RARE));
		int quantity = getInt(getColumnIndex(S.COLUMN_GATHERING_QUANTITY));
		
		gathering.setId(id);
		gathering.setArea(area);
		gathering.setSite(site);
		gathering.setRank(rank);
        gathering.setRate(rate);
		gathering.setGroup(group);
		gathering.setFixed(fixed==1);
		gathering.setRare(rare == 1);
		gathering.setQuantity(quantity);
		
		// Get the Item
		Item item = new Item();
		
		long itemId = getLong(getColumnIndex(S.COLUMN_GATHERING_ITEM_ID));
		String itemName = getString(getColumnIndex("i" + S.COLUMN_ITEMS_NAME));
//			String jpnName = getString(getColumnIndex(S.COLUMN_ITEMS_JPN_NAME));
//			String type = getString(getColumnIndex(S.COLUMN_ITEMS_TYPE));
//			int rarity = getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY));
//			int carry_capacity = getInt(getColumnIndex(S.COLUMN_ITEMS_CARRY_CAPACITY));
//			int buy = getInt(getColumnIndex(S.COLUMN_ITEMS_BUY));
//			int sell = getInt(getColumnIndex(S.COLUMN_ITEMS_SELL));
//			String description = getString(getColumnIndex(S.COLUMN_ITEMS_DESCRIPTION));
			String fileLocation = getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME));
//			String armor_dupe_name_fix = getString(getColumnIndex(S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX));

		item.setId(itemId);
		item.setName(itemName);
//			item.setJpnName(jpnName);
//			item.setType(type);
//			item.setRarity(rarity);
//			item.setCarryCapacity(carry_capacity);
//			item.setBuy(buy);
//			item.setSell(sell);
//			item.setDescription(description);
			item.setFileLocation(fileLocation);
//			item.setArmorDupeNameFix(armor_dupe_name_fix);
		
		gathering.setItem(item);

		// Get the Location
		Location location = new Location();

		long locationId = getLong(getColumnIndex(S.COLUMN_GATHERING_LOCATION_ID));
		String locationName = getString(getColumnIndex("l" + S.COLUMN_LOCATIONS_NAME));
	    String fileLocationLoc = getString(getColumnIndex("l" + S.COLUMN_LOCATIONS_MAP));

		location.setId(locationId);
		location.setName(locationName);
        location.setFileLocation(fileLocationLoc);
		
		gathering.setLocation(location);
		
		return gathering;
	}

}