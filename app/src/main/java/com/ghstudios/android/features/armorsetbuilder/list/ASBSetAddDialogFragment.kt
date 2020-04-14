package com.ghstudios.android.features.armorsetbuilder.list

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ASBSet

import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.applyArguments
import com.ghstudios.android.util.sendDialogResult

/**
 * Dialog used to add or edit an ASBSet or Session.
 */
class ASBSetAddDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_ID = "id"
        private const val ARG_NAME = "name"
        private const val ARG_RANK = "rank"
        private const val ARG_HUNTER_TYPE = "hunter_type"

        @JvmStatic fun newInstance(): ASBSetAddDialogFragment {
            val f = ASBSetAddDialogFragment()
            f.isEditing = false
            return f
        }

        @JvmStatic fun newInstance(set: ASBSet)
                = newInstance(set.id, set.name, set.rank, set.hunterType)

        @JvmStatic fun newInstance(id: Long, name: String, rank: Rank, hunterType: Int): ASBSetAddDialogFragment {
            val f = ASBSetAddDialogFragment().applyArguments {
                putLong(ARG_ID, id)
                putString(ARG_NAME, name)
                putInt(ARG_RANK, rank.value)
                putInt(ARG_HUNTER_TYPE, hunterType)
            }

            f.isEditing = true

            return f
        }
    }

    private var isEditing: Boolean = false

    private fun sendResult(resultCode: Int, name: String, rank: Rank, hunterType: Int) {
        val i = Intent()
        if (isEditing) {
            i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, arguments!!.getLong(ARG_ID))
        }
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_NAME, name)
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_RANK, rank.value)
        i.putExtra(ASBSetListFragment.EXTRA_ASB_SET_HUNTER_TYPE, hunterType)

        sendDialogResult(resultCode, i)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_asb_set_add, null)

        val nameInput = view.findViewById<EditText>(R.id.name_text)
        val rankSpinner = view.findViewById<Spinner>(R.id.spinner_rank)
        val hunterTypeSpinner = view.findViewById<Spinner>(R.id.spinner_hunter_type)

        val ranks = Rank.all
        val rankStrings = ranks.map { AssetLoader.localizeRank(it) }.toTypedArray()

        rankSpinner.adapter = ArrayAdapter<String>(context!!, R.layout.view_spinner_item, rankStrings).apply {
            setDropDownViewResource(R.layout.view_spinner_dropdown_item)
        }

        hunterTypeSpinner.adapter = ArrayAdapter.createFromResource(context!!, R.array.hunter_type, R.layout.view_spinner_item).apply {
            setDropDownViewResource(R.layout.view_spinner_dropdown_item)
        }

        if (isEditing) {
            val selectedRank = Rank.from(arguments?.getInt(ARG_RANK) ?: -1)

            nameInput.setText(arguments?.getString(ARG_NAME) ?: "")
            rankSpinner.setSelection(ranks.indexOf(selectedRank))
            hunterTypeSpinner.setSelection(arguments!!.getInt(ARG_HUNTER_TYPE))
        }

        val d = AlertDialog.Builder(activity)
                .setTitle(if (!isEditing) R.string.dialog_title_add_asb_set else R.string.dialog_title_edit_asb_set)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val name = nameInput.text.toString()

                    val rankIdx = rankSpinner.selectedItemPosition
                    val hunterTypeIdx = hunterTypeSpinner.selectedItemPosition
                    sendResult(Activity.RESULT_OK, name, ranks[rankIdx], hunterTypeIdx)
                }
                .create()

        // Allow the auto-focused name input to pop up the onscreen keyboard
        val window = d.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return d
    }
}
