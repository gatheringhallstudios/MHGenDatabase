package com.ghstudios.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.BasicItemClickListener
import com.ghstudios.android.adapter.common.SimpleListDelegate
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.Combining
import com.ghstudios.android.mhgendatabase.R
import kotlinx.android.synthetic.main.fragment_combining_listitem.*

/**
 * An adapter delegate that can be added to any adapter delegate adapter.
 * Renders item combination information
 */
class ItemCombinationAdapterDelegate: SimpleListDelegate<Combining>() {
    /**
     * Sets whether the result item performs navigation. Defaults to true.
     * Use before adding items to the adapter
     */
    var resultItemNavigationEnabled = true

    override fun isForViewType(obj: Any) = obj is Combining

    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.fragment_combining_listitem, parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, data: Combining) {
        val context = viewHolder.context

        AssetLoader.setIcon(viewHolder.result_icon,data.createdItem)
        AssetLoader.setIcon(viewHolder.item1_icon,data.item1)
        AssetLoader.setIcon(viewHolder.item2_icon,data.item2)

        viewHolder.result_name.text = data.createdItem.name
        viewHolder.item1_name.text = data.item1.name
        viewHolder.item2_name.text = data.item2.name

        viewHolder.percentage.text = "${data.percentage}%"

        val min = data.amountMadeMin
        val max = data.amountMadeMax
        viewHolder.yield_amount.text = "x" + when (min == max) {
            true -> min.toString()
            false -> "$min-$max"
        }

        viewHolder.item1.setOnClickListener(BasicItemClickListener(context, data.item1.id))
        viewHolder.item2.setOnClickListener(BasicItemClickListener(context, data.item2.id))

        if (resultItemNavigationEnabled) {
            viewHolder.itemView.setOnClickListener(BasicItemClickListener(context, data.createdItem.id))
        } else {
            // disable selectable item background
            viewHolder.itemView.setBackgroundResource(0)
        }
    }
}
