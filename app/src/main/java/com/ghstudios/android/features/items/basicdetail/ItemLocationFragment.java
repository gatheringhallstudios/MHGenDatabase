package com.ghstudios.android.features.items.basicdetail;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Gathering;
import com.ghstudios.android.data.cursors.GatheringCursor;
import com.ghstudios.android.features.items.ItemDetailViewModel;
import com.ghstudios.android.loader.GatheringListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.LocationClickListener;
import com.github.monxalo.android.widget.SectionCursorAdapter;

public class ItemLocationFragment extends ListFragment {

    private static final String ARG_ITEM_ID = "ITEM_ID";

    public static ItemLocationFragment newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        ItemLocationFragment f = new ItemLocationFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ItemDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(ItemDetailViewModel.class);
        viewModel.getGatherData().observe(this, (cursor) -> {
            GatheringListCursorAdapter adapter = new GatheringListCursorAdapter(
                    getActivity(), cursor);
            setListAdapter(adapter);
        });
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
            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.fragment_item_location_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the item for the current row
            Gathering gathering = mGatheringCursor.getGathering();

            // Set up the text view
            LinearLayout itemLayout = view.findViewById(R.id.listitem);

            TextView mapTextView = view.findViewById(R.id.map);
            TextView methodTextView = view.findViewById(R.id.method);
            TextView rateTextView = view.findViewById(R.id.rate);

            String method = gathering.getSite();
            long rate = (long) gathering.getRate();
            
            mapTextView.setText(gathering.getArea());
            methodTextView.setText(method);
            rateTextView.setText(Long.toString(rate) + "%");
            
            itemLayout.setTag(gathering.getLocation().getId());
            itemLayout.setOnClickListener(new LocationClickListener(context,
                    gathering.getLocation().getId()));
        }
    }

}
