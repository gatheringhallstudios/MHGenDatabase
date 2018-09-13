package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.DataManager;

public class LocationLoader extends DataLoader<Location> {
	private long mLocationId;
	
	public LocationLoader(Context context, long locationId) {
		super(context);
		mLocationId = locationId;
	}
	
	@Override
	public Location loadInBackground() {
		// Query the specific location
		return DataManager.get().getLocation(mLocationId);
	}
}
