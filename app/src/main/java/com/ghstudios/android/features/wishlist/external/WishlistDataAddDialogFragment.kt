package com.ghstudios.android.features.wishlist.external

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.ghstudios.android.data.classes.Wishlist

import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.applyArguments
import com.ghstudios.android.util.toList

/**
 * A dialog created to decide which wishlist to add an item to.
 */
class WishlistDataAddDialogFragment : DialogFragment() {
    private var item_id: Long = 0

    companion object {
        private val REQUEST_PATH = 1

        private val ARG_WISHLIST_DATA_ID = "WISHLIST_DATA_ID"
        private val ARG_WISHLIST_DATA_WEAPON_NAME = "WISHLIST_DATA_WEAPON_NAME"

        /**
         * Creates a new instance of this dialog to decide which wishlist to add an item to.
         * @param id
         * @param name
         */
        @JvmStatic fun newInstance(id: Long, name: String): WishlistDataAddDialogFragment {
            return WishlistDataAddDialogFragment().applyArguments {
                putLong(ARG_WISHLIST_DATA_ID, id)
                putString(ARG_WISHLIST_DATA_WEAPON_NAME, name)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // todo: refactor to a facade
        val wishlists = DataManager.get(activity).queryWishlists().toList { it.wishlist }

        return showSelectWishlistDialog(wishlists)
    }

    private fun showSelectWishlistDialog(wishlists: List<Wishlist>): Dialog {
        item_id = arguments?.getLong(ARG_WISHLIST_DATA_ID) ?: -1

        // todo: move somewhere else
        val paths = DataManager.get(activity).queryComponentCreateImprove(item_id)

        val inflater = LayoutInflater.from(this.context)
        val dialogView = inflater.inflate(R.layout.dialog_wishlist_data_add, null)
        val wishlistSelect = dialogView.findViewById<Spinner>(R.id.wishlist_select)
        val pathSelect = dialogView.findViewById<RadioGroup>(R.id.path_select)
        val quantityInput = dialogView.findViewById<EditText>(R.id.add)

        // Bind selectable wishlists
        wishlistSelect.adapter = ArrayAdapter(
                this.context,
                R.layout.support_simple_spinner_dropdown_item,
                wishlists.map { it.name }.toTypedArray())

        // If there are any "path" items, add them to the path selection area
        // Also select the first one
        if (paths.size >= 1) {
            pathSelect.removeAllViews()
            for (path in paths) {
                pathSelect.addView(RadioButton(context).apply {
                    this.text = path
                    tag = path
                })
            }

            pathSelect.check(pathSelect.getChildAt(0).id)
        }

        return AlertDialog.Builder(activity)
                .setTitle(getString(R.string.wishlist_add_title))
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, id ->
                    val dataManager = DataManager.get(activity)

                    // Pull configured data. We don't validate, instead preferring reasonable defaults
                    // todo: if its possible to cancel the dialog from closing, perform validations
                    val wishlistIdx = wishlistSelect.selectedItemPosition
                    val wishlist = wishlists[wishlistIdx]
                    val pathItem = pathSelect.findViewById<RadioButton>(pathSelect.checkedRadioButtonId)
                    val path = pathItem?.tag as? String ?: "Create"
                    val quantity = Math.max(1, quantityInput.text.toString().toIntOrNull() ?: 1)

                    // Add to wishlist
                    dataManager.queryAddWishlistData(wishlist.id, item_id, quantity, path)

                    // Show success message.
                    val message = getString(R.string.wishlist_add_success, wishlist.name)
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }
                .create()
    }

}
