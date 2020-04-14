package com.ghstudios.android.features.locations;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.cursors.LocationCursor;
import com.ghstudios.android.loader.LocationListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.LocationClickListener;

public class LocationListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.location_grid_fragment, getArguments(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_generic_list, parent,
                false);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		return new LocationListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor
		if(getListAdapter() == null) {
			setListAdapter(new LocationListCursorAdapter(getActivity(), (LocationCursor) cursor));
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class LocationListCursorAdapter extends CursorAdapter {

		private LocationCursor mLocationCursor;

		LocationListCursorAdapter(Context context, LocationCursor cursor) {
			super(context, cursor, 0);
			mLocationCursor = cursor;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_list_item_large,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the monster for the current row
			Location location = mLocationCursor.getLocation();

			RelativeLayout listLayout = view.findViewById(R.id.listitem);

			// Set up the text view
			TextView locationNameTextView = view.findViewById(R.id.item_label);
			ImageView locationImage = view.findViewById(R.id.item_image);

			String cellText = location.getName();
			locationNameTextView.setText(cellText);

			AssetLoader.setIcon(locationImage,location);

			listLayout.setTag(location.getId());
            listLayout.setOnClickListener(new LocationClickListener(context, location.getId()));
		}
	}
}
