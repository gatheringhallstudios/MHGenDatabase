package com.ghstudios.android.ui.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.MonsterGridPagerAdapter;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

/**
 * Created by Joseph on 7/9/2016.
 */
public class PalicoActivity extends GenericTabActivity {

    private ViewPager viewPager;
    private PalicoPagerAdapter mAdapter;
    private int toggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.palicos);
        toggle = 0;

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new PalicoPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mSlidingTabLayout.setViewPager(viewPager);

        // Tag as top level activity
        super.setAsTopLevel();
    }


    @Override
    protected int getSelectedSection() {
        return MenuSection.PALICOS;
    }

    class PalicoPagerAdapter extends FragmentPagerAdapter {

        String[] _tabs = {"Weapons","Armor"};

        public PalicoPagerAdapter(FragmentManager fm){super(fm);}

        @Override
        public int getCount() {
            return _tabs.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (_tabs[position]){
                case "Weapons":
                    return new PalicoWeaponListFragment();
                case "Armor":
                    return new PalicoArmorListFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int index) {
            return _tabs[index];
        }

    }

}
