package com.ghstudios.android.loader;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.data.DataManager;

public class WishlistComponentListCursorLoader extends SQLiteCursorLoader {

	private long id;		// Wishlist id
	
	public WishlistComponentListCursorLoader(Context context, long id) {
		super(context);
		this.id = id;
	}

	@Override
	protected Cursor loadCursor() {
		// Query the list of wishlist components based on wishlist
		return DataManager.get().queryWishlistComponents(id);
	}
}
