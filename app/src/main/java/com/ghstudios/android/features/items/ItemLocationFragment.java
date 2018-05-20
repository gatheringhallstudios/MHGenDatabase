package com.ghstudios.android.features.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Gathering;
import com.ghstudios.android.data.database.GatheringCursor;
import com.ghstudios.android.loader.GatheringListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.LocationClickListener;
import com.github.monxalo.android.widget.SectionCursorAdapter;

public class ItemLocationFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final String ARG_ITEM_ID = "ITEM_ID";

	public static ItemLocationFragment newInstance(long itemId) {
		Bundle args = new Bundle();
		args.putLong(ARG_ITEM_ID, itemId);
		ItemLocationFragment f = new ItemLocationFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(R.id.item_location_fragment, getArguments(), this);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_generic_list, null);
		return v;
	}
	

	@SuppressLint("NewApi")
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// You only ever load the runs, so assume this is the case
		long itemId = args.getLong(ARG_ITEM_ID, -1);

		return new GatheringListCursorLoader(getActivity(), 
				GatheringListCursorLoader.FROM_ITEM, itemId, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Create an adapter to point at this cursor

		GatheringListCursorAdapter adapter = new GatheringListCursorAdapter(
				getActivity(), (GatheringCursor) cursor);
		setListAdapter(adapter);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}

	private static class GatheringListCursorAdapter extends SectionCursorAdapter {

		private GatheringCursor mGatheringCursor;

		public GatheringListCursorAdapter(Context context,
				GatheringCursor cursor) {
			super(context, cursor,R.layout.listview_generic_header,1);
			mGatheringCursor = cursor;
		}

		@Override
		protected String getCustomGroup(Cursor c) {
			Gathering g  = ((GatheringCursor)c).getGathering();
			return g.getRank() + " " + g.getLocation().getName();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_item_location_listitem,
					parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the item for the current row
			Gathering gathering = mGatheringCursor.getGathering();

			// Set up the text view
			LinearLayout itemLayout = (LinearLayout) view
					.findViewById(R.id.listitem);

			TextView mapTextView = (TextView) view.findViewById(R.id.map);
			//TextView rankTextView = (TextView) view.findViewById(R.id.rank);
			//TextView areaTextView = (TextView) view.findViewById(R.id.area);
			TextView methodTextView = (TextView) view.findViewById(R.id.method);
            TextView rateTextView = (TextView) view.findViewById(R.id.rate);
            //ImageView mapView = (ImageView) view.findViewById(R.id.map_image);

			
			String mapName = gathering.getLocation().getName();
			String rank = gathering.getRank();
			String area = gathering.getArea();
			String method = gathering.getSite();
            long rate = (long) gathering.getRate();
			
			mapTextView.setText(gathering.getArea());
			//rankTextView.setText(rank);
			//areaTextView.setText(area);
			methodTextView.setText(method);
            rateTextView.setText(Long.toString(rate) + "%");
			
			itemLayout.setTag(gathering.getLocation().getId());
            itemLayout.setOnClickListener(new LocationClickListener(context,
                    gathering.getLocation().getId()));

            //This code is too slow, needs async
            /*
            Drawable i = null;
            String cellImage = "icons_location/"
                    + gathering.getLocation().getFileLocation();
            try {
                i = Drawable.createFromStream(
                        context.getAssets().open(cellImage), null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mapView.setImageDrawable(i);
            */

		}
	}

}
