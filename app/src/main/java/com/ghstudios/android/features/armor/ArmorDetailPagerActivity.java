package com.ghstudios.android.features.armor;

import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.ui.detail.ComponentListFragment;
import com.ghstudios.android.ui.detail.ItemToSkillFragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.wishlist.WishlistDataAddDialogFragment;
import com.ghstudios.android.ui.general.BasePagerActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class ArmorDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a armor ID as a long
     */
    public static final String EXTRA_ARMOR_ID =
            "com.daviancorp.android.android.ui.detail.armor_id";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";
    private static final int REQUEST_ADD = 0;

    private long id;
    private String name;

    @Override
    public void onAddTabs(TabAdder tabs) {
        id = getIntent().getLongExtra(EXTRA_ARMOR_ID, -1);
        name = DataManager.get(getApplicationContext()).getArmor(id).getName();
        setTitle(name);

        tabs.addTab("Detail", () ->
                ArmorDetailFragment.newInstance(id)
        );

        tabs.addTab("Skills", () ->
                ItemToSkillFragment.newInstance(id, "Armor")
        );

        tabs.addTab("Components", () ->
                ComponentListFragment.newInstance(id)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR;
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
                        .newInstance(id, name);
                dialogCopy.show(fm, DIALOG_WISHLIST_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
