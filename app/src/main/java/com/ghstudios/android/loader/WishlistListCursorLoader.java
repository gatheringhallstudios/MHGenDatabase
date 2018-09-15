package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class WishlistListCursorLoader extends SQLiteCursorLoader {

	public WishlistListCursorLoader(Context context) {
		super(context);
	}

	@Override
	protected Cursor loadCursor() {
		// Query the list of all wishlists
		return DataManager.get().queryWishlists();
	}
}
