package com.ghstudios.android.features.armorsetbuilder.talismans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.adapter.common.SimpleDiffRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.ListitemTalismanBinding

/**
 * A recyclerview adapter used to display talismans
 */
class TalismanAdapter(
        val onSelect: (ASBTalisman) -> Unit,
        val onLongSelect: ((ASBTalisman) -> Unit)? = null
): SimpleDiffRecyclerViewAdapter<ASBTalisman>() {
    override fun areItemsTheSame(oldItem: ASBTalisman, newItem: ASBTalisman): Boolean {
        return oldItem.id == newItem.id
    }

    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.listitem_talisman, parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, data: ASBTalisman) {
        fun boolToVisibility(value: Boolean) = when (value) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        val ctx = viewHolder.context
        val binding = ListitemTalismanBinding.bind(viewHolder.itemView)
        with(binding) {
            icon.setImageDrawable(AssetLoader.loadIconFor(data))
            slots.setSlots(data.numSlots, 0)
        }
        binding.skill1.apply {
            visibility = boolToVisibility(data.firstSkill != null)
            text = data.firstSkill?.skillTree?.name ?: ""
        }
        binding.skill1Pts.apply {
            visibility = binding.skill1.visibility
            text = ctx.getString(R.string.format_plus, data.firstSkill?.points)
        }
        binding.skill2.apply {
            visibility = boolToVisibility(data.secondSkill != null)
            text = data.secondSkill?.skillTree?.name ?: ""
        }
        binding.skill2Pts.apply {
            visibility = binding.skill2.visibility
            text = ctx.getString(R.string.format_plus, data.secondSkill?.points)
        }

        binding.root.tag = data.id
        binding.root.setOnClickListener {
            onSelect.invoke(data)
        }

        if (onLongSelect != null) {
            binding.root.setOnLongClickListener {
                onLongSelect.invoke(data)
                true
            }
        }
    }
}
