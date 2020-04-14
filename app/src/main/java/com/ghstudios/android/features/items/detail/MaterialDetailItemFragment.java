package com.ghstudios.android.features.items.detail;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemToMaterial;
import com.ghstudios.android.data.cursors.ItemToMaterialCursor;
import com.ghstudios.android.loader.ItemToMaterialListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.ItemClickListener;

/**
 * Created by Joseph on 7/7/2016.
 */
public class MaterialDetailItemFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_ITEM_ID = "COMPONENT_ID";

    public static MaterialDetailItemFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, id);
        MaterialDetailItemFragment f = new MaterialDetailItemFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.material_item_list_fragment, getArguments(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        //JOE:This list is never empty, so remove empty view to prevent flash
        View emptyView = v.findViewById(android.R.id.empty);
        ((ViewGroup)emptyView.getParent()).removeView(emptyView);
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // You only ever load the runs, so assume this is the case
        long mId = -1;
        if (args != null) {
            mId = args.getLong(ARG_ITEM_ID);
        }
        return new ItemToMaterialListCursorLoader(getActivity(), mId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Create an adapter to point at this cursor
        MaterialListCursorAdapter adapter = new MaterialListCursorAdapter(
                getActivity(), (ItemToMaterialCursor) cursor);
        setListAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    class MaterialListCursorAdapter extends CursorAdapter{
        ItemToMaterialCursor _cursor;

        public MaterialListCursorAdapter(Context c, ItemToMaterialCursor cur){
            super(c,cur,0);
            _cursor = cur;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_material_item_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ItemToMaterial mat = _cursor.GetItemToMaterial();
            Item item = mat.getItem();
            LinearLayout itemLayout = view.findViewById(R.id.listitem);
            ImageView itemImageView = view.findViewById(R.id.item_image);
            TextView itemTextView = view.findViewById(R.id.item);
            TextView amtTextView = view.findViewById(R.id.amt);

            String nameText = item.getName();
            String amtText = "" + mat.getAmount();

            itemTextView.setText(nameText);
            amtTextView.setText(amtText);

            AssetLoader.setIcon(itemImageView,item);

            itemLayout.setTag(item.getId());
            itemLayout.setOnClickListener(new ItemClickListener(context, item));
        }
    }


}
