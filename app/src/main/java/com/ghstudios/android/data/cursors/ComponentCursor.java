package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Converters;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.database.S;

/**
 * A convenience class to wrap a cursor that returns rows from the "component"
 * table. The getComponent() method will give you a Component instance
 * representing the current row.
 */
public class ComponentCursor extends CursorWrapper {

	public ComponentCursor(Cursor c) {
		super(c);
	}

	/**
	 * Returns a Component object configured for the current row, or null if the
	 * current row is invalid.
	 */

	public Component getComponent() {
		if (isBeforeFirst() || isAfterLast())
			return null;

		Component component = new Component();
		
		long id = getLong(getColumnIndex(S.COLUMN_COMPONENTS_ID));
		int quantity = getInt(getColumnIndex(S.COLUMN_COMPONENTS_QUANTITY));
		String ctype = getString(getColumnIndex(S.COLUMN_COMPONENTS_TYPE));

		int keyI = getColumnIndex(S.COLUMN_COMPONENTS_KEY);
		if(keyI != -1)
			component.setKey(getInt(keyI));
		
		component.setId(id);
		component.setQuantity(quantity);
		component.setType(ctype);

		// Get the created Item
		Item created = new Item();

		long itemId1 = getLong(getColumnIndex(S.COLUMN_COMPONENTS_CREATED_ITEM_ID));
		String itemName1 = getString(getColumnIndex("cr" + S.COLUMN_ITEMS_NAME));
		String type1 = getString(getColumnIndex("cr" + S.COLUMN_ITEMS_TYPE));
		int rarity1 = getInt(getColumnIndex("cr" + S.COLUMN_ITEMS_RARITY));
		String fileLocation1 = getString(getColumnIndex("cr" + S.COLUMN_ITEMS_ICON_NAME));
		String subtype = getString(getColumnIndex("cr" + S.COLUMN_ITEMS_SUB_TYPE));
		int color = getInt(getColumnIndex("cr"+S.COLUMN_ITEMS_ICON_COLOR));


		created.setId(itemId1);
		created.setName(itemName1);
        created.setSubType(subtype);
        created.setType(Converters.getItemTypeConverter().deserialize(type1));
        created.setRarity(rarity1);
        created.setFileLocation(fileLocation1);
        created.setIconColor(color);
		
		component.setCreated(created);

		// Get the component Item
		Item comp = new Item();
		
		long itemId2 = getLong(getColumnIndex(S.COLUMN_COMPONENTS_COMPONENT_ITEM_ID));
		String itemName2 = getString(getColumnIndex("co" + S.COLUMN_ITEMS_NAME));
        String itemType2 = getString(getColumnIndex("co" + S.COLUMN_ITEMS_TYPE));
        int rarity2 = getInt(getColumnIndex("co" + S.COLUMN_ITEMS_RARITY));
        String fileLocation2 = getString(getColumnIndex("co" + S.COLUMN_ITEMS_ICON_NAME));
        String subtype2 = getString(getColumnIndex("co" + S.COLUMN_ITEMS_SUB_TYPE));
		color = getInt(getColumnIndex("co"+S.COLUMN_ITEMS_ICON_COLOR));

		comp.setId(itemId2);
		comp.setName(itemName2);
        comp.setSubType(subtype2);
        comp.setRarity(rarity2);
        comp.setType(Converters.getItemTypeConverter().deserialize(itemType2));
        comp.setFileLocation(fileLocation2);
        comp.setIconColor(color);
		
		component.setComponent(comp);
		
		return component;
	}

}