package com.ghstudios.android.features.locations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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
import com.ghstudios.android.data.classes.Habitat;
import com.ghstudios.android.data.cursors.MonsterHabitatCursor;
import com.ghstudios.android.loader.MonsterHabitatListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.MonsterClickListener;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationHabitatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationHabitatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationHabitatFragment extends ListFragment implements
        LoaderCallbacks<Cursor> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LOCATION_ID = "LOCATION_ID";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loc_id The id of the location for the fragment
     *
     * @return A new instance of fragment MonsterHabitatFragment.
     */
    public static LocationHabitatFragment newInstance(long loc_id) {
        LocationHabitatFragment fragment = new LocationHabitatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_LOCATION_ID, loc_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the loader to load the list of monsters
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @SuppressLint("NewApi")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long itemId = args.getLong(ARG_LOCATION_ID, -1);

        return new MonsterHabitatListCursorLoader(getActivity(),
                MonsterHabitatListCursorLoader.FROM_LOCATION, itemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Create an adapter to point at this cursor
        if (getListAdapter() == null) {
            MonsterHabitatCursorAdapter adapter = new MonsterHabitatCursorAdapter(
                    getActivity(), (MonsterHabitatCursor) cursor);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    private static class MonsterHabitatCursorAdapter extends CursorAdapter {

        private MonsterHabitatCursor mHabitatCursor;

        public MonsterHabitatCursorAdapter(Context context,
                                          MonsterHabitatCursor cursor) {
            super(context, cursor, 0);
            mHabitatCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_monster_habitat_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the item for the current row
            Habitat habitat = mHabitatCursor.getHabitat();

            // Set up the text view
            RelativeLayout itemLayout = view.findViewById(R.id.listitem);

            ImageView monsterImageView = view.findViewById(R.id.mapImage);
            TextView monsterTextView = view.findViewById(R.id.map);
            TextView startTextView = view.findViewById(R.id.start);
            TextView areaTextView = view.findViewById(R.id.move);
            TextView restTextView = view.findViewById(R.id.rest);

            long start = habitat.getStart();
            long[] area = habitat.getAreas();
            long rest = habitat.getRest();

            String areas = "";
            for(int i = 0; i < area.length; i++)
            {
                areas += Long.toString(area[i]);
                if (i != area.length - 1)
                {
                    areas += ", ";
                }
            }

            AssetLoader.setIcon(monsterImageView,habitat.getMonster());

            monsterTextView.setText(habitat.getMonster().getName());
            startTextView.setText(Long.toString(start));
            areaTextView.setText(areas);
            restTextView.setText(Long.toString(rest));

            // Set id of layout to location so clicking gives us the location
            itemLayout.setTag(habitat.getMonster().getId());
            itemLayout.setOnClickListener(new MonsterClickListener(context, habitat.getMonster()
                    .getId()));
        }
    }

}
