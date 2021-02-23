package com.ghstudios.android.features.monsters.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.MonsterClickListener
import com.ghstudios.android.adapter.common.SimpleRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.FragmentListItemLargeBinding

/**
 * An adapter used to display a monster list (and only a monster list).
 */
class MonsterListAdapter: SimpleRecyclerViewAdapter<Monster>() {
    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.fragment_list_item_large, parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, monster: Monster) {
        val binding = FragmentListItemLargeBinding.bind(viewHolder.itemView)
        with(binding) {
            AssetLoader.setIcon(itemImage,monster)
            itemLabel.text = monster.name
            root.tag = monster.id
            root.setOnClickListener(MonsterClickListener(viewHolder.context, monster.id))
        }
    }
}
