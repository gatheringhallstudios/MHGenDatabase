package com.ghstudios.android.features.wishlist.external

import android.app.AlertDialog
import android.app.Dialog
import androidx.lifecycle.Observer
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.applyArguments
import java.util.*

enum class WishlistItemType {
    ITEM,
    ARMORSET
}

/**
 * A dialog created to decide which wishlist to add an item to.
 */
class WishlistDataAddDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_WISHLIST_TYPE = "WISHLIST_DATA_TYPE"
        private const val ARG_WISHLIST_DATA_ID = "WISHLIST_DATA_ID"
        private const val ARG_WISHLIST_DATA_WEAPON_NAME = "WISHLIST_DATA_WEAPON_NAME"

        /**
         * Creates a new instance of this dialog to decide which wishlist to add an item to.
         * This constructor is for just items
         * @param id
         * @param name
         */
        @JvmStatic fun newInstance(id: Long, name: String)
                = newInstance(WishlistItemType.ITEM, id, name)

        @JvmStatic fun newInstance(type: WishlistItemType, id: Long, name: String): WishlistDataAddDialogFragment {
            return WishlistDataAddDialogFragment().applyArguments {
                putSerializable(ARG_WISHLIST_TYPE, type)
                putLong(ARG_WISHLIST_DATA_ID, id)
                putString(ARG_WISHLIST_DATA_WEAPON_NAME, name) // currently unused?
            }
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WishlistAddItemViewModel::class.java)
    }

    private val TAG = javaClass.simpleName

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel.loadWishlists()

        val itemType = arguments?.getSerializable(ARG_WISHLIST_TYPE) as WishlistItemType
        val itemId = arguments?.getLong(ARG_WISHLIST_DATA_ID) ?: -1
        viewModel.setItem(itemType, itemId)

        return showSelectWishlistDialog()
    }

    override fun onResume() {
        // reload wishlists on resume, in case there any changes
        viewModel.loadWishlists()

        super.onResume()
    }

    private fun showSelectWishlistDialog(): Dialog {
        val inflater = LayoutInflater.from(this.context)
        val dialogView = inflater.inflate(R.layout.dialog_wishlist_data_add, null)
        val wishlistSelect = dialogView.findViewById<Spinner>(R.id.wishlist_select)
        val wishlistNameEntry = dialogView.findViewById<EditText>(R.id.wishlist_name)
        val pathSelect = dialogView.findViewById<RadioGroup>(R.id.path_select)
        val quantityInput = dialogView.findViewById<EditText>(R.id.add)

        // observe the list of wishlists. If there are any changes, update
        viewModel.allWishlists.observe(this, Observer { wishlists ->
            if (wishlists == null) return@Observer // not loaded

            if (wishlists.isEmpty()) {
                // There's no wishlist, so ask the user to enter one
                wishlistSelect.visibility = View.GONE
                wishlistNameEntry.visibility = View.VISIBLE
            } else {
                // Bind selectable wishlists
                wishlistSelect.visibility = View.VISIBLE
                wishlistNameEntry.visibility = View.GONE

                wishlistSelect.adapter = ArrayAdapter(
                        this.context!!,
                        R.layout.support_simple_spinner_dropdown_item,
                        wishlists.map { it.name }.toTypedArray())
            }
        })

        // Observe paths and add them to the path selection area
        // Paths require unique ids, so we assign them IDs from the ids.xml file
        // Also select the first one
        viewModel.itemPaths.observe(viewLifecycleOwner, Observer { paths ->
            if (paths == null) return@Observer // not loaded

            // Pre-pull radio buttons (adding programmatically has errors in older Android Versions)
            val availableButtons = ArrayDeque<RadioButton>(listOf(
                    pathSelect.findViewById(R.id.path_1),
                    pathSelect.findViewById(R.id.path_2)
            ))

            if (paths.isNotEmpty()) {
                for (path in paths) {
                    availableButtons.pop().apply {
                        this.text = path
                        tag = path
                        visibility = View.VISIBLE
                    }
                }

                // check the first item
                pathSelect.check(pathSelect.getChildAt(0).id)
            }
        })


        // Determine the title for the dialog
        val title = when (viewModel.itemType) {
            WishlistItemType.ITEM -> getString(R.string.wishlist_add_title)
            WishlistItemType.ARMORSET -> getString(R.string.wishlist_add_set_title)
        }

        // Create the dialog
        val dialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing here, since we want to be able to show errors and cancel closing
                }
                .create()

        // Handles the "ok" input option. We put it here so that we can validate w/o closing the dialog.
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                try {
                    // get path
                    val selectedPathButtonId = pathSelect.checkedRadioButtonId
                    val pathItem = pathSelect.findViewById<RadioButton>(selectedPathButtonId)
                    val path = pathItem?.tag as? String ?: "Create"

                    // get quantity value
                    val quantity = quantityInput.text.toString().toIntOrNull()

                    val result = when (wishlistNameEntry.visibility == View.VISIBLE) {
                        true -> {
                            // making a new wishlist
                            val name = wishlistNameEntry.text.toString().trim()
                            viewModel.addToWishlist(name, quantity, path)
                        }
                        false -> {
                            val idx = wishlistSelect.selectedItemPosition
                            viewModel.addToWishlist(idx, quantity, path)
                        }
                    }

                    // test the result...and close if its a success
                    when (result) {
                        is WishlistSuccessResult -> {
                            // Show success message.
                            val message = getString(R.string.wishlist_add_success, result.wishlistName)
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                            // We're done, exit
                            dialog.dismiss()
                        }
                        is WishlistErrorResult -> {
                            Toast.makeText(activity, result.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (ex: Exception) {
                    Toast.makeText(activity, R.string.wishlist_error_unknown, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "ERROR While adding item to wishlist", ex)
                }
            }
        }

        return dialog
    }
}
