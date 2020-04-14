package com.ghstudios.android.features.items.detail

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.adapter.ItemCombinationAdapterDelegate
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.mhgendatabase.R
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

/**
 * A fragment that display usages for an item
 */
class ItemUsageFragment : RecyclerViewFragment() {
    companion object {
        private const val ARG_ITEM_ID = "COMPONENT_ID"

        @JvmStatic
        fun newInstance(id: Long): ItemUsageFragment {
            val args = Bundle()
            args.putLong(ARG_ITEM_ID, id)
            val f = ItemUsageFragment()
            f.arguments = args
            return f
        }
    }

    val adapter = BasicListDelegationAdapter(
            ItemCombinationAdapterDelegate(),
            UsageAdapterDelegate()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter(adapter)
        enableDivider()

        val viewModel = ViewModelProvider(activity!!).get(ItemDetailViewModel::class.java)
        viewModel.usageData.observe(viewLifecycleOwner, Observer { usage ->
            usage ?: return@Observer

            adapter.items = usage.combinations + usage.crafting
            adapter.notifyDataSetChanged()
        })
    }
}

/**
 * Internal adapter delegate used to render items on the usage tab
 */
class UsageAdapterDelegate : AbsListItemAdapterDelegate<Component, Any, UsageAdapterDelegate.UsageViewHolder>() {
    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Component
    }

    override fun onCreateViewHolder(parent: ViewGroup): UsageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_component_listitem, parent, false)
        return UsageViewHolder(view)
    }

    override fun onBindViewHolder(component: Component, holder: UsageViewHolder, payloads: MutableList<Any>) {
        holder.bindItem(component)
    }

    class UsageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bindItem(component: Component) {
            // Set up the text view
            val itemLayout = itemView.findViewById<LinearLayout>(R.id.listitem)
            val itemImageView = itemView.findViewById<ImageView>(R.id.item_image)
            val itemTextView = itemView.findViewById<TextView>(R.id.item)
            val amtTextView = itemView.findViewById<TextView>(R.id.amt)
            val typeTextView = itemView.findViewById<TextView>(R.id.type)

            val created = component.created
            val createdId = created.id

            val nameText = created.name
            val amtText = "" + component.quantity
            val typeText = "" + component.type

            itemTextView.text = nameText
            amtTextView.text = amtText
            typeTextView.text = typeText

            AssetLoader.setIcon(itemImageView,created)

            itemLayout.tag = createdId
            itemLayout.setOnClickListener(ItemClickListener(itemView.context, created))
        }
    }
}
