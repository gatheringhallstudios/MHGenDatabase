package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class ComponentListCursorLoader extends SQLiteCursorLoader {
	public static String FROM_CREATED = "created";
	public static String FROM_COMPONENT = "component";
	
	private String from;	// "created" or "component"
	private long id; 		// Item id

	public ComponentListCursorLoader(Context context, String from, long id) {
		super(context);
		this.from = from;
		this.id = id;
	}

	@Override
	protected Cursor loadCursor() {
		if (from.equals(FROM_CREATED)) {
			return DataManager.get().queryComponentCreated(id);
		}
		else if(from.equals(FROM_COMPONENT)) {
			return DataManager.get().queryComponentComponent(id);
		}
		else {
			return null;
		}
	}
}
