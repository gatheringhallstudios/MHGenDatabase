package com.ghstudios.android.features.wishlist;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.WishlistDetailPagerAdapter;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class WishlistDetailActivity extends GenericTabActivity implements WishlistDataDetailFragment.RefreshActivityTitle{
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

        // Set Title
        long id = getIntent().getLongExtra(EXTRA_WISHLIST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getWishlist(id).getName());

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new WishlistDetailPagerAdapter(getSupportFragmentManager(), id);
        viewPager.setAdapter(mAdapter);

        setViewPager(viewPager);

    }

    // Highlight appropriate navigation drawer item
    @Override
    protected int getSelectedSection() {
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void refreshTitle(){
        // Set again after wishlist is renamed
        long id = getIntent().getLongExtra(EXTRA_WISHLIST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getWishlist(id).getName());
    }


}
