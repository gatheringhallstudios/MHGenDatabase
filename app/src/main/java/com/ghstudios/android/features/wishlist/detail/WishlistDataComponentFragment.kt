package com.ghstudios.android.features.wishlist.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v4.widget.CursorAdapter
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.cursors.WishlistComponentCursor
import com.ghstudios.android.features.armor.detail.ArmorSetDetailPagerActivity
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity
import com.ghstudios.android.features.items.detail.ItemDetailPagerActivity
import com.ghstudios.android.features.weapons.detail.WeaponDetailPagerActivity
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.ItemClickListener

import java.util.ArrayList

/**
 * Fragment used to display a list of components for the wishlist detail, which are used to craft the item.
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
            mTotalCostView.text = (it ?: 0).toString()
        })
    }


    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        // The id argument will be the Item ID; CursorAdapter gives us this
        // for free

        mListView!!.setItemChecked(position, false)
        var i: Intent? = null
        val mId = v!!.tag as Long

        val component: WishlistComponent?

        val mycursor = l!!.getItemAtPosition(position) as WishlistComponentCursor
        component = mycursor.wishlistComponent
        val itemtype = component!!.item.type

        // todo: Find a way to create intents using only the item. Why can't we use ItemClickListener?
        when (itemtype) {
            ItemType.WEAPON -> {
                i = Intent(activity, WeaponDetailPagerActivity::class.java)
                i.putExtra(WeaponDetailPagerActivity.EXTRA_WEAPON_ID, mId)
            }
            ItemType.ARMOR -> {
                i = Intent(activity, ArmorSetDetailPagerActivity::class.java)
                i.putExtra(ArmorSetDetailPagerActivity.EXTRA_ARMOR_ID, mId)
            }
            ItemType.DECORATION -> {
                i = Intent(activity, DecorationDetailActivity::class.java)
                i.putExtra(DecorationDetailActivity.EXTRA_DECORATION_ID, mId)
            }
            else -> {
                i = Intent(activity, ItemDetailPagerActivity::class.java)
                i.putExtra(ItemDetailPagerActivity.EXTRA_ITEM_ID, mId)
            }
        }

        startActivity(i)

    }

    /**
     * Adapter used to render the Wishlist components
     */
    private class WishlistComponentAdapter(
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

            // Set up the text view
            val root = view.findViewById<LinearLayout>(R.id.listitem)
            val itemImageView = view.findViewById<ImageView>(R.id.item_image)
            val itemTextView = view.findViewById<TextView>(R.id.item_name)
            val amtTextView = view.findViewById<TextView>(R.id.text_qty_required)

            val componentrowid = component!!.id
            val componentid = component.item.id
            val qtyreq = component.quantity
            val qtyhave = component.notes

            // Set color component requirement is met
            if (qtyhave >= qtyreq) {
                itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color))
            } else {
                itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color))
            }

            val nameText = component.item.name
            val amtText = "" + qtyreq

            // Assign textviews
            itemTextView.text = nameText
            amtTextView.text = amtText

            /***************** SPINNER FOR QTY_HAVE DISPLAY  */

            // Assign Spinner
            val spinner = view.findViewById<Spinner>(R.id.spinner_component_qty)
            // Create an ArrayAdapter containing all possible values for spinner, 0 -> quantity
            val options = ArrayList<Int>()
            for (i in 0..100) {
                options.add(i)
            }
            val adapter = ArrayAdapter(context, R.layout.view_spinner_item, options)
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.view_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter

            val onSpinner = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long) {

                    // Edit qtyhave for the component's row
                    DataManager.get().wishlistManager.queryUpdateWishlistComponentNotes(componentrowid, position)

                    // Change item color if requirement is met
                    if (spinner.getItemAtPosition(position) as Int >= qtyreq) {
                        itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color))
                    } else {
                        itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color))
                    }
                }

                override fun onNothingSelected(
                        parent: AdapterView<*>) {
                    Log.v("ComponentFragment", "Nothing selected.")
                }
            }

            // Set spinner listener
            spinner.onItemSelectedListener = onSpinner

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
