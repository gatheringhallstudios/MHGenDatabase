package com.ghstudios.android.features.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter

/**
 * A fragment to display search results.
 * The activity updates the search filter "performSearch",
 * which internally requests the viewmodel to update a livedata of search results
 */
class UniversalSearchFragment : RecyclerViewFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(UniversalSearchViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enableDivider()

        val adapter = BasicListDelegationAdapter(
                SearchResultAdapterDelegate(context!!)
        )
        setAdapter(adapter)

        viewModel.searchResults.observe(this, Observer { results ->
            adapter.items = results ?: emptyList()
            adapter.notifyDataSetChanged()
        })
    }

    fun performSearch(searchTerm: String) {
        viewModel.updateSearchFilter(searchTerm)
    }
}
