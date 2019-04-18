package com.ghstudios.android.features.wishlist.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ghstudios.android.mhgendatabase.R

/**
 * Fragment used to display a list of required crafting components for the wishlist detail.
 */
class WishlistDataComponentFragment : Fragment() {
    /**
     * Returns the viewmodel owned by the activity, which has already loaded wishlist data
     */
    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(WishlistDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wishlist_component_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.content_recyclerview)

        // Add divider for recyclerview
        if (recyclerView.itemDecorationCount == 0) {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            recyclerView.addItemDecoration(divider)
        }

        val adapter = WishlistComponentViewAdapter { component, quantity ->
            // Edit qtyhave for the component's row
            viewModel.updateComponentQuantity(component.id, quantity)
        }
        recyclerView.adapter = adapter

        viewModel.wishlistComponents.observe(this, Observer {
            if (it == null) return@Observer
            adapter.setItems(it)
        })

        // Bind the total cost value
        val mTotalCostView = view.findViewById<TextView>(R.id.total_cost_value)
        viewModel.priceData.observe(this, Observer {
            mTotalCostView.text = getString(R.string.value_zenny, it ?: 0)
        })
    }
}
