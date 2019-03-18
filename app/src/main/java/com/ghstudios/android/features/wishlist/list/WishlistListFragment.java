package com.ghstudios.android.features.wishlist.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghstudios.android.ClickListeners.WishlistClickListener;
import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.data.classes.Wishlist;
import com.ghstudios.android.features.wishlist.detail.WishlistDeleteDialogFragment;
import com.ghstudios.android.features.wishlist.detail.WishlistRenameDialogFragment;
import com.ghstudios.android.mhgendatabase.R;

import java.util.List;

public class WishlistListFragment extends ListFragment {

    public static final String DIALOG_WISHLIST_ADD = "wishlist_add";
    public static final String DIALOG_WISHLIST_COPY = "wishlist_copy";
    public static final String DIALOG_WISHLIST_DELETE = "wishlist_delete";
    public static final String DIALOG_WISHLIST_RENAME = "wishlist_rename";
    public static final int REQUEST_ADD = 0;
    public static final int REQUEST_RENAME = 1;
    public static final int REQUEST_COPY = 2;
    public static final int REQUEST_DELETE = 3;

    private ListView mListView;
    FloatingActionButton fab;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_generic_context, container, false);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
        mListView = (ListView) v.findViewById(android.R.id.list);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_wishlist_list, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_new_wishlist:
                showAddDialog();
                return true;
            default:
                // Action for other existing menu items
                return super.onOptionsItemSelected(item);
            }
    }

    private void showAddDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        WishlistAddDialogFragment dialog = new WishlistAddDialogFragment();
        dialog.setTargetFragment(WishlistListFragment.this, REQUEST_ADD);
        dialog.show(fm, DIALOG_WISHLIST_ADD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_ADD) {
            if(data.getBooleanExtra(WishlistAddDialogFragment.EXTRA_ADD, false)) {
                updateUI();
            }
        }
        else if (requestCode == REQUEST_RENAME) { // not used here
            if(data.getBooleanExtra(WishlistRenameDialogFragment.EXTRA_RENAME, false)) {
                updateUI();
            }
        }
        else if (requestCode == REQUEST_COPY) { // might be used here
            if(data.getBooleanExtra(WishlistCopyDialogFragment.EXTRA_COPY, false)) {
                updateUI();
            }
        }
        else if (requestCode == REQUEST_DELETE) { // not used here
            if(data.getBooleanExtra(WishlistDeleteDialogFragment.EXTRA_DELETE, false)) {
                updateUI();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for dataset changes when the activity is resumed.
        // Not the best practice but the list will always be small.
        // Only do this if data has been previously loaded.
        if(mListView.getAdapter() != null){
            updateUI();
        }
    }

    private void updateUI() {
        List<Wishlist> wishlists = DataManager.get().getWishlistManager().getWishlists();
        WishlistListArrayAdapter adapter = new WishlistListArrayAdapter(getActivity(), wishlists);
        mListView.setAdapter(adapter);
    }

    private static class WishlistListArrayAdapter extends ArrayAdapter<Wishlist> {
        WishlistListArrayAdapter(Context context, List<Wishlist> items) {
            super(context, 0, items);
        }

        @Override @NonNull public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Context ctx = this.getContext();
            LayoutInflater inflater = LayoutInflater.from(ctx);

            // Inflate the view (or reuse convert view if its non-null)
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_wishlistmain_listitem, parent, false);
            }

            Wishlist wishlist = this.getItem(position);

            // Set up the views
            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.listitem);
            TextView wishlistNameTextView = (TextView) view.findViewById(R.id.item_name);
            view.findViewById(R.id.item_image).setVisibility(View.GONE);

            // Bind views
            String cellText = wishlist.getName();
            wishlistNameTextView.setText(cellText);
            //itemLayout.setId((int)wishlist.getId());

            // Assign view tag and listeners
            itemLayout.setTag(wishlist.getId());
            itemLayout.setOnClickListener(new WishlistClickListener(ctx, wishlist.getId()));

            // Assign menu click listener if we decide to go that route
            //Toast debugmsg = Toast.makeText(c, "Long clicked ID " + id, Toast.LENGTH_SHORT);

            return view;
        }
    }
}
