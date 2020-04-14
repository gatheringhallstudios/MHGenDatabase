package com.ghstudios.android.components;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.database.S;
import com.ghstudios.android.ClickListeners.ItemClickListener;
import com.github.monxalo.android.widget.SectionCursorAdapter;
import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.cursors.ComponentCursor;
import com.ghstudios.android.loader.ComponentListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;

// this is a general component fragment maybe. Don't move until we know what to do with it
public class ComponentListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final String ARG_ITEM_ID = "COMPONENT_ID";

	public static ComponentListFragment newInstance(long id) {
		Bundle args = new Bundle();
		args.putLong(ARG_ITEM_ID, id);
		ComponentListFragment f = new ComponentListFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.component_list_fragment, getArguments(), this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list, null);
		return v;
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long mId = -1;
		if (args != null) {
			mId = args.getLong(ARG_ITEM_ID);
		}
		return new ComponentListCursorLoader(getActivity(), 
				ComponentListCursorLoader.FROM_CREATED, mId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		ComponentListCursorAdapter adapter = new ComponentListCursorAdapter(
				getActivity(), (ComponentCursor) cursor);
		setListAdapter(adapter);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class ComponentListCursorAdapter extends SectionCursorAdapter {

		private ComponentCursor mComponentCursor;

		public ComponentListCursorAdapter(Context context, ComponentCursor cursor) {
			super(context, cursor, R.layout.listview_generic_header,cursor.getColumnIndex(S.COLUMN_COMPONENTS_TYPE));
			mComponentCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_component_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the skill for the current row
			Component component = mComponentCursor.getComponent();
			Item item = component.getComponent();

			// Set up the text view
			LinearLayout itemLayout = view.findViewById(R.id.listitem);
			ImageView itemImageView = view.findViewById(R.id.item_image);
			TextView itemTextView = view.findViewById(R.id.item);
			TextView amtTextView = view.findViewById(R.id.amt);
			
			String nameText = item.getName();
			String amtText = "" + component.getQuantity();
			
			itemTextView.setText(nameText);
			amtTextView.setText(amtText);

			AssetLoader.setIcon(itemImageView,item);

			itemLayout.setTag(item.getId());
			itemLayout.setOnClickListener(new ItemClickListener(context, item));
		}
	}

}
