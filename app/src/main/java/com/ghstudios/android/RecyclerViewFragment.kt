package com.ghstudios.android

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.mhgendatabase.R

/**
 * A special version of a recyclerview that updates the adapter
 * to null when it is detatched from the window.
 * Used internally by the RecyclerViewFragment.
 * Do not use for nested recyclerviews.
 */
class DetachingRecyclerView : RecyclerView {
    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?):
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int):
            super(context, attrs, defStyle)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter = null
    }
}

/**
 * Creates a fragment that contains a recyclerview.
 * This handles most of the setup and handles a potential memory leak case.
 * Items are split by a divider
 */
open class RecyclerViewFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
        private set

    private lateinit var emptyView: View

    /**
     * Overrides onCreateView to return a list_generic.
     * Instead of overriding this, override "onViewCreated".
     */
    final override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        // the leak is actually handled by the special subclass recyclerview in the inflated layout
        val view = inflater.inflate(R.layout.fragment_recyclerview_main, parent,false)

        recyclerView = view.findViewById(R.id.content_recyclerview)
        emptyView = view.findViewById(R.id.empty_view)

        return view
    }

    /**
     * Enables a divider to be shown between list items in the recycler view
     * using the default settings for this project.
     */
    fun enableDivider() {
        if (this.recyclerView.itemDecorationCount == 0) {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
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

    /**
     * Shows the empty view instead of the recycler view.
     * There is no way to revert. Only call this once you're SURE there is no data.
     */
    fun showEmptyView() {
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }
}