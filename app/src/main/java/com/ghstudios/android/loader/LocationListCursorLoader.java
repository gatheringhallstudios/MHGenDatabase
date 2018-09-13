package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class LocationListCursorLoader extends SQLiteCursorLoader {

	public LocationListCursorLoader(Context context) {
		super(context);
	}

	@Override
	protected Cursor loadCursor() {
		// Query the list of all locations
		return DataManager.get().queryLocations();
	}
}
