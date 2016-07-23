package com.ghstudios.android.ui.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghstudios.android.data.classes.WishlistData;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.data.database.WishlistDataCursor;
import com.ghstudios.android.loader.WishlistDataListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.dialog.WishlistDataDeleteDialogFragment;
import com.ghstudios.android.ui.dialog.WishlistDataEditDialogFragment;
import com.ghstudios.android.ui.dialog.WishlistDeleteDialogFragment;
import com.ghstudios.android.ui.dialog.WishlistRenameDialogFragment;
import com.ghstudios.android.ui.list.WishlistListActivity;
import com.ghstudios.android.ui.list.WishlistListFragment;

import java.io.IOException;

public class WishlistDataDetailFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	public static final String EXTRA_DETAIL_REFRESH =
			"com.daviancorp.android.ui.general.wishlist_detail_refresh";
	
	private static final String ARG_ID = "ID";

	private static final String DIALOG_WISHLIST_DATA_EDIT = "wishlist_data_edit";
	private static final String DIALOG_WISHLIST_DATA_DELETE = "wishlist_data_delete";
	private static final int REQUEST_REFRESH = 0;
	private static final int REQUEST_EDIT = 1;
	private static final int REQUEST_DELETE = 2;

    private static final String TAG = "WishlistDataFragment";

	private boolean started, fromOtherTab;
	
	private ListView mListView;
	private ActionMode mActionMode;
	
	public static WishlistDataDetailFragment newInstance(long id) {
		Bundle args = new Bundle();
		args.putLong(ARG_ID, id);
		WishlistDataDetailFragment f = new WishlistDataDetailFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		
		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.wishlist_data_detail_fragment, getArguments(), this);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wishlist_item_list, container, false);

		mListView = (ListView) v.findViewById(android.R.id.list);

        // Use contextual action bar
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        WishlistDataMultiChoiceListener multiChoiceListener = new WishlistDataMultiChoiceListener();
        mListView.setMultiChoiceModeListener(multiChoiceListener);
		
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fm = getActivity().getSupportFragmentManager();

		switch (item.getItemId()) {
            case R.id.wishlist_rename: // Launch Rename Wishlist dialog
                WishlistRenameDialogFragment dialog = WishlistRenameDialogFragment.newInstance(
                        getArguments().getLong(ARG_ID, -1),
                        (String) getActivity().getTitle());
                dialog.setTargetFragment(this, WishlistListFragment.REQUEST_RENAME);
                dialog.show(fm, WishlistListFragment.DIALOG_WISHLIST_RENAME);
                return true;
            case R.id.wishlist_delete:// Launch Delete Wishlist dialog
                WishlistDeleteDialogFragment dialogDelete = WishlistDeleteDialogFragment.newInstance(
                        getArguments().getLong(ARG_ID, -1),
                        (String) getActivity().getTitle());
                dialogDelete.setTargetFragment(this, WishlistListFragment.REQUEST_DELETE);
                dialogDelete.show(fm, WishlistListFragment.DIALOG_WISHLIST_DELETE);
                return true;
			default:
				return super.onOptionsItemSelected(item);
			}
	}

    /********************** MULTI-SELECT ACTION BAR *******************/
    private class WishlistDataMultiChoiceListener implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
        int position, long id, boolean checked) {
            // Capture total checked items
            final int checkedCount = mListView.getCheckedItemCount();
            // Set the CAB title according to total checked items
            mode.setTitle(checkedCount + " Selected");
            // Calls toggleSelection method from ListViewAdapter Class
            // change background color to better indicate selected states
            // mListView.setItemChecked(position, true);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_item_delete_wishlist_data:

                    // Returns a hashmap of <position, boolean> where items are selected
                    SparseBooleanArray selected = mListView
                            .getCheckedItemPositions();
                    Log.v(TAG, selected.toString());
                    // Check each entry in the map where the value is true,
                    // use the key to delete each entry from the database
                    for (int i = 0; i < selected.size(); i++) {
                        if (selected.valueAt(i)) {
                            int position = selected.keyAt(i);

                            // Get _id of the wishlist item
                            WishlistDataListCursorAdapter adapter = (WishlistDataListCursorAdapter) getListAdapter();
                            WishlistData wishlistData = ((WishlistDataCursor) adapter.getItem(position)).getWishlistData();
                            long id = wishlistData.getId();

                            Log.v(TAG, "_id is " + Long.toString(id));

                            DataManager.get(getContext()).queryDeleteWishlistData(id);
                            updateUI();
                        }
                    }


                    // Close CAB
                    mode.finish();
                    return true;
                default:
                    mode.finish();
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_wishlist_data, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
                /*
                listviewadapter.removeSelection();
                */
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return false;
        }
    }


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.context_wishlist_data, menu);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Return nothing if result is failed
		if(resultCode != Activity.RESULT_OK) return;

		switch (requestCode){
			case WishlistListFragment.REQUEST_RENAME: // After wishlist is renamed
				if(data.getBooleanExtra(WishlistRenameDialogFragment.EXTRA_RENAME, false)) {
					// Cast parent activity in order to call refresh title method
					((WishlistDetailActivity) getActivity()).refreshTitle();
					updateUI();
				}
				return;
			case WishlistListFragment.REQUEST_DELETE: // After wishlist is deleted
				if(data.getBooleanExtra(WishlistDeleteDialogFragment.EXTRA_DELETE, false)) {
					// Exit current activity
                    Intent intent = new Intent(getActivity(), WishlistListActivity.class);
                    startActivity(intent);
					getActivity().finish();
				}
				return;
		}

	}
		/*		if (requestCode == WishlistListFragment.REQUEST_REFRESH) {
			if(data.getBooleanExtra(WishlistDataComponentFragment.EXTRA_COMPONENT_REFRESH, false)) {
				fromOtherTab = true;
				updateUI();
			}
		}
		else if (requestCode == WishlistListFragment.REQUEST_EDIT) {
			if(data.getBooleanExtra(WishlistDataEditDialogFragment.EXTRA_EDIT, false)) {
				updateUI();
			}
		}
		*/
	
	private void updateUI() {
		if (started) {
            // Refresh wishlist data fragment
			getLoaderManager().getLoader( R.id.wishlist_data_detail_fragment ).forceLoad();
			WishlistDataListCursorAdapter adapter = (WishlistDataListCursorAdapter) getListAdapter();
			adapter.notifyDataSetChanged();
	
			if (!fromOtherTab) {
				sendResult(Activity.RESULT_OK, true);
			}
			else {
				fromOtherTab = false;
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
        // If we are becoming visible, then...
        if (isVisibleToUser) {
            // Update wishlist with items that are 'satisfied'
            DataManager.get(getContext()).helperQueryUpdateWishlistSatisfied(getArguments().getLong(ARG_ID));
            updateUI();
        }
	}


	private void sendResult(int resultCode, boolean refresh) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		i.putExtra(EXTRA_DETAIL_REFRESH, refresh);
		
		getTargetFragment()
			.onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		
		boolean temp = onItemSelected(item, position);
		
		if(temp) {
			return true;
		}
		else {
			return super.onContextItemSelected(item);
		}
	}
	
	private boolean onItemSelected(MenuItem item, int position) {
		WishlistDataListCursorAdapter adapter = (WishlistDataListCursorAdapter) getListAdapter();
		WishlistData wishlistData = ((WishlistDataCursor) adapter.getItem(position)).getWishlistData();
		long id = wishlistData.getId();
		String name = wishlistData.getItem().getName();
		
		FragmentManager fm = getActivity().getSupportFragmentManager();
		
		switch (item.getItemId()) {
			case R.id.menu_item_edit_wishlist_data:
				WishlistDataEditDialogFragment dialogEdit = WishlistDataEditDialogFragment.newInstance(id, name);
				dialogEdit.setTargetFragment(WishlistDataDetailFragment.this, REQUEST_EDIT);
				dialogEdit.show(fm, DIALOG_WISHLIST_DATA_EDIT);
				return true;
			case R.id.menu_item_delete_wishlist_data:
				WishlistDataDeleteDialogFragment dialogDelete = WishlistDataDeleteDialogFragment.newInstance(id, name);
				dialogDelete.setTargetFragment(WishlistDataDetailFragment.this, REQUEST_DELETE);
				dialogDelete.show(fm, DIALOG_WISHLIST_DATA_DELETE);
				return true;
			default:
				return false;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long mId = -1;
		if (args != null) {
			mId = args.getLong(ARG_ID);
		}
		return new WishlistDataListCursorLoader(getActivity(), mId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		WishlistDataListCursorAdapter adapter = new WishlistDataListCursorAdapter(
				getActivity(), (WishlistDataCursor) cursor);
		setListAdapter(adapter);

		started = true;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}



	private static class WishlistDataListCursorAdapter extends CursorAdapter {

		private WishlistDataCursor mWishlistDataCursor;

		public WishlistDataListCursorAdapter(Context context, WishlistDataCursor cursor) {
			super(context, cursor, 0);
			mWishlistDataCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_wishlist_item_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the skill for the current row
			WishlistData data = mWishlistDataCursor.getWishlistData();

			// Set up the text view
			LinearLayout root = (LinearLayout) view.findViewById(R.id.listitem);
			ImageView itemImageView = (ImageView) view.findViewById(R.id.item_image);
			TextView itemTextView = (TextView) view.findViewById(R.id.item);
			TextView amtTextView = (TextView) view.findViewById(R.id.amt);
			TextView extraTextView = (TextView) view.findViewById(R.id.extra);
			
			long id = data.getItem().getId();
			String nameText = data.getItem().getName();
			String amtText = "" + data.getQuantity();
			
			String extraText = data.getPath();
			int satisfied = data.getSatisfied();

			itemTextView.setTextColor(Color.BLACK);
			if (satisfied == 1) {
				itemTextView.setTextColor(Color.GREEN);
			}
			
			// Assign textviews
			itemTextView.setText(nameText);
			amtTextView.setText(amtText);
			extraTextView.setText(extraText);

			// Draw image
			String cellImage = data.getItem().getItemImage();

			Drawable itemImage = null;
			try {
				itemImage = Drawable.createFromStream(
						context.getAssets().open(cellImage), null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			itemImageView.setImageDrawable(itemImage);

			String itemtype = data.getItem().getType();
            root.setClickable(false);

            // TODO  reenable listeners after they don't hijack longclicks
//			switch(itemtype){
//				case "Weapon":
//					root.setOnClickListener(new WeaponClickListener(context, id));
//					break;
//				case "Armor":
//					root.setOnClickListener(new ArmorClickListener(context, id));
//					break;
//				case "Decoration":
//					root.setOnClickListener(new DecorationClickListener(context, id));
//					break;
//				case "Materials":
//					root.setOnClickListener(new MaterialClickListener(context,id));
//					break;
//				case "Palico Weapon":
//					root.setOnClickListener(new PalicoWeaponClickListener(context,id));
//					break;
//				default:
//					root.setOnClickListener(new ItemClickListener(context, id));
//					break;
//			}


			root.setTag(id);
		}
	}

    // Define interface WishlistDetailActivity must implement to refresh it's title
    public interface RefreshActivityTitle{
        void refreshTitle();
    }
}
