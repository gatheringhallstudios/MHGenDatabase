package com.ghstudios.android.adapter.common

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import java.util.*
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v7.util.DiffUtil


/**
 * A simple container-only viewholder used by SimpleListDelegate and SimpleRecyclerViewAdapter.
 * Using a viewholder when using KTX allows caching to work.
 */
class SimpleViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
    val context get() = itemView.context
    val resources get() = itemView.resources
}

/**
 * Defines an adapter for a simple item meant to be used via KTX.
 * For an adapter with multiple items, use a delegate instead.
 * (Once SimpleListDelegate is made, it'll become easy to swap between the two)
 */
abstract class SimpleRecyclerViewAdapter<T>: RecyclerView.Adapter<SimpleViewHolder>() {
    protected abstract fun onCreateView(parent: ViewGroup): View
    protected abstract fun bindView(viewHolder: SimpleViewHolder, data: T)

    // internal modifiable list
    private val itemSource = mutableListOf<T>()

    /**
     * A list of items contained in this adapter. Cannot be modified directly
     */
    open val items = Collections.unmodifiableList(itemSource)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val v = onCreateView(parent)
        return SimpleViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val item = items[position]
        bindView(holder, item)
    }

    /**
     * Updates the items in this adapter and calls notifyDataSetChanged
     */
    open fun setItems(items: List<T>) {
        itemSource.clear()
        itemSource.addAll(items)
        notifyDataSetChanged()
    }
}

/**
 * A subclass of the basic recyclerview adapter that performs list diffing for fast recyclerview updates.
 */
abstract class SimpleDiffRecyclerViewAdapter<T>: SimpleRecyclerViewAdapter<T>() {
    // suppressed since both will be gc'd at the same time.
    @Suppress("LeakingThis")
    private val mDiffer = AsyncListDiffer(this, DiffCallback())

    /**
     * Returns a readonly collection of the current items in this adapter.
     */
    override val items: List<T> get() = mDiffer.currentList

    /**
     * Sets the list of items to be displayed.
     * Sends notifications to update the list
     */
    override fun setItems(items: List<T>) {
        mDiffer.submitList(items)
    }

    /**
     * Override to create a comparator for whether two items are the same
     */
    protected abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    /**
     * Called when an item has been confirmed to match, to determine if the view needs rerendering.
     * Defaults to always returning true.
     */
    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return true
    }

    // internal implementation of the diff callback. Differs to an astract method.
    inner class DiffCallback : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return this@SimpleDiffRecyclerViewAdapter.areItemsTheSame(oldItem, newItem)
        }
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return this@SimpleDiffRecyclerViewAdapter.areContentsTheSame(oldItem, newItem)
        }
    }
}