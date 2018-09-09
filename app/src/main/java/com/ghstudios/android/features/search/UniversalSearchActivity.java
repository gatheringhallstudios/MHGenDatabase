package com.ghstudios.android.features.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.GenericActivity;

/**
 * Created by Carlos on 8/3/2015.
 */
public class UniversalSearchActivity extends GenericActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(null); // todo: replace
    }

    @Override
    protected Fragment createFragment() {
        return new UniversalSearchFragment();
    }

    @Override
    protected int getSelectedSection() {
        return -1; // todo: something else?
    }

    public void performSearch(String query) {
        if (getDetail() != null) {
            ((UniversalSearchFragment)getDetail()).performSearch(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // we do not call the superclass as the menu changes in this activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and perform some setup
        final SearchView searchView = (SearchView) menu.findItem(R.id.universal_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setIconified(false);

        // Perform searches on text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                performSearch(s);
                return true;
            }
        });

        // Focus the search view on entry
        searchView.requestFocusFromTouch();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Close software keyboard when navigating back from search using the action bar.
        try {
            View selectedView = this.getCurrentFocus();
            if (selectedView != null) {
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(selectedView.getWindowToken(), 0);
            }
        } catch (Exception ex) {
            Log.w(getClass().getName(), "Error closing keyboard navigating from UniversalSearch", ex);
        }

        return super.onOptionsItemSelected(item);
    }
}
