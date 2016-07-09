package com.ghstudios.android.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.WishlistDetailPagerAdapter;
import com.ghstudios.android.ui.dialog.WishlistDeleteDialogFragment;
import com.ghstudios.android.ui.dialog.WishlistRenameDialogFragment;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class WishlistDetailActivity extends GenericTabActivity {
    /**
     * A key for passing a wishlist ID as a long
     */
    public static final String EXTRA_WISHLIST_ID =
            "com.daviancorp.android.android.ui.detail.wishlist_id";


    private ViewPager viewPager;
    private WishlistDetailPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getIntent().getLongExtra(EXTRA_WISHLIST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getWishlist(id).getName());

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new WishlistDetailPagerAdapter(getSupportFragmentManager(), id);
        viewPager.setAdapter(mAdapter);

        mSlidingTabLayout.setViewPager(viewPager);

    }

    // Highlight appropriate navigation drawer item
    @Override
    protected MenuSection getSelectedSection() {
        return MenuSection.WISH_LISTS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_wishlist_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    // Implement menu actions on this wishlist
/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        else if (requestCode == REQUEST_RENAME) {
            if(data.getBooleanExtra(WishlistRenameDialogFragment.EXTRA_RENAME, false)) {
                updateUI();
            }
        }
        else if (requestCode == REQUEST_COPY) {
            if(data.getBooleanExtra(WishlistCopyDialogFragment.EXTRA_COPY, false)) {
                updateUI();
            }
        }
        else if (requestCode == REQUEST_DELETE) {
            if(data.getBooleanExtra(WishlistDeleteDialogFragment.EXTRA_DELETE, false)) {
                updateUI();
            }
        }
    }

        private boolean onItemSelected(MenuItem item, int position) {
            WishlistListCursorAdapter adapter = (WishlistListCursorAdapter) getListAdapter();
            Wishlist wishlist = ((WishlistCursor) adapter.getItem(position)).getWishlist();
            long id = wishlist.getId();
            String name = wishlist.getName();

            FragmentManager fm = getActivity().getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.menu_item_rename_wishlist:
                    WishlistRenameDialogFragment dialogRename = WishlistRenameDialogFragment.newInstance(id, name);
                    dialogRename.setTargetFragment(WishlistListFragment.this, REQUEST_RENAME);
                    dialogRename.show(fm, DIALOG_WISHLIST_RENAME);
                    return true;
                case R.id.menu_item_copy_wishlist:
                    WishlistCopyDialogFragment dialogCopy = WishlistCopyDialogFragment.newInstance(id, name);
                    dialogCopy.setTargetFragment(WishlistListFragment.this, REQUEST_COPY);
                    dialogCopy.show(fm, DIALOG_WISHLIST_COPY);
                    return true;
                case R.id.menu_item_delete_wishlist:
                    WishlistDeleteDialogFragment dialogDelete = WishlistDeleteDialogFragment.newInstance(id, name);
                    dialogDelete.setTargetFragment(WishlistListFragment.this, REQUEST_DELETE);
                    dialogDelete.show(fm, DIALOG_WISHLIST_DELETE);
                    return true;
                default:
                    return false;
            }
	}
*/

    @Override
    public void onPause() {
        super.onPause();
    }

}
