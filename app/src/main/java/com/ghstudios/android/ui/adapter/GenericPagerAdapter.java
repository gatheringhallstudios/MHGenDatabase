package com.ghstudios.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Internal class that handles the actual logic of rendering tabs
 */
public class GenericPagerAdapter extends FragmentPagerAdapter {
    private String TAG = getClass().getSimpleName();
    private List<PagerTab> tabs;

    public GenericPagerAdapter(FragmentManager fm, List<PagerTab> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    /**
     * Creates the pager adapter for the fragment, but doesn't attach it.
     * Equivalent to passing frag.getChildFragmentManager().
     * @param frag
     * @param tabs
     */
    public GenericPagerAdapter(Fragment frag, List<PagerTab> tabs) {
        this(frag.getChildFragmentManager(), tabs);
    }

    @Override
    public Fragment getItem(int index) {
        try {
            return tabs.get(index).buildFragment();
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(TAG, "getItem: ", ex);
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int index) {
        try {
            return tabs.get(index).getTitle();
        } catch (ArrayIndexOutOfBoundsException ex) {
            Log.e(TAG, "getItem: ", ex);
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
