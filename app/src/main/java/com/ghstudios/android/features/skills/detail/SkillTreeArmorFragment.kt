package com.ghstudios.android.features.skills.detail

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.components.SlotsView
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.util.applyArguments
import com.ghstudios.android.util.setImageAsset

/**
 * Fragment used to display a list of armor that have at least one point in a particular skill
 */
class SkillTreeArmorFragment : ListFragment() {

    companion object {
        private val ARG_SKILL = "SKILLTREE_SKILL"
        private val ARG_TYPE = "SKILLTREE_TYPE"

        @JvmStatic fun newInstance(skill: Long, armorType: String): SkillTreeArmorFragment {
            return SkillTreeArmorFragment().applyArguments {
                putLong(ARG_SKILL, skill)
                putString(ARG_TYPE, armorType)
            }
        }
    }

    /**
     * ViewModel belonging to the parent activity
     */
    private val parentViewModel by lazy {
        ViewModelProvider(activity!!).get(SkillDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // required for ListFragment

        val mType = arguments!!.getString(ARG_TYPE) ?: ""

        val adapter = ArmorToSkillTreeListAdapter(context!!)
        listAdapter = adapter

        parentViewModel.observeArmorsWithSkill(viewLifecycleOwner, mType, Observer {
            if (it != null) {
                adapter.updateItems(it)
            }
        })
    }

    /**
     * Internal cursor adapter to display armors that provide the specified skill
     */
    private class ArmorToSkillTreeListAdapter(context: Context)
        : ArrayAdapter<ItemToSkillTree>(context, R.layout.listitem_skill_armor) {

        fun updateItems(newItems: List<ItemToSkillTree>) {
            clear()
            addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = when (convertView) {
                null -> {
                    val inflater = LayoutInflater.from(context)
                    inflater.inflate(R.layout.listitem_skill_armor, parent, false)
                }
                else -> convertView
            }

            // Get the skill for the current row
            val skill = checkNotNull(getItem(position))
            val armor = skill.item as Armor

            // Set up the text view
            val skillItemImageView = view.findViewById<ImageView>(R.id.item_image)
            val hunterTypeImageView = view.findViewById<ImageView>(R.id.hunter_type_image)
            val skillItemTextView = view.findViewById<TextView>(R.id.item)
            val skillAmtTextView = view.findViewById<TextView>(R.id.amt)
            val minDefView = view.findViewById<TextView>(R.id.min_defense)
            val maxDefView = view.findViewById<TextView>(R.id.max_defense)
            val slotsView = view.findViewById<SlotsView>(R.id.slots)

            val hunterTypeResId = when (armor.hunterType) {
                Armor.ARMOR_TYPE_BLADEMASTER -> R.drawable.icon_great_sword
                Armor.ARMOR_TYPE_GUNNER -> R.drawable.icon_heavy_bowgun
                else -> 0
            }

            if (hunterTypeResId != 0) {
                hunterTypeImageView.setImageResource(hunterTypeResId)
            }

            skillItemImageView.setImageAsset(armor)
            skillItemTextView.text = armor.name
            skillAmtTextView.text = skill.points.toString()
            minDefView.text = armor.defense.toString()
            maxDefView.text = armor.maxDefense.toString()
            slotsView.setSlots(armor.numSlots, 0)

            view.tag = armor.id
            view.setOnClickListener(ArmorClickListener(context, armor))

            return view
        }
    }
}
