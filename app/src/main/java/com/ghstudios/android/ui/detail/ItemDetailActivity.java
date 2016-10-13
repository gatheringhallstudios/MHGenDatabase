package com.ghstudios.android.ui.detail;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.ItemDetailPagerAdapter;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class ItemDetailActivity extends GenericTabActivity {
    /**
     * A key for passing a item ID as a long
     */
    public static final String EXTRA_ITEM_ID =
            "com.daviancorp.android.android.ui.detail.item_id";

    private ViewPager viewPager;
    private ItemDetailPagerAdapter mAdapter;
    private ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long id = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getItem(id).getName());

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ItemDetailPagerAdapter(getSupportFragmentManager(), id);
        viewPager.setAdapter(mAdapter);
        mSlidingTabLayout.setDistributeEvenly(false);
        mSlidingTabLayout.setViewPager(viewPager);
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ITEMS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
