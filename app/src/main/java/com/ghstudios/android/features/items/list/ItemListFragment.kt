package com.ghstudios.android.features.items.list

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.RecyclerViewFragment

class ItemListFragment : RecyclerViewFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(ItemListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.enableDivider()
        enableFilter {
            viewModel.setFilter(it)
        }

        val adapter = ItemListAdapter()
        setAdapter(adapter)

        viewModel.itemData.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            adapter.setItems(it)
        })
    }
}