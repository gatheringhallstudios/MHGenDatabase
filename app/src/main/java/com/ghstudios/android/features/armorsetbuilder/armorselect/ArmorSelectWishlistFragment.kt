package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.mhgendatabase.R

/**
 * Fragment used to display armor pieces that are currently in wishlists.
 */
class ArmorSelectWishlistFragment: ListFragment() {
    /**
     * ViewModel (anchored to parent)
     */
    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ArmorSelectViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.armorWishlistData.observe(this, Observer {
            if (it == null) return@Observer

            val adapter = ArmorListAdapter(context!!, it)
            listAdapter = adapter
        })
    }
}