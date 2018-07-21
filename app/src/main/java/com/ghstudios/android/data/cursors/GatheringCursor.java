package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Gathering;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "gathering"
 * table. The getGathering() method will give you a Gathering instance
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

		String area = getString(S.COLUMN_GATHERING_AREA);
		String site = getString(S.COLUMN_GATHERING_SITE);
		String rank = getString(S.COLUMN_GATHERING_RANK);
        Float rate = (float)getInt(S.COLUMN_GATHERING_RATE);
		int group = getInt(S.COLUMN_GATHERING_GROUP);
		int fixed = getInt(S.COLUMN_GATHERING_FIXED);
		int rare = getInt(S.COLUMN_GATHERING_RARE);
		int quantity = getInt(S.COLUMN_GATHERING_QUANTITY,1);

		gathering.setArea(area);
		gathering.setSite(site);
		gathering.setRank(rank);
        gathering.setRate(rate);
		gathering.setGroup(group);
		gathering.setFixed(fixed == 1);
		gathering.setRare(rare == 1);
		gathering.setQuantity(quantity);
		
		// Get the Item
		Item item = new Item();
		
		long itemId = getLong(S.COLUMN_GATHERING_ITEM_ID,-1);
		String itemName = getString("i" + S.COLUMN_ITEMS_NAME);
		String fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME);


		item.setId(itemId);
		item.setName(itemName);
		item.setFileLocation(fileLocation);
		
		gathering.setItem(item);

		// Get the Location
		Location location = new Location();

		long locationId = getLong(S.COLUMN_GATHERING_LOCATION_ID,-1);
		String locationName = getString("l" + S.COLUMN_LOCATIONS_NAME);
	    String fileLocationLoc = getString("l" + S.COLUMN_LOCATIONS_MAP);

		location.setId(locationId);
		location.setName(locationName);
        location.setFileLocation(fileLocationLoc);
		
		gathering.setLocation(location);
		
		return gathering;
	}

	private String getString(String columnName){
		return getString(columnName,"");
	}

	private String getString(String columnName, String defaultValue){
		int columnIndex = getColumnIndex(columnName);
		if(columnIndex == -1) return defaultValue;
		return getString(columnIndex);
	}

	private int getInt(String columnName){ return getInt(columnName,0);}

	private int getInt(String columnName, int defaultValue){
		int columnIndex = getColumnIndex(columnName);
		if(columnIndex == -1) return defaultValue;
		return getInt(columnIndex);
	}

	private long getLong(String columnName){ return getLong(columnName,0);}

	private long getLong(String columnName, long defaultValue){
		int columnIndex = getColumnIndex(columnName);
		if(columnIndex == -1) return defaultValue;
		return getLong(columnIndex);
	}

}