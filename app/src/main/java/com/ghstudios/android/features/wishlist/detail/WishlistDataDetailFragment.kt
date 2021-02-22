package com.ghstudios.android.features.wishlist.detail

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import androidx.core.content.ContextCompat
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.WishlistData
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.features.wishlist.detail.WishlistDetailPagerActivity.Companion.DIALOG_WISHLIST_DATA_DELETE
import com.ghstudios.android.features.wishlist.detail.WishlistDetailPagerActivity.Companion.REQUEST_WISHLIST_DATA_DELETE

/**
 * Fragment used to display wishlist items in the wishlist detail.
 */
class WishlistDataDetailFragment : ListFragment() {
    private var mListView: ListView? = null

    /**
     * Returns the viewmodel owned by the activity, which has already loaded wishlist data
     */
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(WishlistDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_wishlist_item_list, container, false)
        mListView = v.findViewById(android.R.id.list)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wishlistItems.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            val adapter = WishlistDataListAdapter(context, it)
            mListView?.adapter = adapter
        })
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        activity!!.menuInflater.inflate(R.menu.context_wishlist_data, menu)
    }

    private inner class WishlistDataListAdapter(
            context: Context?,
            items: List<WishlistData>
    ) : ArrayAdapter<WishlistData>(requireContext(), R.layout.fragment_wishlist_item_listitem, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                inflater.inflate(R.layout.fragment_wishlist_item_listitem,
                        parent, false)
            } else {
                convertView
            }

            val data = getItem(position)

            // Set up the text view
            val root = view.findViewById<LinearLayout>(R.id.listitem)
            val itemImageView = view.findViewById<ImageView>(R.id.item_image)
            val itemTextView = view.findViewById<TextView>(R.id.item)
            val amtTextView = view.findViewById<TextView>(R.id.amt)
            val extraTextView = view.findViewById<TextView>(R.id.extra)

            val id = data!!.item.id
            val nameText = data.item.name
            val amtText = "" + data.quantity

            val extraText = data.path
            val satisfied = data.satisfied

            // Indicate a piece's requirements are met
            if (satisfied == 1) {
                itemTextView.setTextColor(ContextCompat.getColor(context, R.color.light_accent_color))
            } else {
                itemTextView.setTextColor(ContextCompat.getColor(context, R.color.text_color))
            }

            // Assign textviews
            itemTextView.text = nameText
            amtTextView.text = amtText
            extraTextView.text = extraText

            AssetLoader.setIcon(itemImageView, data.item)

            root.setOnClickListener(ItemClickListener(context, data.item))

            root.setOnLongClickListener { view1 ->
                val dialogDelete = WishlistDataDeleteDialogFragment.newInstance(data.id, nameText)
                dialogDelete.setTargetFragment(null, REQUEST_WISHLIST_DATA_DELETE)
                dialogDelete.show(parentFragmentManager, DIALOG_WISHLIST_DATA_DELETE)
                true
            }

            return view
        }
    }
}
