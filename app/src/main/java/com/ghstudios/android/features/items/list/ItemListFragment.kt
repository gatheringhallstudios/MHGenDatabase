package com.ghstudios.android.features.items.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.ghstudios.android.RecyclerViewFragment

class ItemListFragment : RecyclerViewFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ItemListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.enableDivider()
        enableFilter {
            viewModel.setFilter(it)
        }

        val adapter = ItemListAdapter()
        setAdapter(adapter)

        viewModel.itemData.observe(this, Observer {
            if (it == null) return@Observer

            adapter.setItems(it)
        })
    }
}