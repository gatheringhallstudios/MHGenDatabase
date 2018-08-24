package com.ghstudios.android.features.wishlist.detail;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class WishlistDetailPagerActivity extends BasePagerActivity implements WishlistDataDetailFragment.RefreshActivityTitle{
    /**
     * A key for passing a wishlist ID as a long
     */
    public static final String EXTRA_WISHLIST_ID =
            "com.daviancorp.android.android.ui.detail.wishlist_id";

    private static final int REQUEST_REFRESH = 0;

    @Override
    public void onAddTabs(TabAdder tabs) {
        // Set Title
        long id = getIntent().getLongExtra(EXTRA_WISHLIST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getWishlist(id).getName());

        Fragment detailFragment = WishlistDataDetailFragment.newInstance(id);
        Fragment componentFragment = WishlistDataComponentFragment.newInstance(id);

        tabs.addTab("Wishlist", () -> detailFragment);
        tabs.addTab("Materials", () -> componentFragment);
    }

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
    public void refreshTitle(){
        // Set again after wishlist is renamed
        long id = getIntent().getLongExtra(EXTRA_WISHLIST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getWishlist(id).getName());
    }
}
