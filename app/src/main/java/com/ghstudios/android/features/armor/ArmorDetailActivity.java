package com.ghstudios.android.features.armor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.components.ComponentListFragment;
import com.ghstudios.android.features.decorations.DecorationDetailFragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.wishlist.WishlistDataAddDialogFragment;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class ArmorDetailActivity extends GenericActivity {
    /**
     * A key for passing a armor ID as a long
     */
    public static final String EXTRA_ARMOR_ID =
            "com.daviancorp.android.android.ui.detail.armor_id";

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR;
    }

    @Override
    protected Fragment createFragment() {
        long id = getIntent().getLongExtra(EXTRA_ARMOR_ID, -1);
        return ArmorDetailFragment.newInstance(id);
    }
}
