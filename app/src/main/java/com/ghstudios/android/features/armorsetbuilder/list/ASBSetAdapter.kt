package com.ghstudios.android.features.armorsetbuilder.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ASBSetClickListener
import com.ghstudios.android.adapter.common.SimpleDiffRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.mhgendatabase.R

/** Adapter used to display ASB items **/
class ASBSetAdapter : SimpleDiffRecyclerViewAdapter<ASBSet>() {
    override fun areItemsTheSame(oldItem: ASBSet, newItem: ASBSet): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ASBSet, newItem: ASBSet): Boolean {
        return oldItem.name == newItem.name &&
                oldItem.hunterType == newItem.hunterType &&
                oldItem.rank == newItem.rank
    }

    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.fragment_asb_sets_list_item, parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, data: ASBSet) {
        val view = viewHolder.itemView

        val textView = view.findViewById<View>(R.id.name_text) as TextView
        textView.text = data.name

        val propertiesText = view.findViewById<View>(R.id.properties_text) as TextView

        val rankString = AssetLoader.localizeRank(data.rank)
        val hunterType = viewHolder.context.resources.getStringArray(R.array.hunter_type)[data.hunterType]

        propertiesText.text = "$rankString, $hunterType"

        view.tag = data.id

        view.setOnClickListener(ASBSetClickListener(view.context, data.id))
    }
}
