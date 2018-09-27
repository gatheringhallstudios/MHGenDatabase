package com.ghstudios.android.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ghstudios.android.mhgendatabaseold.R;
import com.ghstudios.android.ui.adapter.ArmorExpandableListPagerAdapter;
import com.ghstudios.android.ui.detail.ASBActivity;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class ArmorListActivity extends GenericTabActivity {

    private ViewPager viewPager;
    private ArmorExpandableListPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.armor);

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ArmorExpandableListPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mSlidingTabLayout.setViewPager(viewPager);

        // Enable back button if we're coming from the set builder
        if (getIntent().getBooleanExtra(ASBActivity.EXTRA_FROM_SET_BUILDER, false)) {
            super.disableDrawerIndicator();
            if (getIntent().getIntExtra(ASBActivity.EXTRA_SET_HUNTER_TYPE, -1) == 1) {
                viewPager.setCurrentItem(1); // We change to the gunner page if its a gunner set
            }
        }
        else {
            // Tag as top level activity
            super.setAsTopLevel();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ASBActivity.REQUEST_CODE_ADD_PIECE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
