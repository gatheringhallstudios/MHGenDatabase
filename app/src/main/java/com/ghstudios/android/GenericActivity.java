package com.ghstudios.android;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ghstudios.android.mhgendatabase.R;

/*
 * Any subclass needs to:
 *  - override onCreate() to set title
 *  - override createFragment() for detail fragments
 */

public abstract class GenericActivity extends GenericActionBarActivity {

    private Fragment detail;

	protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            detail = fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }

        // Integrate Toolbar so sliding drawer can go over toolbar
        androidx.appcompat.widget.Toolbar mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        super.setupDrawer(); // Needs to be called after setContentView
        // Disabled by request. Turns into BACK button
        //super.enableDrawerIndicator(); // Enable drawer toggle button
    }

    /**
     * Returns the currently registered detail fragment.
     * This either returns the result of createFragment, or retrieves it from the fragment manager.
     * @return
     */
    public Fragment getDetail() {
        if (detail != null) {
            return detail;
        }

        // If there is no detail registered, try to get the current fragment manager one.
        // This MAY fix certain cases
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    public void showFatalError() {
        // todo: implement
        // make it override the fragment with a fatal error message
    }
}
