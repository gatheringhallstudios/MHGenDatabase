package com.ghstudios.android.ui.general;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.GenericPagerAdapter;
import com.ghstudios.android.ui.adapter.PagerTab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract Base Activity for implementing screens with multiple tabs.
 * Subclass this and call addTab() to set up hub pages.
 * NOTE: This is adapted from the BasePagerFragment from the world app.
 * If switching to single activity, replace for the fragment version.
 * Everything else is the same.
 */

public abstract class BasePagerActivity extends GenericActionBarActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    /**
     * Called when the fragment wants the tabs, but after Butterknife
     * has binded the view
     * @param tabs
     */
    public abstract void onAddTabs(TabAdder tabs);

    @Nullable
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab);

        // Perform MHGUDatabase specific stuff
        doSpecialSetup();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);

        // Setup tabs
        ArrayList<PagerTab> tabs = new ArrayList<>();
        AtomicInteger defaultIdx = new AtomicInteger(-1);

        onAddTabs(new TabAdder() {
            @Override
            public void addTab(String title, PagerTab.Factory builder) {
                tabs.add(new PagerTab(title, builder));
            }

            @Override
            public void setDefaultItem(int idx) {
                // note: We're setting an atomic integer due to reassignment restrictions
                defaultIdx.set(idx);
            }
        });

        if (!tabs.isEmpty()) {
            resetTabs(tabs, defaultIdx.get());
        }
    }

    /**
     * Performs "special setup" exclusive to this app. This is code not from the World app.
     */
    private void doSpecialSetup() {
        android.support.v7.widget.Toolbar mtoolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mtoolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.app_name);
        super.setupDrawer(); // Needs to be called after setContentView
    }

    /**
     * Function to reset pager tabs using a list of PagerTab objects.
     */
    public void resetTabs(List<PagerTab> tabs, int idx) {
        // check if we're over 4 tabs. If so, make scrollable. This is the old app behavior
        if (tabs.size() > 4) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        // Initialize ViewPager (tab behavior)
        viewPager.setAdapter(new GenericPagerAdapter(getSupportFragmentManager(), tabs));
        tabLayout.setupWithViewPager(viewPager);

        if (idx > 0) {
            viewPager.setCurrentItem(idx);
        }
    }

    public void resetTabs(List<PagerTab> tabs) {
        resetTabs(tabs, -1);
    }

    public interface TabAdder {
        /**
         * Adds a tab to the fragment
         *
         * @param title   The title to display for the tab
         * @param builder A TabFactory or lambda that builds the tab fragment
         */
        void addTab(String title, PagerTab.Factory builder);

        /**
         * Sets the default selected tab idx
         * @param idx
         */
        void setDefaultItem(int idx);
    }
}
