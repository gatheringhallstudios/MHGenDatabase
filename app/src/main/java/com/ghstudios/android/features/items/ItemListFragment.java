package com.ghstudios.android.features.items;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.database.ItemCursor;
import com.ghstudios.android.loader.ItemListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.ItemClickListener;

import java.io.IOException;

public class ItemListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private ItemListCursorAdapter mAdapter;
	private String mFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.item_list_fragment, null, this);

		mFilter = "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list_search, container,false);

		EditText inputSearch = (EditText) v.findViewById(R.id.input_search);
		inputSearch.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        // When user changed the Text
		    	mFilter = cs.toString();
		    	getLoaderManager().restartLoader(0, null, ItemListFragment.this);
		    }

		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		            int arg3) {
		        // TODO Auto-generated method stub

		    }

		    @Override
		    public void afterTextChanged(Editable arg0) {
		        // TODO Auto-generated method stub
		    }
		});

		return v;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		return new ItemListCursorLoader(getActivity(), mFilter);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		mAdapter = new ItemListCursorAdapter(getActivity(), (ItemCursor) cursor);
		setListAdapter(mAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class ItemListCursorAdapter extends CursorAdapter {

		private ItemCursor mItemCursor;

		public ItemListCursorAdapter(Context context, ItemCursor cursor) {
			super(context, cursor, 0);
			mItemCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			return inflater.inflate(R.layout.fragment_item_listitem,
					parent, false);
		}



		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the item for the current row
			Item item = mItemCursor.getItem();

			// Set up the text view
            LinearLayout clickView = (LinearLayout) view.findViewById(R.id.listitem);

			TextView itemNameTextView = (TextView) view
					.findViewById(R.id.text1);
			ImageView itemImageView = (ImageView) view
					.findViewById(R.id.icon);

			String cellText = item.getName();

            String cellImage = item.getItemImage();

			itemNameTextView.setText(cellText);

			Drawable itemImage = null;

			try {
				itemImage = Drawable.createFromStream(
						context.getAssets().open(cellImage), null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			itemImageView.setImageDrawable(itemImage);
			clickView.setOnClickListener(new ItemClickListener(context, item));
		}
	}

}
