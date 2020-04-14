package com.ghstudios.android.features.armorsetbuilder.armorselect

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.mhgendatabase.R

/**
 * Fragment used to display armor pieces that are currently in wishlists.
 */
class ArmorSelectWishlistFragment: ListFragment() {
    /**
     * ViewModel (anchored to parent)
     */
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(ArmorSelectViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.armorWishlistData.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            val adapter = ArmorListAdapter(context!!, it)
            listAdapter = adapter
        })
    }
}