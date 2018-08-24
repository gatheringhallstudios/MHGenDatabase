package com.ghstudios.android.features.items.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.adapter.common.SimpleRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.setImageAsset
import kotlinx.android.synthetic.main.fragment_item_listitem.*

class ItemListAdapter : SimpleRecyclerViewAdapter<Item>() {
    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.fragment_item_listitem,
                parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, item: Item) {
        val itemNameTextView = viewHolder.text1
        val itemImageView = viewHolder.icon

        itemNameTextView.text = item.name
        itemImageView.setImageAsset(item)

        val listener = ItemClickListener(viewHolder.context, item)
        viewHolder.itemView.setOnClickListener(listener)
    }
}