package com.ghstudios.android

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.ghstudios.android.mhgendatabase.R

/**
 * A special version of a recyclerview that updates the adapter
 * to null when it is detatched from the window.
 * Used internally by the RecyclerViewFragment.
 * Do not use for nested recyclerviews.
 */
class DetachingRecyclerView : androidx.recyclerview.widget.RecyclerView {
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
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
        private set
    lateinit var fab: FloatingActionButton
        private set

    private lateinit var recyclerViewContainer: View
    private lateinit var textField: EditText
    private lateinit var emptyView: View

    private var previousListener: TextWatcher? = null

    /**
     * Overrides onCreateView to return a list_generic.
     * Instead of overriding this, override "onViewCreated".
     */
    final override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        // the leak is actually handled by the special subclass recyclerview in the inflated layout
        val view = inflater.inflate(R.layout.fragment_recyclerview_main, parent,false)

        recyclerViewContainer = view.findViewById(R.id.recyclerview_container)
        recyclerView = view.findViewById(R.id.content_recyclerview)
        textField = view.findViewById(R.id.input_search)
        emptyView = view.findViewById(R.id.empty_view)
        fab = view.findViewById(R.id.fab)

        return view
    }

    /**
     * Enables a divider to be shown between list items in the recycler view
     * using the default settings for this project.
     */
    fun enableDivider() {
        if (this.recyclerView.itemDecorationCount == 0) {
            val divider = DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
            this.recyclerView.addItemDecoration(divider)
        }
    }

    /**
     * Makes the floating action button visible and binds it to a callback
     */
    fun enableFab(callback: () -> Unit) {
        fab.visibility = View.VISIBLE
        fab.setOnClickListener {
            callback.invoke()
        }
    }

    /**
     * Sets the adapter of the internal recyclerview.
     * This function has to be called everytime the view is recreated
     * by overriding onViewCreated().
     */
    fun setAdapter(adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?) {
        recyclerView.adapter = adapter
    }

    fun enableFilter(onUpdate: (String) -> Unit) {
        textField.visibility = View.VISIBLE

        if (previousListener != null) {
            textField.removeTextChangedListener(previousListener)
        }

        previousListener = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = (s?.toString() ?: "").trim()
                onUpdate(value)
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        }
        textField.addTextChangedListener(previousListener)
    }

    /**
     * Shows the empty view instead of the recycler view.
     * Recommended to wait until you're sure there is no data.
     */
    fun showEmptyView(show: Boolean = true) {
        if (show) {
            recyclerViewContainer.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerViewContainer.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }
}