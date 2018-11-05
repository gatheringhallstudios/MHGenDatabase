package com.ghstudios.android.features.armor.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.components.SlotsView
import com.ghstudios.android.data.classes.ArmorFamily
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.setImageAsset
import kotlinx.android.synthetic.main.listitem_armor_family.view.*
import kotlinx.android.synthetic.main.listitem_armor_header.view.*

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
        val view = inflater.inflate(R.layout.listitem_armor_header, parent, false)

        view.name_text.text = AssetLoader.localizeRarityLabel(group.rarity)
        view.rank_text.text = when (group.rarity) {
            11 -> view.resources.getString(R.string.monster_class_deviant)
            else -> AssetLoader.localizeRank(Rank.fromArmorRarity(group.rarity))
        }

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val context = parent.context

        val view = when (convertView) {
            null -> LayoutInflater.from(context).inflate(R.layout.listitem_armor_family, parent, false)
            else -> convertView
        }

        val family = getChild(groupPosition, childPosition)

        view.icon.setImageAsset(family)
        view.family_name.text = family.name

        val minDef = view.findViewById<TextView>(R.id.min_defense)
        val maxDef = view.findViewById<TextView>(R.id.max_defense)
        minDef.text = Integer.toString(family.minDef)
        maxDef.text = Integer.toString(family.maxDef)

        // Get views for slots and skills
        val slotViews = arrayOf(R.id.slots_1, R.id.slots_2, R.id.slots_3, R.id.slots_4, R.id.slots_5).map { view.findViewById<SlotsView>(it) }
        val skillViews = arrayOf(view.findViewById(R.id.skill_1), view.findViewById(R.id.skill_2), view.findViewById(R.id.skill_3), view.findViewById(R.id.skill_4), view.findViewById<TextView>(R.id.skill_5))

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

        view.tag = family.id
        view.setOnClickListener(ArmorClickListener(context, family))

        return view
    }
}