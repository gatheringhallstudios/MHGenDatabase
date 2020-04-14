package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.sendDialogResult
import java.lang.IllegalStateException

/**
 * Defines a dialog used to add an Armor set to a wishlist.
 * Currently only pulls a name.
 */
class ASBAddToWishlistDialog: DialogFragment() {
    companion object {
        const val EXTRA_NAME = "WISHLIST_NAME"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_asb_add_to_wishlist, null)

        val editText = view.findViewById<EditText>(R.id.wishlist_name)

        val builder = AlertDialog.Builder(activity)
        val dialog = builder.setTitle(R.string.option_wishlist_add)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) {_, _ ->}
                .create()

        // Handle on success, prevent dialog closing if invalid
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val newWishlistName = editText.text.toString()
                if (newWishlistName.isEmpty()) {
                    return@setOnClickListener
                }

                val intent = Intent()
                intent.putExtra(EXTRA_NAME, newWishlistName)
                sendDialogResult(Activity.RESULT_OK, intent)
                dialog.dismiss()
            }
        }

        return dialog
    }
}