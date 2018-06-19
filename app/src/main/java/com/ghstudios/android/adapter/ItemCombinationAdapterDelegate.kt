package com.ghstudios.android.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.ghstudios.android.ClickListeners.BasicItemClickListener
import com.ghstudios.android.MHUtils
import com.ghstudios.android.data.classes.Combining
import com.ghstudios.android.mhgendatabase.R
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

/**
 * An adapter delegate that can be added to any adapter delegate adapter.
 * Renders item combination information
 */
class ItemCombinationAdapterDelegate: AbsListItemAdapterDelegate<Combining, Any, ItemCombinationAdapterDelegate.CombinationViewHolder>() {
    override fun isForViewType(item: Any, items: List<Any>, position: Int): Boolean {
        return item is Combining
    }

    override fun onCreateViewHolder(parent: ViewGroup): CombinationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_combining_listitem, parent, false)
        return CombinationViewHolder(view)
    }

    override fun onBindViewHolder(combination: Combining, holder: CombinationViewHolder, payloads: MutableList<Any>) {
        holder.bindItem(combination)
    }

    class CombinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.item_text1) lateinit var itemtv1: TextView
        @BindView(R.id.item_text2) lateinit var itemtv2: TextView
        @BindView(R.id.item_text3) lateinit var itemtvResult: TextView

        @BindView(R.id.item_img1) lateinit var itemiv1: ImageView
        @BindView(R.id.item_img2) lateinit var itemiv2: ImageView
        @BindView(R.id.item_img3) lateinit var itemivResult: ImageView

        @BindView(R.id.item1) lateinit var itemlayout1 : LinearLayout
        @BindView(R.id.item2) lateinit var itemlayout2: LinearLayout
        @BindView(R.id.item3) lateinit var itemlayoutResult: RelativeLayout

        @BindView(R.id.percentage) lateinit var percenttv : TextView
        @BindView(R.id.amt) lateinit var amttv : TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bindItem(item : Combining) {
            val context = itemView.context

            val cellImage1 = "icons_items/" + item.item1.fileLocation
            val cellImage2 = "icons_items/" + item.item2.fileLocation
            val cellImage3 = "icons_items/" + item.createdItem.fileLocation

            val i1 = MHUtils.loadAssetDrawable(context, cellImage1)
            val i2 = MHUtils.loadAssetDrawable(context, cellImage2)
            val i3 = MHUtils.loadAssetDrawable(context, cellImage3)

            itemiv1.setImageDrawable(i1)
            itemiv2.setImageDrawable(i2)
            itemivResult.setImageDrawable(i3)

            itemtv1.text = item.item1.name
            itemtv2.text = item.item2.name
            itemtvResult.text = item.createdItem.name

            val percentage = "${item.percentage}%"
            percenttv.text = percentage

            val min = item.amountMadeMin
            val max = item.amountMadeMax
            amttv.text = when (min == max) {
                true -> min.toString()
                false -> "$min-$max"
            }

            itemlayout1.setOnClickListener(BasicItemClickListener(context, item.item1.id))
            itemlayout2.setOnClickListener(BasicItemClickListener(context, item.item2.id))
            itemlayoutResult.setOnClickListener(BasicItemClickListener(context, item.createdItem.id))
        }
    }
}