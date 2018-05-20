package com.ghstudios.android.features.palicos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.detail.ComponentListFragment;
import com.ghstudios.android.ui.dialog.WishlistDataAddDialogFragment;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

/**
 * Created by Joseph on 7/10/2016.
 */
public class PalicoWeaponDetailActivity extends GenericTabActivity {

    public static final String EXTRA_WEAPON_ID = "WEAPON_ID";
    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";

    private long id;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getLongExtra(EXTRA_WEAPON_ID, -1);
        PalicoWeapon wep = DataManager.get(getApplicationContext()).getPalicoWeapon(id);
        name = wep.getItem().getName();
        setTitle(R.string.palicos);

        // Initialization
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PalicoWeaponPagerAdapter mAdapter = new PalicoWeaponPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mSlidingTabLayout.setViewPager(viewPager);
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

    class PalicoWeaponPagerAdapter extends FragmentPagerAdapter{

        String[] _tabs = {"Details","Components"};

        public PalicoWeaponPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (_tabs[position]){
                case "Details":
                    return PalicoWeaponDetailFragment.newInstance(getIntent().getLongExtra(EXTRA_WEAPON_ID,0));
                case "Components":
                    return ComponentListFragment.newInstance(getIntent().getLongExtra(EXTRA_WEAPON_ID,0));
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _tabs[position];
        }

        @Override
        public int getCount() {
            return _tabs.length;
        }
    }

}
