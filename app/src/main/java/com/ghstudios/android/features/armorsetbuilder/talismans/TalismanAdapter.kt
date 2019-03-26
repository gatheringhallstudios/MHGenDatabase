package com.ghstudios.android.features.armorsetbuilder.talismans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.adapter.common.SimpleDiffRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.mhgendatabase.R
import kotlinx.android.synthetic.main.listitem_talisman.*

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

        viewHolder.icon.setImageDrawable(AssetLoader.loadIconFor(data))
        viewHolder.slots.setSlots(data.numSlots, 0)
        viewHolder.skill_1.apply {
            visibility = boolToVisibility(data.firstSkill != null)
            text = data.firstSkill?.skillTree?.name ?: ""
        }
        viewHolder.skill_1_pts.apply {
            visibility = viewHolder.skill_1.visibility
            text = ctx.getString(R.string.format_plus, data.firstSkill?.points)
        }
        viewHolder.skill_2.apply {
            visibility = boolToVisibility(data.secondSkill != null)
            text = data.secondSkill?.skillTree?.name ?: ""
        }
        viewHolder.skill_2_pts.apply {
            visibility = viewHolder.skill_2.visibility
            text = ctx.getString(R.string.format_plus, data.secondSkill?.points)
        }

        viewHolder.itemView.tag = data.id
        viewHolder.itemView.setOnClickListener {
            onSelect.invoke(data)
        }

        if (onLongSelect != null) {
            viewHolder.itemView.setOnLongClickListener {
                onLongSelect.invoke(data)
                true
            }
        }
    }
}