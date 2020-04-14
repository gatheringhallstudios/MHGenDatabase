package com.ghstudios.android.features.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.GenericActivity

/**
 * Created by Carlos on 8/3/2015.
 */
class UniversalSearchActivity : GenericActivity() {
    private var searchView: SearchView? = null
    private var searchFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = null
    }

    override fun createFragment(): androidx.fragment.app.Fragment {
        return UniversalSearchFragment()
    }

    override fun getSelectedSection() = -1

    /**
     * Changes the search query text and submits it.
     */
    fun setSearchQueryAndSubmit(query: String) {
        // The searchview's onQueryTextSubmit() should handle the actual searching
        searchFilter = query
        searchView?.setQuery(query, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // we do not call the superclass as the menu changes in this activity
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        // Get the SearchView and perform some setup
        searchView = menu.findItem(R.id.universal_search).actionView as SearchView
        searchView?.setIconifiedByDefault(false)
        searchView?.isSubmitButtonEnabled = false
        searchView?.queryHint = getString(R.string.search_hint)
        searchView?.isIconified = false

        // Perform searches on text change
        // Note: We call performSearch on the detail, because otherwise its an infinite recursion
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                (detail as? UniversalSearchFragment)?.performSearch(s)
                searchView?.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                (detail as? UniversalSearchFragment)?.performSearch(s)
                return true
            }
        })

        // If filter text was already set, bind it, otherwise get focus
        if (!searchFilter.isNullOrBlank()) {
            searchView?.setQuery(searchFilter, true)
        } else {
            searchView?.requestFocusFromTouch()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Close software keyboard when navigating back from search using the action bar.
        try {
            val selectedView = this.currentFocus
            if (selectedView != null) {
                val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(selectedView.windowToken, 0)
            }
        } catch (ex: Exception) {
            Log.w(javaClass.name, "Error closing keyboard navigating from UniversalSearch", ex)
        }

        return super.onOptionsItemSelected(item)
    }
}
