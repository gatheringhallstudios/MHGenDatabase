package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class GatheringListCursorLoader extends SQLiteCursorLoader {
	public static String FROM_ITEM = "item";
	public static String FROM_LOCATION = "location";

	public static String RANK_LR = "LR";
	public static String RANK_HR = "HR";
	public static String RANK_G = "G";
	
	private String from;	// "item" or "location"
	private long id; 		// Item or Location id
	private String rank; 	// (For Locations only) "LR", "HR", "G", or null

	public GatheringListCursorLoader(Context context, String from, long id, String rank) {
		super(context);
		this.from = from;
		this.id = id;
		this.rank = rank;
	}

	@Override
	protected Cursor loadCursor() {
		if (from.equals(FROM_ITEM)) {
			return DataManager.get().queryGatheringItem(id);
		}
		else if(from.equals(FROM_LOCATION) && rank != null && !rank.equals("")) {
			return DataManager.get().queryGatheringLocationRank(id, rank);
		}
		else if(from.equals(FROM_LOCATION)) {
			return DataManager.get().queryGatheringLocation(id);
		}
		else {
			return null;
		}
	}
}
