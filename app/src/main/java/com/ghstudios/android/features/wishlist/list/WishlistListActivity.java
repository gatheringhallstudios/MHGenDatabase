package com.ghstudios.android.features.wishlist.list;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

public class WishlistListActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wishlist);

        // Tag as top level activity
        super.setAsTopLevel();
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.WISH_LISTS;
    }

    @Override
    protected Fragment createFragment() {
        return new WishlistListFragment();
    }

}
