package com.ghstudios.android.features.wishlist.list

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.ghstudios.android.ClickListeners.WishlistClickListener
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.adapter.common.SimpleDiffRecyclerViewAdapter
import com.ghstudios.android.adapter.common.SimpleViewHolder
import com.ghstudios.android.adapter.common.SwipeReorderTouchHelper
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.features.wishlist.detail.WishlistRenameDialogFragment
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.createSnackbarWithUndo

/** Adapter used to render wishlists in a recyclerview */
class WishlistAdapter: SimpleDiffRecyclerViewAdapter<Wishlist>() {
    override fun areItemsTheSame(oldItem: Wishlist, newItem: Wishlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun onCreateView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.fragment_wishlistmain_listitem, parent, false)
    }

    override fun bindView(viewHolder: SimpleViewHolder, data: Wishlist) {
        val view = viewHolder.itemView
        val wishlist = data

        // Set up the views
        val itemLayout = view.findViewById<View>(R.id.listitem) as LinearLayout
        val wishlistNameTextView = view.findViewById<View>(R.id.item_name) as TextView
        view.findViewById<View>(R.id.item_image).visibility = View.GONE

        // Bind views
        val cellText = wishlist.name
        wishlistNameTextView.text = cellText

        // Assign view tag and listeners
        view.tag = wishlist.id
        itemLayout.tag = wishlist.id
        itemLayout.setOnClickListener(WishlistClickListener(viewHolder.context, wishlist.id))
    }
}

/**
 * Fragment used to display and manage a collection of wishlists
 */
class WishlistListFragment : RecyclerViewFragment() {
    companion object {
        const val DIALOG_WISHLIST_ADD = "wishlist_add"
        const val DIALOG_WISHLIST_COPY = "wishlist_copy"
        const val DIALOG_WISHLIST_DELETE = "wishlist_delete"
        const val DIALOG_WISHLIST_RENAME = "wishlist_rename"
        const val REQUEST_ADD = 0
        const val REQUEST_RENAME = 1
        const val REQUEST_COPY = 2
        const val REQUEST_DELETE = 3
    }

    val viewModel by lazy {
        ViewModelProviders.of(this).get(WishlistListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableDivider()
        enableFab {
            showAddDialog()
        }

        val adapter = WishlistAdapter()
        setAdapter(adapter)

        val handler = ItemTouchHelper(SwipeReorderTouchHelper(
                afterSwiped = {
                    val wishlistId = it.itemView.tag as Long
                    val message = getString(R.string.wishlist_deleted)
                    val operation = viewModel.startDeleteWishlist(wishlistId)
                    val containerView = view.findViewById<ViewGroup>(R.id.recyclerview_container_main)

                    containerView.createSnackbarWithUndo(message, operation)
                }
        ))
        handler.attachToRecyclerView(recyclerView)

        viewModel.wishlistData.observe(this, Observer {
            if (it == null) return@Observer
            adapter.setItems(it)
            showEmptyView(show = it.isEmpty())
        })
    }

    private fun showAddDialog() {
        val dialog = WishlistAddDialogFragment()
        dialog.setTargetFragment(this@WishlistListFragment, REQUEST_ADD)
        dialog.show(fragmentManager, DIALOG_WISHLIST_ADD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_ADD) {
            if (data!!.getBooleanExtra(WishlistAddDialogFragment.EXTRA_ADD, false)) {
                updateUI()
            }
        } else if (requestCode == REQUEST_RENAME) { // not used here
            if (data!!.getBooleanExtra(WishlistRenameDialogFragment.EXTRA_RENAME, false)) {
                updateUI()
            }
        } else if (requestCode == REQUEST_COPY) { // might be used here
            if (data!!.getBooleanExtra(WishlistCopyDialogFragment.EXTRA_COPY, false)) {
                updateUI()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Check for dataset changes when the activity is resumed.
        // Not the best practice but the list will always be small.
        viewModel.reload()
    }

    private fun updateUI() {
        viewModel.reload()
    }
}
