package com.ghstudios.android.ui.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.general.GenericActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class WyporiumTradeListActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wyporiumtrade);

        // Tag as top level activity
        super.setAsTopLevel();
    }

    @Override
    protected MenuSection getSelectedSection() {
        return MenuSection.WYPORIUM_TRADE;
    }

    @Override
    protected Fragment createFragment() {
        super.detail = new WyporiumTradeListFragment();
        return super.detail;
    }

}
