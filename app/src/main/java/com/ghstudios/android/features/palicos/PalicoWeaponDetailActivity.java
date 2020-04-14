package com.ghstudios.android.features.palicos;

import androidx.fragment.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.components.ComponentListFragment;
import com.ghstudios.android.features.wishlist.external.WishlistDataAddDialogFragment;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

/**
 * Created by Joseph on 7/10/2016.
 */
public class PalicoWeaponDetailActivity extends BasePagerActivity {

    public static final String EXTRA_WEAPON_ID = "WEAPON_ID";
    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";

    private long id;
    private String name;

    @Override
    public void onAddTabs(TabAdder tabs) {
        id = getIntent().getLongExtra(EXTRA_WEAPON_ID, -1);
        PalicoWeapon wep = DataManager.get().getPalicoWeapon(id);
        name = wep.getItem().getName();
        setTitle(R.string.palicos);

        tabs.addTab(R.string.palico_equipment_tab_detail, () ->
                PalicoWeaponDetailFragment.newInstance(getIntent().getLongExtra(EXTRA_WEAPON_ID,0))
        );

        tabs.addTab(R.string.palico_equipment_tab_components, () ->
                ComponentListFragment.newInstance(getIntent().getLongExtra(EXTRA_WEAPON_ID,0))
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.PALICOS;
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
