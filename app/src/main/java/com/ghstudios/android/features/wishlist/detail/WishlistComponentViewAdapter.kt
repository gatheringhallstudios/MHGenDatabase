package com.ghstudios.android.features.wishlist.detail

import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.adapter.common.SimpleDiffRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.mhgendatabase.R

/**
 * RecyclerView adapter used to render Wishlist components
 */
class WishlistComponentViewAdapter(
        val onValueChange: (component: WishlistComponent, value: Int) -> Unit
) : SimpleDiffRecyclerViewAdapter<WishlistComponent>() {
    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.fragment_wishlist_component_listitem, parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, data: WishlistComponent) {

        val view = viewHolder.itemView
        val component = data
        val item = component.item

        // Set up the text view
        val root = view.findViewById<LinearLayout>(R.id.listitem)
        val itemImageView = view.findViewById<ImageView>(R.id.item_image)
        val itemTextView = view.findViewById<TextView>(R.id.item_name)
        val amtTextView = view.findViewById<TextView>(R.id.text_qty_required)

        val qtyreq = component.quantity

        // Change item color if requirement is met
        fun updateTextColor(qtyhave: Int) {
            // Set color component requirement is met
            if (qtyhave >= qtyreq) {
                itemTextView.setTextColor(ContextCompat.getColor(view.context, R.color.light_accent_color))
            } else {
                itemTextView.setTextColor(ContextCompat.getColor(view.context, R.color.text_color))
            }
        }

        updateTextColor(component.notes)

        // Assign textviews
        itemTextView.text = item.name
        amtTextView.text = qtyreq.toString()

        /***************** SPINNER FOR QTY_HAVE DISPLAY  */

        // Create spinner and apply values
        val spinner = view.findViewById<Spinner>(R.id.spinner_component_qty)
        val options = (0..100).toList()
        val adapter = ArrayAdapter(view.context, R.layout.view_spinner_item, options)
        adapter.setDropDownViewResource(R.layout.view_spinner_dropdown_item)

        // Prevent events from firing, and then set the adapter
        spinner.onItemSelectedListener = null
        spinner.adapter = adapter

        // Get position of notes (qty_have) and set spinner to that position
        val spinnerpos = adapter.getPosition(component.notes)
        spinner.setSelection(spinnerpos, false) // false prevents animation, preventing a deferred false selection.

        // Set spinner listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long) {
                val newQuantity = spinner.getItemAtPosition(position) as Int
                updateTextColor(newQuantity)
                onValueChange(component, newQuantity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.v("ComponentFragment", "Nothing selected.")
            }
        }

        /********************* END SPINNER  */

        AssetLoader.setIcon(itemImageView, component.item)

        root.setOnClickListener(ItemClickListener(view.context, component.item))
        root.tag = item.id
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