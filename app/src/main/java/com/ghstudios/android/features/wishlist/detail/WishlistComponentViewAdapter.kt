package com.ghstudios.android.features.wishlist.detail

import androidx.core.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.adapter.common.BaseDiffRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.mhgendatabase.R

typealias WishlistComponentQuantityChanged = (component: WishlistComponent, quantity: Int) -> Unit

/**
 * RecyclerView adapter used to render Wishlist components
 */
class WishlistComponentViewAdapter(
        val onValueChange:  WishlistComponentQuantityChanged
) : BaseDiffRecyclerViewAdapter<WishlistComponent, WishlistComponentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistComponentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fragment_wishlist_component_listitem, parent, false)

        val viewHolder = WishlistComponentViewHolder(view)
        viewHolder.onValueChanged = onValueChange

        return viewHolder
    }

    override fun bindView(viewHolder: WishlistComponentViewHolder, data: WishlistComponent) {
        viewHolder.bindComponent(data)
    }

    override fun areItemsTheSame(oldItem: WishlistComponent, newItem: WishlistComponent): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: WishlistComponent, newItem: WishlistComponent): Boolean {
        return (oldItem.item.id == newItem.item.id
                && oldItem.quantity == newItem.quantity
                && oldItem.notes == newItem.notes)
    }
}


class WishlistComponentViewHolder(containerView: View): SimpleViewHolder(containerView) {
    val spinner = itemView.findViewById<Spinner>(R.id.spinner_component_qty)
    val adapter = ArrayAdapter(itemView.context, R.layout.view_spinner_item, (0..100).toList())

    private val root = itemView.findViewById<ViewGroup>(R.id.listitem)
    private val itemImageView = itemView.findViewById<ImageView>(R.id.item_image)
    private val itemTextView = itemView.findViewById<TextView>(R.id.item_name)
    private val amtTextView = itemView.findViewById<TextView>(R.id.text_qty_required)

    var onValueChanged: WishlistComponentQuantityChanged? = null

    init {
        adapter.setDropDownViewResource(R.layout.view_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Change item color if requirement is met
    private fun updateTextColor(qtyhave: Int, quantityRequired: Int) {
        // Set color component requirement is met
        if (qtyhave >= quantityRequired) {
            itemTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.light_accent_color))
        } else {
            itemTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_color))
        }
    }

    fun bindComponent(data: WishlistComponent) {
        val view = itemView
        val item = data.item

        AssetLoader.setIcon(itemImageView, data.item)
        updateTextColor(data.notes, data.quantity)

        // Assign textviews
        itemTextView.text = item.name
        amtTextView.text = data.quantity.toString()

        /***************** SPINNER FOR QTY_HAVE DISPLAY  */

        // Prevent events from firing
        // Get position of notes (qty_have) and set spinner to that position
        // false prevents animation, preventing a deferred false selection.
        spinner.onItemSelectedListener = null
        val s = adapter.getPosition(data.notes)
        spinner.setSelection(s, false)

        // Set spinner listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long) {
                val newQuantity = spinner.getItemAtPosition(position) as Int
                updateTextColor(newQuantity, data.quantity)
                onValueChanged?.invoke(data, newQuantity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.v("ComponentFragment", "Nothing selected.")
            }
        }

        /********************* END SPINNER  */

        root.setOnClickListener(ItemClickListener(view.context, data.item))
        root.tag = item.id
    }
}