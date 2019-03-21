package com.ghstudios.android.features.armorsetbuilder.talismans

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.features.armorsetbuilder.detail.ASBDetailPagerActivity
import com.ghstudios.android.features.armorsetbuilder.detail.ASBTalismanSkillContainer
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.skills.detail.SkillTreeDetailPagerActivity
import com.ghstudios.android.util.sendDialogResult
import java.io.Serializable
import java.lang.NumberFormatException

import java.util.ArrayList

/**
 * Data class used to encapsulate talisman data before it is created into a real talisman object
 */
data class TalismanMetadata(
        val id: Long,
        val typeIndex: Int,
        val numSlots: Int,
        val skills: List<Pair<Long, Int>>
): Serializable

/**
 * Fragment used to add or edit a talisman. The dialog result is a TalismanMetadata object stored in EXTRA_TALISMAN.
 */
class ASBTalismanDialogFragment : DialogFragment(), ASBTalismanSkillContainer.ChangeListener {
    companion object {
        const val EXTRA_TALISMAN = "EXTRA_TALISMAN"

        private const val ARG_ID = "id"
        private const val ARG_TYPE_INDEX = "type_index"
        private const val ARG_SLOTS = "slots"
        private const val ARG_SKILL_1_ID = "skill_1_id"
        private const val ARG_SKILL_1_POINTS = "skill_1_points"
        private const val ARG_SKILL_2_ID = "skill_2_id"
        private const val ARG_SKILL_2_POINTS = "skill_2_points"

        fun newInstance(): ASBTalismanDialogFragment {
            return ASBTalismanDialogFragment()
        }

        /**
         * Used when creating a talisman dialog for a talisman that has already been created.
         * If talisman is null, returns the normal fragment
         */
        fun newInstance(talisman: ASBTalisman?): ASBTalismanDialogFragment {
            if (talisman == null) {
                return newInstance()
            }

            val f = ASBTalismanDialogFragment()

            val args = Bundle()
            args.putLong(ARG_ID, talisman.id)
            args.putInt(ARG_TYPE_INDEX, talisman.typeIndex)
            args.putInt(ARG_SLOTS, talisman.numSlots)
            args.putLong(ARG_SKILL_1_ID, talisman.firstSkill?.skillTree?.id ?: -1)
            args.putInt(ARG_SKILL_1_POINTS, talisman.firstSkill?.points ?: 0)
            args.putLong(ARG_SKILL_2_ID, talisman.secondSkill?.skillTree?.id ?: -1)
            args.putInt(ARG_SKILL_2_POINTS, talisman.secondSkill?.points ?: 0)

            f.arguments = args

            return f
        }
    }

    private var talismanSkillContainers: Array<ASBTalismanSkillContainer?> = arrayOfNulls(2)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val addView = inflater.inflate(R.layout.dialog_asb_edit_talisman, null)

        talismanSkillContainers[0] = addView.findViewById<View>(R.id.skill_1_view) as ASBTalismanSkillContainer
        talismanSkillContainers[1] = addView.findViewById<View>(R.id.skill_2_view) as ASBTalismanSkillContainer
        for (c in talismanSkillContainers) {
            c?.setParent(this)
            c?.setChangeListener(this)
        }

        val typeSpinner = initializeTypeSpinner(addView)
        val slotsSpinner = initializeSlotsSpinner(addView)

        arguments?.let { arguments ->  // If the talisman is already defined, we initialize it here.
            typeSpinner.setSelection(arguments.getInt(ARG_TYPE_INDEX))
            slotsSpinner.setSelection(arguments.getInt(ARG_SLOTS))
            talismanSkillContainers[0]?.setSkillTree(arguments.getLong(ARG_SKILL_1_ID))
            talismanSkillContainers[0]?.setSkillPoints(arguments.getInt(ARG_SKILL_1_POINTS))

            if (arguments.getLong(ARG_SKILL_2_ID) != -1L) {
                talismanSkillContainers[1]?.setSkillTree(arguments.getLong(ARG_SKILL_2_ID))
                talismanSkillContainers[1]?.setSkillPoints(arguments.getInt(ARG_SKILL_2_POINTS))
            }
        }

        updateSkillEnabledStates()

        val d = AlertDialog.Builder(activity)
                .setTitle(R.string.asb_dialog_talisman_title)
                .setView(addView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    fun parseIntSafe(str: String): Int {
                        return try {
                            Integer.parseInt(str)
                        } catch (ex: NumberFormatException) {
                            0
                        }
                    }

                    if (talismanSkillContainers[0]?.skillTree != null) {
                        val skill1Id = talismanSkillContainers[0]!!.skillTree.id
                        val skill1Points = parseIntSafe(talismanSkillContainers[0]!!.skillPoints)
                        val skill2Id = talismanSkillContainers[1]?.skillTree?.id ?: -1
                        val skill2Points = parseIntSafe(talismanSkillContainers[1]!!.skillPoints)

                        val talismanMetadata = TalismanMetadata(
                                id = arguments?.getLong(ARG_ID, -1) ?: -1,
                                typeIndex = typeSpinner.selectedItemPosition,
                                numSlots = slotsSpinner.selectedItemPosition,
                                skills = listOf(
                                        Pair(skill1Id, skill1Points),
                                        Pair(skill2Id, skill2Points)
                                )
                        )

                        val data = Intent()
                        data.putExtra(EXTRA_TALISMAN, talismanMetadata)
                        sendDialogResult(Activity.RESULT_OK, data)
                    }
                }
                .create()
        d.setOnShowListener {
            updateOkButtonState() // At first, there is no data in the dialog, but there may be if the user is choosing to edit
        }

        return d
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == ASBDetailPagerActivity.REQUEST_CODE_CREATE_TALISMAN) {

            val talismanSkillNumber = data!!.getIntExtra(ASBDetailPagerActivity.EXTRA_TALISMAN_SKILL_INDEX, -1)
            val skillTreeId = data.getLongExtra(SkillTreeDetailPagerActivity.EXTRA_SKILLTREE_ID, -1)

            talismanSkillContainers[talismanSkillNumber]?.skillTree = DataManager.get().getSkillTree(skillTreeId)
            talismanSkillContainers[talismanSkillNumber]?.requestFocus()
        }
    }

    override fun onTalismanSkillChanged() {
        updateSkillEnabledStates()
        updateOkButtonState()
    }

    override fun onTalismanSkillPointsChanged() {
        if (dialog != null) {
            updateOkButtonState()
        }
    }

    /**
     * Updates the enabled status of the second skill tree based on the first.
     */
    private fun updateSkillEnabledStates() {
        if (talismanSkillContainers[0]?.skillTree != null) {
            talismanSkillContainers[1]?.isEnabled = true
        } else {
            if (talismanSkillContainers[1]?.skillTree != null) {
                talismanSkillContainers[1]?.skillTree = null
            }
            talismanSkillContainers[1]?.isEnabled = false
        }
    }

    /**
     * Checks to see that all necessary data is defined before the user attempts to submit their talisman.
     */
    private fun updateOkButtonState() {
        val d = dialog as? AlertDialog

        if (d != null) {
            if (talismanSkillContainers[0]?.skillTree == null || !talismanSkillContainers[0]!!.skillPointsIsValid()) {
                d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                return
            } else if (talismanSkillContainers[1]?.skillTree != null) {
                if (!talismanSkillContainers[1]!!.skillPointsIsValid() || talismanSkillContainers[0]?.skillTree?.id == talismanSkillContainers[1]?.skillTree?.id) {
                    d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    return
                }
            }

            d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        }
    }

    /**
     * Helper method that performs initialization logic on the "type of talisman" spinner.
     */
    private fun initializeTypeSpinner(view: View): Spinner {
        val talismanNames = ArrayList<String>()

        for (s in resources.getStringArray(R.array.talisman_names)) {
            val name = s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            talismanNames.add(name)
        }

        val spinner = view.findViewById<View>(R.id.talisman_rank_spinner) as Spinner
        spinner.adapter = ArrayAdapter(activity!!, R.layout.support_simple_spinner_dropdown_item,
                talismanNames)

        return spinner
    }

    private fun initializeSlotsSpinner(view: View): Spinner {
        val spinner = view.findViewById<View>(R.id.talisman_slots_spinner) as Spinner
        spinner.adapter = ArrayAdapter.createFromResource(activity!!, R.array.slot_values,
                android.R.layout.simple_spinner_dropdown_item)
        (spinner.adapter as ArrayAdapter<*>).setDropDownViewResource(R.layout.view_spinner_dropdown_item)

        return spinner
    }
}
