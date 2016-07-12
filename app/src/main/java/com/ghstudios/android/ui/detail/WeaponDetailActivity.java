package com.ghstudios.android.ui.detail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.WeaponDetailPagerAdapter;
import com.ghstudios.android.ui.dialog.WishlistDataAddDialogFragment;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class WeaponDetailActivity extends GenericTabActivity {
    /**
     * A key for passing a weapon ID as a long
     */
    public static final String EXTRA_WEAPON_ID =
            "com.daviancorp.android.android.ui.detail.weapon_id";

    private static final String DIALOG_WISHLIST_ADD = "wishlist_add";
    private static final int REQUEST_ADD = 0;

    private ViewPager viewPager;
    private WeaponDetailPagerAdapter mAdapter;

    private long id;
    private String name;
    String wtype;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getLongExtra(EXTRA_WEAPON_ID, -1);
        name = DataManager.get(getApplicationContext()).getWeapon(id).getName();
        setTitle(name);

        //JOE: This is so we can add/remove tabs as needed based on type.
        //Goes against the design of doing queries on the UI Thread, but
        //this query is super fast and won't cause issues.
        //
        //Possible redesign is to pass in the wtype, almost all links here should know it.
        wtype = DataManager.get(this).getWeaponType(id);


        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new WeaponDetailPagerAdapter(getSupportFragmentManager(), getApplicationContext(), id,wtype);
        viewPager.setAdapter(mAdapter);

        //If we have a lot of tabs
        if(mAdapter.getCount()>3)
            mSlidingTabLayout.setDistributeEvenly(false);


        mSlidingTabLayout.setViewPager(viewPager);
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
                        .newInstance(id, name);
                dialogCopy.show(fm, DIALOG_WISHLIST_ADD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
