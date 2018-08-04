package com.ghstudios.android.features.weapons.detail;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.MHUtils;
import com.ghstudios.android.data.classes.Melody;
import com.ghstudios.android.data.cursors.HornMelodiesCursor;
import com.ghstudios.android.loader.HornMelodyListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joseph on 7/1/2016.
 */
public class WeaponSongFragment extends ListFragment implements
        LoaderCallbacks<Cursor> {
    private static final String ARG_WEAPON_ID = "WEAPON_ID";
    private long mWeaponId;

    public static WeaponSongFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(ARG_WEAPON_ID, weaponId);
        WeaponSongFragment f = new WeaponSongFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.horn_melodies_list_fragment, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // You only ever load the runs, so assume this is the case
        mWeaponId = args.getLong(ARG_WEAPON_ID, -1);

        return new HornMelodyListCursorLoader(getActivity(), mWeaponId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Create an adapter to point at this cursor
        HornMelodiesCursorAdapter adapter = new HornMelodiesCursorAdapter(
                getActivity(), (HornMelodiesCursor) cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    public static class HornMelodiesCursorAdapter extends CursorAdapter {

        private HornMelodiesCursor mHornMelodiesCursor;

        public HornMelodiesCursorAdapter(Context context,
                                         HornMelodiesCursor cursor) {
            super(context, cursor, 0);
            mHornMelodiesCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_horn_melody_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the Melody for the current row
            Melody melody = mHornMelodiesCursor.getMelody();

            // Get assignable TextViews
            TextView effect1TextView = view.findViewById(R.id.effect1);
            TextView effect2TextView = view.findViewById(R.id.effect2);
            TextView durationTextView = view.findViewById(R.id.duration);
            TextView extensionTextView = view.findViewById(R.id.extension);

            // Get assignable ImageViews
            ImageView note1ImageView = view.findViewById(R.id.horn_note1);
            ImageView note2ImageView = view.findViewById(R.id.horn_note2);
            ImageView note3ImageView = view.findViewById(R.id.horn_note3);
            ImageView note4ImageView = view.findViewById(R.id.horn_note4);

            // Assign Effect 1
            String cellText = melody.getEffect1();
            effect1TextView.setText(cellText);

            // Assign Effect 2
            cellText = melody.getEffect2();
            if(!cellText.equals("N/A")) {
                effect2TextView.setText(cellText);
                effect2TextView.setVisibility(View.VISIBLE);
            } else {
                effect2TextView.setVisibility(View.GONE);
            }

            // Assign Duration
            cellText = "DUR: " + melody.getDuration();
            if(!cellText.equals("DUR: N/A")) {
                durationTextView.setText(cellText);
                durationTextView.setVisibility(View.VISIBLE);
            } else {
                durationTextView.setVisibility(View.GONE);
            }

            // Assign Extension
            cellText = "EXT: " + melody.getExtension();
            if(!cellText.equals("EXT: N/A")) {
                extensionTextView.setText(cellText);
                extensionTextView.setVisibility(View.VISIBLE);
            } else {
                extensionTextView.setVisibility(View.GONE);
            }

            // Get string version of song
            String song = melody.getSong();

            note1ImageView.setImageResource(R.drawable.icon_music_note);
            note1ImageView.setColorFilter(ContextCompat.getColor(mContext, MHUtils.getNoteColor(song.charAt(0))), PorterDuff.Mode.MULTIPLY);

            note2ImageView.setImageResource(R.drawable.icon_music_note);
            note2ImageView.setColorFilter(ContextCompat.getColor(mContext, MHUtils.getNoteColor(song.charAt(1))), PorterDuff.Mode.MULTIPLY);

            if(song.length()>2) {
                note3ImageView.setVisibility(View.VISIBLE);
                note3ImageView.setImageResource(R.drawable.icon_music_note);
                note3ImageView.setColorFilter(ContextCompat.getColor(mContext, MHUtils.getNoteColor(song.charAt(2))), PorterDuff.Mode.MULTIPLY);
            }else{
                note3ImageView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
