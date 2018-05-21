package com.ghstudios.android.features.wishlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Wishlist;
import com.ghstudios.android.data.database.WishlistCursor;
import com.ghstudios.android.loader.WishlistListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;

@SuppressLint("NewApi")
public class WishlistListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	public static final String DIALOG_WISHLIST_ADD = "wishlist_add";
	public static final String DIALOG_WISHLIST_COPY = "wishlist_copy";
	public static final String DIALOG_WISHLIST_DELETE = "wishlist_delete";
	public static final String DIALOG_WISHLIST_RENAME = "wishlist_rename";
	public static final int REQUEST_ADD = 0;
	public static final int REQUEST_RENAME = 1;
	public static final int REQUEST_COPY = 2;
	public static final int REQUEST_DELETE = 3;

    private int lastSelectionIndex = 0;
    private ActionMode mActionMode;
	private ListView mListView;
	FloatingActionButton fab;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.wishlist_list_fragment, null, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list_generic_context, container, false);
		fab = (FloatingActionButton) v.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showAddDialog();
			}
		});
		mListView = (ListView) v.findViewById(android.R.id.list);
		return v;

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		return new WishlistListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		WishlistListCursorAdapter adapter = new WishlistListCursorAdapter(
				getActivity(), (WishlistCursor) cursor);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_wishlist_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.create_new_wishlist:
				showAddDialog();
				return true;
			default:
				// Action for other existing menu items
				return super.onOptionsItemSelected(item);
			}
	}

	private void showAddDialog() {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		WishlistAddDialogFragment dialog = new WishlistAddDialogFragment();
		dialog.setTargetFragment(WishlistListFragment.this, REQUEST_ADD);
		dialog.show(fm, DIALOG_WISHLIST_ADD);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) return;
		if (requestCode == REQUEST_ADD) {
			if(data.getBooleanExtra(WishlistAddDialogFragment.EXTRA_ADD, false)) {
				updateUI();
			}
		}
		else if (requestCode == REQUEST_RENAME) { // not used here
			if(data.getBooleanExtra(WishlistRenameDialogFragment.EXTRA_RENAME, false)) {
				updateUI();
			}
		}
		else if (requestCode == REQUEST_COPY) { // might be used here
			if(data.getBooleanExtra(WishlistCopyDialogFragment.EXTRA_COPY, false)) {
				updateUI();
			}
		}
		else if (requestCode == REQUEST_DELETE) { // not used here
			if(data.getBooleanExtra(WishlistDeleteDialogFragment.EXTRA_DELETE, false)) {
				updateUI();
			}
		}
	}

	@Override
	public void onResume() {
		// Check for dataset changes when the activity is resumed. Not the best practice but the list will
		// always be small.
		super.onResume();
		// Only do this if data has been previously loaded
		if(getListView().getAdapter() != null){
			updateUI();
		}
	}

	private void updateUI() {
		getLoaderManager().getLoader( R.id.wishlist_list_fragment ).forceLoad();
		WishlistListCursorAdapter adapter = (WishlistListCursorAdapter) getListAdapter();
		adapter.notifyDataSetChanged();
	}

	private static class WishlistListCursorAdapter extends CursorAdapter {

		private WishlistCursor mWishlistCursor;

		public WishlistListCursorAdapter(Context context,
				WishlistCursor cursor) {
			super(context, cursor, 0);
			mWishlistCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_wishlistmain_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the skill for the current row
			Wishlist wishlist = mWishlistCursor.getWishlist();

			// Set up the views
			LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.listitem);
			TextView wishlistNameTextView = (TextView) view.findViewById(R.id.item_name);
			view.findViewById(R.id.item_image).setVisibility(View.GONE);

			// Bind views
			String cellText = wishlist.getName();
			wishlistNameTextView.setText(cellText);
			//itemLayout.setId((int)wishlist.getId());

			// Assign view tag and listeners
			itemLayout.setTag(wishlist.getId());
			itemLayout.setOnClickListener(new WishlistClickListener(context, wishlist.getId()));

			// Assign menu click listener if we decide to go that route
			//Toast debugmsg = Toast.makeText(c, "Long clicked ID " + id, Toast.LENGTH_SHORT);
		}
	}

	// Click listener to open Wishlist Details when a wishlist is clicked
	public static class WishlistClickListener implements View.OnClickListener {
		private Context c;
		private Long id;

		public WishlistClickListener(Context context, Long id) {
			super();
			this.id = id;
			this.c = context;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(c, WishlistDetailActivity.class);
			i.putExtra(WishlistDetailActivity.EXTRA_WISHLIST_ID, id);
			c.startActivity(i);
		}
	}
}
