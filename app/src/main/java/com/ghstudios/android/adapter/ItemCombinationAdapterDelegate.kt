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
import com.ghstudios.android.mhgendatabase.databinding.FragmentCombiningListitemBinding

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
        val binding = FragmentCombiningListitemBinding.bind(viewHolder.itemView)
        with (binding) {
            AssetLoader.setIcon(binding.resultIcon,data.createdItem)
            AssetLoader.setIcon(binding.item1Icon,data.item1)
            AssetLoader.setIcon(binding.item2Icon,data.item2)

            resultName.text = data.createdItem.name
            item1Name.text = data.item1.name
            item2Name.text = data.item2.name

            percentage.text = "${data.percentage}%"
        }


        val min = data.amountMadeMin
        val max = data.amountMadeMax
        binding.yieldAmount.text = "x" + when (min == max) {
            true -> min.toString()
            false -> "$min-$max"
        }

        binding.item1.setOnClickListener(BasicItemClickListener(context, data.item1.id))
        binding.item2.setOnClickListener(BasicItemClickListener(context, data.item2.id))

        if (resultItemNavigationEnabled) {
            binding.root.setOnClickListener(BasicItemClickListener(context, data.createdItem.id))
        } else {
            // disable selectable item background
            binding.root.setBackgroundResource(0)
        }
    }
}
