package com.ghstudios.android.features.armor.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.data.classes.ArmorFamily
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.ListitemArmorFamilyBinding
import com.ghstudios.android.mhgendatabase.databinding.ListitemArmorHeaderBinding
import com.ghstudios.android.util.setImageAsset

class ArmorFamilyGroup(
        val rarity: Int,
        val families: List<ArmorFamily>
)

/**
 * Expandable list adapter used to render a collection of armor families grouped by rarity.
 */
class ArmorFamilyListAdapter(private val groups: List<ArmorFamilyGroup>) : BaseExpandableListAdapter() {
    override fun hasStableIds() = true
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroup(groupPosition: Int) = groups[groupPosition]
    override fun getGroupCount() = groups.size

    override fun getChild(groupPosition: Int, childPosition: Int) = groups[groupPosition].families[childPosition]
    override fun getChildrenCount(groupPosition: Int) = groups[groupPosition].families.size

    override fun getChildId(groupPosition: Int, childPosition: Int) = groupPosition * 100L + childPosition
    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val group = getGroup(groupPosition)

        val inflater = LayoutInflater.from(parent?.context)
        val binding = ListitemArmorHeaderBinding.inflate(inflater, parent, false)

        binding.nameText.text = AssetLoader.localizeRarityLabel(group.rarity)
        binding.rankText.text = when (group.rarity) {
            11 -> binding.root.resources.getString(R.string.monster_class_deviant)
            else -> AssetLoader.localizeRank(Rank.fromArmorRarity(group.rarity))
        }

        return binding.root
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val context = parent.context

        val binding = when (convertView) {
            null -> ListitemArmorFamilyBinding.inflate(LayoutInflater.from(context), parent, false)
            else -> ListitemArmorFamilyBinding.bind(convertView)
        }

        val family = getChild(groupPosition, childPosition)

        with(binding) {
            icon.setImageAsset(family)
            familyName.text = family.name
            binding.minDefense.text = Integer.toString(family.minDef)
            binding.maxDefense.text = Integer.toString(family.maxDef)
        }

        // Get views for slots and skills
        val slotViews = with(binding) {
            arrayOf(slots1, slots2, slots3, slots4, slots5)
        }
        val skillViews = with(binding) {
            arrayOf(skill1, skill2, skill3, skill4, skill5)
        }
        // Initialize, hide all slotsViews and skills
        slotViews.forEach { it.visibility = View.GONE }
        skillViews.forEach { it.visibility = View.GONE }

        // Populate slots
        for ((i, numSlots) in family.slots.withIndex()) {
            val slotsView = slotViews[i]
            slotsView.visibility = View.VISIBLE
            slotsView.setSlots(numSlots, 0)
        }

        // Populate skills
        for ((i, skillString) in family.skills.withIndex()) {
            val skillView = skillViews[i]
            skillView.visibility = View.VISIBLE
            skillView.text = skillString
        }

        binding.root.tag = family.id
        binding.root.setOnClickListener(ArmorClickListener(context, family))

        return binding.root
    }
}
