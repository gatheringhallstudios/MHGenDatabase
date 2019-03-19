package com.ghstudios.android.features.wishlist.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView

import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.ItemClickListener

/**
 * Fragment used to display a list of required crafting components for the wishlist detail.
 */
class WishlistDataComponentFragment : ListFragment() {
    private var mListView: ListView? = null

    /**
     * Returns the viewmodel owned by the activity, which has already loaded wishlist data
     */
    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(WishlistDetailViewModel::class.java);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_wishlist_component_list, container, false)
        mListView = v.findViewById(android.R.id.list)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wishlistComponents.observe(this, Observer {
            if (it == null) return@Observer

            val adapter = WishlistComponentAdapter(context, it)
            mListView?.adapter = adapter
        })

        // Bind the total cost value
        val mTotalCostView = view.findViewById<TextView>(R.id.total_cost_value)
        viewModel.priceData.observe(this, Observer {
            mTotalCostView.text = getString(R.string.value_zenny, it ?: 0)
        })
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        mListView?.setItemChecked(position, false)

        val component = mListView?.adapter?.getItem(position) as? WishlistComponent
        if (component != null && v != null) {
            val listener = ItemClickListener(context!!, component.item)
            listener.onClick(v)
        }
    }

    /**
     * Adapter used to render the Wishlist components
     */
    private inner class WishlistComponentAdapter(
            context: Context?,
            items: List<WishlistComponent>
    ) : ArrayAdapter<WishlistComponent>(context, R.layout.fragment_wishlist_component_listitem, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                inflater.inflate(R.layout.fragment_wishlist_component_listitem, parent, false)
            } else {
                convertView
            }

            val component = getItem(position)
            val item = component.item

            // Set up the text view
            val root = view.findViewById<LinearLayout>(R.id.listitem)
            val itemImageView = view.findViewById<ImageView>(R.id.item_image)
            val itemTextView = view.findViewById<TextView>(R.id.item_name)
            val amtTextView = view.findViewById<TextView>(R.id.text_qty_required)

            val componentrowid = component!!.id
            val componentid = item.id
            val qtyreq = component.quantity
            val qtyhave = component.notes

            // Set color component requirement is met
            if (qtyhave >= qtyreq) {
                itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color))
            } else {
                itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color))
            }

            // Assign textviews
            itemTextView.text = item.name
            amtTextView.text = qtyreq.toString()

            /***************** SPINNER FOR QTY_HAVE DISPLAY  */

            // Create spinner and apply values
            val spinner = view.findViewById<Spinner>(R.id.spinner_component_qty)
            val options = (0..100).toList()
            val adapter = ArrayAdapter(context, R.layout.view_spinner_item, options)
            adapter.setDropDownViewResource(R.layout.view_spinner_dropdown_item)
            spinner.adapter = adapter

            // Set spinner listener
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long) {
                    // Edit qtyhave for the component's row
                    viewModel.updateComponentQuantity(componentrowid, position)

                    // Change item color if requirement is met
                    if (spinner.getItemAtPosition(position) as Int >= qtyreq) {
                        itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color))
                    } else {
                        itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color))
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Log.v("ComponentFragment", "Nothing selected.")
                }
            }

            // Get position of notes (qty_have) and set spinner to that position
            val spinnerpos = adapter.getPosition(qtyhave)
            spinner.setSelection(spinnerpos)

            /********************* END SPINNER  */

            AssetLoader.setIcon(itemImageView, component.item)

            root.setOnClickListener(ItemClickListener(context, component.item))
            root.tag = componentid

            return view
        }
    }
}
