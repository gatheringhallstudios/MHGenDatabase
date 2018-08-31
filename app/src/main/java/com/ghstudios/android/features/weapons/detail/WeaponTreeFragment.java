package com.ghstudios.android.features.weapons.detail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghstudios.android.adapter.WeaponExpandableListBladeAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBowAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBowgunAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListGeneralAdapter;
import com.ghstudios.android.components.WeaponListEntry;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.data.cursors.WeaponCursor;
import com.ghstudios.android.loader.WeaponTreeListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.WeaponClickListener;

public class WeaponTreeFragment extends ListFragment implements
        LoaderCallbacks<Cursor> {
    private static final String ARG_WEAPON_ID = "WEAPON_ID";
    private long mWeaponId;
    
    public static WeaponTreeFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(ARG_WEAPON_ID, weaponId);
        WeaponTreeFragment f = new WeaponTreeFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.weapon_tree_fragment, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // You only ever load the runs, so assume this is the case
        mWeaponId = args.getLong(ARG_WEAPON_ID, -1);
        
        return new WeaponTreeListCursorLoader(getActivity(), mWeaponId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Create an adapter to point at this cursor
        WeaponTreeListCursorAdapter adapter = new WeaponTreeListCursorAdapter(
                getActivity(), (WeaponCursor) cursor, mWeaponId);
        setListAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    private static class WeaponTreeListCursorAdapter extends CursorAdapter {

        private WeaponCursor mWeaponCursor;
        private long weaponId;

        public WeaponTreeListCursorAdapter(Context context, WeaponCursor cursor, long id) {
            super(context, cursor, 0);
            mWeaponCursor = cursor;
            weaponId = id;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v;

            Weapon w = mWeaponCursor.getWeapon();
            if (w.getWtype().equals(Weapon.BOW)) {
                v = inflater.inflate(R.layout.fragment_weapon_tree_item_bow, parent, false);
                v.setTag(new WeaponExpandableListBowAdapter.WeaponBowViewHolder(v));
            }
            else if (w.getWtype().equals(Weapon.HEAVY_BOWGUN) || w.getWtype().equals(Weapon.LIGHT_BOWGUN)) {
                v = inflater.inflate(R.layout.fragment_weapon_tree_item_bowgun, parent, false);
                v.setTag(new WeaponExpandableListBowgunAdapter.WeaponBowgunViewHolder(v));
            }
            else {
                v = inflater.inflate(R.layout.fragment_weapon_tree_item_blademaster, parent, false);
                v.setTag(new WeaponExpandableListBladeAdapter.WeaponBladeViewHolder(v));
            }
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the weapon for the current row
            Weapon weapon = mWeaponCursor.getWeapon();
            WeaponListEntry entry = new WeaponListEntry(weapon);
            ((WeaponExpandableListGeneralAdapter.WeaponViewHolder) view.getTag()).bindView(context,entry);
        }
    }

}
