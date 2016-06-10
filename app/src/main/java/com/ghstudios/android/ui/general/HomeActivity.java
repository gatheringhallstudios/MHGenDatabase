package com.ghstudios.android.ui.general;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.list.adapter.MenuSection;

/*
 * The home screen activity upon starting the application
 */
@SuppressLint("NewApi")
public class HomeActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//		getSupportActionBar().setHomeButtonEnabled(false);
        super.enableDrawerIndicator(); // Enable drawer button instead of back button
        setTitle(R.string.app_name);

    }

    @Override
    protected MenuSection getSelectedSection() {
        return MenuSection.UNLISTED;
    }

    @Override
    protected Fragment createFragment() {
        super.detail = new HomeFragment();
        return super.detail;
    }
}
