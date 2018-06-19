package com.ghstudios.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.components.RecyclerViewDivider
import com.ghstudios.android.mhgendatabase.R

/**
 * Creates a fragment that contains a recyclerview.
 * This handles most of the setup and handles a potential memory leak case.
 * Items are split by a divider
 */
open class RecyclerViewFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
        private set

    /**
     * Overrides onCreateView to return a list_generic.
     * Instead of overriding this, override "onViewCreated".
     */
    final override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        recyclerView = inflater.inflate(
                R.layout.fragment_recyclerview_generic,
                parent,
                false) as RecyclerView

        // Adds a divider


        return recyclerView
    }

    /**
     * Enables a divider to be shown between list items in the recycler view
     * using the default settings for this project.
     */
    fun enableDivider() {
        if (this.recyclerView.itemDecorationCount == 0) {
            val divider = RecyclerViewDivider(recyclerView)
            this.recyclerView.addItemDecoration(divider)
        }
    }

    /**
     * Sets the adapter of the internal recyclerview.
     * This function has to be called everytime the view is recreated
     * by overriding onViewCreated().
     */
    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Removes the adapter from the recyclerview on destroy
        // This also causes the adapter to unregister the view,
        // which prevents a potential cyclical reference memory leak.
        recyclerView.adapter = null
    }
}