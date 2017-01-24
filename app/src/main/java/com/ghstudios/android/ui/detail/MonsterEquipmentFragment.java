package com.ghstudios.android.ui.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.MonsterEquipment;
import com.ghstudios.android.data.database.ItemCursor;
import com.ghstudios.android.data.database.MonsterEquipmentCursor;
import com.ghstudios.android.data.database.S;
import com.ghstudios.android.loader.MonsterEquipmentListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.ArmorClickListener;
import com.ghstudios.android.ui.ClickListeners.DecorationClickListener;
import com.ghstudios.android.ui.ClickListeners.ItemClickListener;
import com.ghstudios.android.ui.ClickListeners.MaterialClickListener;
import com.ghstudios.android.ui.ClickListeners.PalicoWeaponClickListener;
import com.ghstudios.android.ui.ClickListeners.WeaponClickListener;
import com.github.monxalo.android.widget.SectionCursorAdapter;

import java.io.IOException;

/**
 * Created by Carlos on 8/21/2016.
 */
public class MonsterEquipmentFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_MONSTER_ID = "MONSTER_ID";

    public static MonsterEquipmentFragment newInstance(long monsterId) {
        Bundle args = new Bundle();
        args.putLong(ARG_MONSTER_ID, monsterId);
        MonsterEquipmentFragment f = new MonsterEquipmentFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.item_list_fragment, getArguments(), this);
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
        long monsterId = args.getLong(ARG_MONSTER_ID, -1);
        return new MonsterEquipmentListCursorLoader(getActivity(), monsterId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        MonsterEquipmentCursorAdapter adapter = new MonsterEquipmentCursorAdapter(
                getActivity(), (MonsterEquipmentCursor) cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        setListAdapter(null);
    }

    private static class MonsterEquipmentCursorAdapter extends SectionCursorAdapter {
        MonsterEquipmentCursor mEquipmentCursor;

        public MonsterEquipmentCursorAdapter(Context context, MonsterEquipmentCursor cursor) {
            super(context, cursor, R.layout.listview_generic_header, S.COLUMN_HUNTING_REWARDS_RANK);
            mEquipmentCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_item_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the item for the current row
            MonsterEquipment monsterEquipment = mEquipmentCursor.getMonsterItem();
            Item item = monsterEquipment.getItem();

            // Set up the text view
            LinearLayout clickView = (LinearLayout) view.findViewById(R.id.listitem);

            TextView itemNameTextView = (TextView) view.findViewById(R.id.text1);
            ImageView itemImageView = (ImageView) view.findViewById(R.id.icon);

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
