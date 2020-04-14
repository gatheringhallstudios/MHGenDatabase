package com.ghstudios.android;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * A custom pager adapter that accepts a list of pager tab objects.
 * todo: rename
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

    /**
     * Updates the set of tabs and notifies that there was a change
     * @param tabs
     */
    public void setTabs(List<PagerTab> tabs) {
        this.tabs = tabs;
        notifyDataSetChanged();
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
