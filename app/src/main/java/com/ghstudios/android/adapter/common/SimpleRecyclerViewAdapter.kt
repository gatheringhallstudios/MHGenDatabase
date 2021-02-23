package com.ghstudios.android.adapter.common

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.util.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil


/**
 * A simple container-only viewholder used by SimpleListDelegate and SimpleRecyclerViewAdapter.
 * Using a viewholder when using KTX allows caching to work.
 */
open class SimpleViewHolder(containerView: View): RecyclerView.ViewHolder(containerView) {
    val context get() = itemView.context
    val resources get() = itemView.resources
}

/**
 * Base class for single object type recyclerviews to use a more declarative style
 */
abstract class BaseListRecyclerView<T, VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {
    // internal modifiable list
    private val itemSource = mutableListOf<T>()

    /**
     * A list of items contained in this adapter. Cannot be modified directly
     */
    open val items = Collections.unmodifiableList(itemSource)

    protected abstract fun bindView(viewHolder: VH, data: T)

    override fun getItemCount(): Int {
        return items.count()
    }

    /**
     * Updates the items in this adapter and calls notifyDataSetChanged
     */
    open fun setItems(items: List<T>) {
        itemSource.clear()
        itemSource.addAll(items)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        bindView(holder, item)
    }
}

/**
 * Defines an adapter for a simple item meant to be used via KTX.
 * For an adapter with multiple items, use a delegate instead.
 * (Once SimpleListDelegate is made, it'll become easy to swap between the two)
 */
abstract class SimpleRecyclerViewAdapter<T>: BaseListRecyclerView<T, SimpleViewHolder>() {
    protected abstract fun onCreateView(parent: ViewGroup): View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val v = onCreateView(parent)
        return SimpleViewHolder(v)
    }
}

/**
 * A subclass of the basic recyclerview adapter that performs list diffing for fast recyclerview updates.
 */
abstract class BaseDiffRecyclerViewAdapter<T, VH: RecyclerView.ViewHolder>: BaseListRecyclerView<T, VH>() {
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
            return this@BaseDiffRecyclerViewAdapter.areItemsTheSame(oldItem, newItem)
        }
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return this@BaseDiffRecyclerViewAdapter.areContentsTheSame(oldItem, newItem)
        }
    }
}

/**
 * A version of the BaseDiffRecyclerViewAdapter that returns simple view holders
 */
abstract class SimpleDiffRecyclerViewAdapter<T>: BaseDiffRecyclerViewAdapter<T, SimpleViewHolder>() {
    protected abstract fun onCreateView(parent: ViewGroup): View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val v = onCreateView(parent)
        return SimpleViewHolder(v)
    }

}
