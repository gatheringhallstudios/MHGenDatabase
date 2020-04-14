package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.applyArguments

/**
 * Dialog fragment used to set the number of weapon slots available for an armor set.
 */
class ASBWeaponSlotsDialogFragment : DialogFragment() {
    companion object {
        private const val EXTRA_INPUT_SLOTS = "ASB_WEAPON_SLOTS_INPUT"
        const val EXTRA_WEAPON_SLOTS = "ASB_WEAPON_SLOTS_RESULT"

        @JvmStatic fun newInstance(numSlots: Int): ASBWeaponSlotsDialogFragment {
            return ASBWeaponSlotsDialogFragment().applyArguments {
                putInt(EXTRA_INPUT_SLOTS, numSlots)
            }
        }
    }

    private fun sendResult(numSlots: Int) {
        val intent = Intent()
        intent.putExtra(EXTRA_WEAPON_SLOTS, numSlots)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val checked = arguments?.getInt(EXTRA_INPUT_SLOTS)?.coerceIn(0, 3) ?: 0

        val items = arrayOf(
                context!!.getString(R.string.asb_weapon_slots_none),
                context!!.getString(R.string.asb_weapon_slots_one),
                context!!.getString(R.string.asb_weapon_slots_two),
                context!!.getString(R.string.asb_weapon_slots_three)
        )

        return AlertDialog.Builder(context!!)
                .setSingleChoiceItems(items, checked) { dialog, which ->
                    sendResult(which)
                    dialog.dismiss()
                }.create()
    }
}