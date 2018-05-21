package com.ghstudios.android.ui.general;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ghstudios.android.mhgendatabase.R;

/*
 * Any subclass needs to:
 *  - override onCreate() to set title
 */

public abstract class GenericTabActivity extends GenericActionBarActivity {

    protected Fragment detail;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab);

        // Integrate Toolbar so sliding drawer can go over toolbar
        android.support.v7.widget.Toolbar mtoolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mtoolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.app_name);
        super.setupDrawer(); // Needs to be called after setContentView
        // Disabled by request. Turns into BACK button
        //super.enableDrawerIndicator(); // Enable drawer toggle button
    }

    public void setViewPager(ViewPager pager) {
        TabLayout layout = findViewById(R.id.tab_layout);

        if (pager.getAdapter().getCount() > 4) {
            layout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            layout.setTabMode(TabLayout.MODE_FIXED);
        }

        layout.setupWithViewPager(pager);
    }
}
