package com.ghstudios.android.features.search

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter

private const val SEARCH_FILTER = "SEARCH_FILTER"

/**
 * A fragment to display search results.
 * The activity updates the search filter "performSearch",
 * which internally requests the viewmodel to update a livedata of search results
 */
class UniversalSearchFragment : RecyclerViewFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(UniversalSearchViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enableDivider()

        // restore search filter (if it can be restored)
        // We call the activity's perform search as it'll also update the text input
        val savedFilter = savedInstanceState?.getString(SEARCH_FILTER)
        if (savedFilter != null && viewModel.searchFilter == "") {
            (activity as? UniversalSearchActivity)?.setSearchQueryAndSubmit(savedFilter)
        }

        // Set up adapter that'll render the search results
        val adapter = BasicListDelegationAdapter(
                SearchResultAdapterDelegate(context!!)
        )
        setAdapter(adapter)

        // Listen for search results, and populate the adapter
        viewModel.searchResults.observe(viewLifecycleOwner, Observer { results ->
            adapter.items = results ?: emptyList()
            adapter.notifyDataSetChanged()
        })
    }

    fun performSearch(searchTerm: String) {
        viewModel.updateSearchFilter(searchTerm)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save search filter to allow it to be restored
        outState.putString(SEARCH_FILTER, viewModel.searchFilter)
    }
}
