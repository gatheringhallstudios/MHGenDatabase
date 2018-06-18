package com.ghstudios.android.features.items.basicdetail;

import java.io.IOException;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.cursors.ComponentCursor;
import com.ghstudios.android.features.items.ItemDetailViewModel;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.ItemClickListener;

/**
 * A fragment that display usages for an item
 */
public class ItemComponentFragment extends ListFragment {

    private static final String ARG_ITEM_ID = "COMPONENT_ID";

    public static ItemComponentFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, id);
        ItemComponentFragment f = new ItemComponentFragment();
        f.setArguments(args);
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);

        ItemDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(ItemDetailViewModel.class);
        viewModel.getUsageData().observe(this, (cursor) -> {
            ComponentListCursorAdapter adapter = new ComponentListCursorAdapter(
                    getActivity(), cursor);
            setListAdapter(adapter);
        });

        return v;
    }

    protected static class ComponentListCursorAdapter extends CursorAdapter {

        private ComponentCursor mComponentCursor;

        public ComponentListCursorAdapter(Context context, ComponentCursor cursor) {
            super(context, cursor, 0);
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

            // Set up the text view
            LinearLayout itemLayout = (LinearLayout) view
                    .findViewById(R.id.listitem);
            ImageView itemImageView = (ImageView) view.findViewById(R.id.item_image);
            TextView itemTextView = (TextView) view.findViewById(R.id.item);
            TextView amtTextView = (TextView) view.findViewById(R.id.amt);
            TextView typeTextView = (TextView) view.findViewById(R.id.type);
            
            Item created = component.getCreated();
            long createdId = created.getId();
            
            String nameText = created.getName();
            String amtText = "" + component.getQuantity();
            String typeText = "" + component.getType();

            itemTextView.setText(nameText);
            amtTextView.setText(amtText);
            typeTextView.setText(typeText);
            
            Drawable i = null;
            String cellImage = created.getItemImage();

            try {
                i = Drawable.createFromStream(
                        context.getAssets().open(cellImage), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            itemImageView.setImageDrawable(i);

            itemLayout.setTag(createdId);
            itemLayout.setOnClickListener(new ItemClickListener(context, created));
        }
    }

}
