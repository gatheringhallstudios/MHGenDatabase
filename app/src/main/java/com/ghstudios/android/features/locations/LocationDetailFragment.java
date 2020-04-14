package com.ghstudios.android.features.locations;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.loader.LocationLoader;
import com.ghstudios.android.mhgendatabase.R;

public class LocationDetailFragment extends Fragment {
	private static final String ARG_LOCATION_ID = "LOCATION_ID";
	
	private Location mLocation;
	
	private TextView mLocationLabelTextView;
	private ImageView mLocationIconImageView;

	public static LocationDetailFragment newInstance(long locationId) {
		Bundle args = new Bundle();
		args.putLong(ARG_LOCATION_ID, locationId);
		LocationDetailFragment f = new LocationDetailFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
		
		// Check for a Item ID as an argument, and find the item
		Bundle args = getArguments();
		if (args != null) {
			long locationId = args.getLong(ARG_LOCATION_ID, -1);
			if (locationId != -1) {
				LoaderManager lm = getLoaderManager();
				lm.initLoader(R.id.location_detail_fragment, args, new LocationLoaderCallbacks());
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_location_detail, container, false);
		
		mLocationLabelTextView = view.findViewById(R.id.map_text);
		mLocationIconImageView = view.findViewById(R.id.map_image);
		
		return view;
	}
	
	private void updateUI() {
		String cellText = mLocation.getName();
		mLocationLabelTextView.setText(cellText);
		AssetLoader.setIcon(mLocationIconImageView,mLocation);
	}
	
	private class LocationLoaderCallbacks implements LoaderCallbacks<Location> {
		
		@Override
		public Loader<Location> onCreateLoader(int id, Bundle args) {
			return new LocationLoader(getActivity(), args.getLong(ARG_LOCATION_ID));
		}
		
		@Override
		public void onLoadFinished(Loader<Location> loader, Location run) {
			mLocation = run;
			updateUI();
		}
		
		@Override
		public void onLoaderReset(Loader<Location> loader) {
			// Do nothing
		}
	}
}