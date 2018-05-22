package com.ghstudios.android.features.weapons;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.wishlist.WishlistDataAddDialogFragment;
import com.ghstudios.android.ui.detail.ComponentListFragment;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class WeaponDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a weapon ID as a long
     */
    public static final String EXTRA_WEAPON_ID =
            "com.daviancorp.android.android.ui.detail.weapon_id";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";
    private static final int REQUEST_ADD = 0;

    long weaponId;
    String name;

    @Override
    public void onAddTabs(TabAdder tabs) {
        weaponId = getIntent().getLongExtra(EXTRA_WEAPON_ID, -1);
        name = DataManager.get(getApplicationContext()).getWeapon(weaponId).getName();

        //JOE: This is so we can add/remove tabs as needed based on type.
        //Goes against the design of doing queries on the UI Thread, but
        //this query is super fast and won't cause issues.
        //
        //Possible redesign is to pass in the wtype, almost all links here should know it.
        String wtype = DataManager.get(this).getWeaponType(weaponId);

        // Set activity title to display weapon type
        setTitle(wtype);

        // All weapons have a detail tab
        tabs.addTab("Detail", () ->
                getDetailForWeaponType(wtype, weaponId)
        );

        // Certain weapon types may have a different second tab
        if (wtype.equals("Hunting Horn")) {
            tabs.addTab("Melodies", () ->
                    WeaponSongFragment.newInstance(weaponId)
            );
        } else if (wtype.contains("Bowgun")) {
            tabs.addTab("Ammo", () ->
                    WeaponDetailAmmoFragment.newInstance(weaponId)
            );
        } else if (wtype.equals("Bow")) {
            tabs.addTab("Coatings", () ->
                    WeaponDetailCoatingFragment.newInstance(weaponId)
            );
        }

        tabs.addTab("Family Tree", () ->
                WeaponTreeFragment.newInstance(weaponId)
        );

        tabs.addTab("Components", () ->
                ComponentListFragment.newInstance(weaponId)
        );
    }

    /**
     * Helper that gets the "Detail tab contents" for the weapon type
     * @param wtype
     * @return
     */
    private Fragment getDetailForWeaponType(String wtype, long weaponId) {
        switch(wtype){
            case "Light Bowgun":
            case "Heavy Bowgun":
                return WeaponBowgunDetailFragment.newInstance(weaponId);
            case "Bow":
                return WeaponBowDetailFragment.newInstance(weaponId);
            default:
                return WeaponBladeDetailFragment.newInstance(weaponId);
        }
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.WEAPONS;
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
                        .newInstance(weaponId, name);
                dialogCopy.show(fm, DIALOG_WISHLIST_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
