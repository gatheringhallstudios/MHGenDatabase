package com.ghstudios.android.features.decorations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.detail.ASBPagerActivity;
import com.ghstudios.android.ui.general.GenericActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class DecorationListActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.decorations);

        // Enable back button if we're coming from the set builder
        if (getIntent().getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)) {
            super.disableDrawerIndicator();
        } else {
            // Enable drawer button instead of back button
            super.enableDrawerIndicator();

            // Tag as top level activity
            super.setAsTopLevel();
        }
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.DECORATION;
    }

    @Override
    protected Fragment createFragment() {
        super.detail = new DecorationListFragment();
        return super.detail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ASBPagerActivity.REQUEST_CODE_ADD_DECORATION && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
