package com.ghstudios.android.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ghstudios.android.data.classes.WishlistComponent;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.data.database.WishlistComponentCursor;
import com.ghstudios.android.loader.WishlistComponentListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.ArmorClickListener;
import com.ghstudios.android.ui.ClickListeners.DecorationClickListener;
import com.ghstudios.android.ui.ClickListeners.ItemClickListener;
import com.ghstudios.android.ui.ClickListeners.MaterialClickListener;
import com.ghstudios.android.ui.ClickListeners.PalicoWeaponClickListener;
import com.ghstudios.android.ui.ClickListeners.WeaponClickListener;

import java.io.IOException;
import java.util.ArrayList;

public class WishlistDataComponentFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	public static final String EXTRA_COMPONENT_REFRESH =
			"com.daviancorp.android.ui.general.wishlist_component_refresh";
	
	private static final String ARG_ID = "ID";

	private long wishlistId;
	private ListView mListView;
	private TextView mTotalCostView;
	private ActionMode mActionMode;
	
	private boolean started, fromOtherTab;

	public static WishlistDataComponentFragment newInstance(long id) {
		Bundle args = new Bundle();
		args.putLong(ARG_ID, id);
		WishlistDataComponentFragment f = new WishlistDataComponentFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		
		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.wishlist_data_component_fragment, getArguments(), this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_wishlist_component_list, container, false);

		mListView = (ListView) v.findViewById(android.R.id.list);
		mTotalCostView = (TextView) v.findViewById(R.id.total_cost_value);
		
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            // TODO options for RENAME and DELETE wishlist still show in this fragment but are unhandled. Stop them from showing.
			default:
				return super.onOptionsItemSelected(item);
			}
	}

	private void updateUI() {
		if (started) {
			getLoaderManager().getLoader( R.id.wishlist_data_component_fragment ).forceLoad();
			WishlistComponentCursorAdapter adapter = (WishlistComponentCursorAdapter) getListAdapter();
			adapter.notifyDataSetChanged();
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
            // Update wishlist componenets with items that are 'satisfied'
            updateUI();
        }
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// The id argument will be the Item ID; CursorAdapter gives us this
		// for free

		mListView.setItemChecked(position, false);
		Intent i = null;
		long mId = (long) v.getTag();
		String itemtype;

		WishlistComponent component;

		WishlistComponentCursor mycursor = (WishlistComponentCursor) l.getItemAtPosition(position);
		component = mycursor.getWishlistComponent();
		itemtype = component.getItem().getType();

		switch(itemtype){
			case "Weapon":
				i = new Intent(getActivity(), WeaponDetailActivity.class);
				i.putExtra(WeaponDetailActivity.EXTRA_WEAPON_ID, mId);
				break;
			case "Armor":
				i = new Intent(getActivity(), ArmorDetailActivity.class);
				i.putExtra(ArmorDetailActivity.EXTRA_ARMOR_ID, mId);
				break;
			case "Decoration":
				i = new Intent(getActivity(), DecorationDetailActivity.class);
				i.putExtra(DecorationDetailActivity.EXTRA_DECORATION_ID, mId);
				break;
			default:
				i = new Intent(getActivity(), ItemDetailActivity.class);
				i.putExtra(ItemDetailActivity.EXTRA_ITEM_ID, mId);
		}

		startActivity(i);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		wishlistId = -1;
		if (args != null) {
			wishlistId = args.getLong(ARG_ID);
		}
		return new WishlistComponentListCursorLoader(getActivity(), wishlistId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		WishlistComponentCursorAdapter adapter = new WishlistComponentCursorAdapter(
				getActivity(), (WishlistComponentCursor) cursor);
		setListAdapter(adapter);
		started = true;
		
		// Show the total price
		int totalPrice = DataManager.get(getActivity()).queryWishlistPrice(wishlistId);
		mTotalCostView.setText(totalPrice + "z");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class WishlistComponentCursorAdapter extends CursorAdapter {

		private WishlistComponentCursor mWishlistComponentCursor;

		public WishlistComponentCursorAdapter(Context context, WishlistComponentCursor cursor) {
			super(context, cursor, 0);
			mWishlistComponentCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_wishlist_component_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, final Context context, Cursor cursor) {
			// Get the skill for the current row
			final WishlistComponent component = mWishlistComponentCursor.getWishlistComponent();

			// Set up the text view
			LinearLayout root = (LinearLayout) view.findViewById(R.id.listitem);
			ImageView itemImageView = (ImageView) view.findViewById(R.id.item_image);
			final TextView itemTextView = (TextView) view.findViewById(R.id.item_name);
			TextView amtTextView = (TextView) view.findViewById(R.id.text_qty_required);

			final long componentrowid = component.getId();
			final long componentid = component.getItem().getId();
			final int qtyreq = component.getQuantity();
			final int qtyhave = component.getNotes();

			// Set color component requirement is met
			if (qtyhave >= qtyreq) {
				itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color));
			} else {
				itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
			}
			
			String nameText = component.getItem().getName();
			String amtText = "" + qtyreq;

			// Assign textviews
			itemTextView.setText(nameText);
			amtTextView.setText(amtText);

			/***************** SPINNER FOR QTY_HAVE DISPLAY **************************/

			// Assign Spinner
			final Spinner spinner = (Spinner) view.findViewById(R.id.spinner_component_qty);
			// Create an ArrayAdapter containing all possible values for spinner, 0 -> quantity
			ArrayList<Integer> options = new ArrayList<>();
			for(int i = 0; i<=100; i++){
				options.add(i);
			}
			ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, R.layout.view_spinner_item, options);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(R.layout.view_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);

			AdapterView.OnItemSelectedListener onSpinner = new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(
						AdapterView<?> parent,
						View view,
						int position,
						long id) {

					// Edit qtyhave for the component's row
					DataManager.get(context).queryUpdateWishlistComponentNotes(componentrowid, position);

					// Change item color if requirement is met
					if ((Integer)spinner.getItemAtPosition(position) >= qtyreq) {
						itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color));
					} else {
						itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
					}
				}

				@Override
				public void onNothingSelected(
						AdapterView<?>  parent) {
					Log.v("ComponentFragment", "Nothing selected.");
				}
			};

			// Set spinner listener
			spinner.setOnItemSelectedListener(onSpinner);

			// Get position of notes (qty_have) and set spinner to that position
			int spinnerpos = adapter.getPosition(qtyhave);
			spinner.setSelection(spinnerpos);
			
			/********************* END SPINNER ***********************/


			// Draw image
			String cellImage = component.getItem().getItemImage();

			Drawable itemImage = null;
			try {
				itemImage = Drawable.createFromStream(
						context.getAssets().open(cellImage), null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			itemImageView.setImageDrawable(itemImage);


			// Set click listeners
			String itemtype = component.getItem().getType();
			switch(itemtype){
				case "Weapon":
					root.setOnClickListener(new WeaponClickListener(context, componentid));
					break;
				case "Armor":
					root.setOnClickListener(new ArmorClickListener(context, componentid));
					break;
				case "Decoration":
					root.setOnClickListener(new DecorationClickListener(context, componentid));
					break;
				case "Materials":
					root.setOnClickListener(new MaterialClickListener(context,componentid));
					break;
				case "Palico Weapon":
					root.setOnClickListener(new PalicoWeaponClickListener(context,componentid));
					break;
				default:
					root.setOnClickListener(new ItemClickListener(context, componentid));
					break;
			}

			root.setTag(componentid);
		}
	}
}
