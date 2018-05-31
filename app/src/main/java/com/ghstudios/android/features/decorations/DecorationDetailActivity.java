package com.ghstudios.android.features.decorations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.components.ItemToSkillFragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.wishlist.WishlistDataAddDialogFragment;
import com.ghstudios.android.components.ComponentListFragment;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class DecorationDetailActivity extends GenericActivity {
    /**
     * A key for passing a decoration ID as a long
     */
    public static final String EXTRA_DECORATION_ID =
            "com.daviancorp.android.android.ui.detail.decoration_id";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";
    private static final int REQUEST_ADD = 0;

    private String name;
    private long decorationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.DECORATION;
    }

    @Override
    protected Fragment createFragment() {
        decorationId = getIntent().getLongExtra(EXTRA_DECORATION_ID, -1);
        return DecorationDetailFragment.newInstance(decorationId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = new MenuInflater(getApplicationContext());
        inflater.inflate(R.menu.menu_add_to_wishlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_wishlist:
                FragmentManager fm = getSupportFragmentManager();
                WishlistDataAddDialogFragment dialogCopy = WishlistDataAddDialogFragment
                        .newInstance(decorationId, name);
                dialogCopy.show(fm, DIALOG_WISHLIST_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
