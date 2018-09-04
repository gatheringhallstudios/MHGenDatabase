package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.ghstudios.android.data.classes.ArmorSkillPoints
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.setImageAsset
import kotlinx.android.synthetic.main.fragment_armor_expandablelist_group_item.view.*
import kotlinx.android.synthetic.main.fragment_armor_set_piece_listitem.view.*

class ArmorGroup(
        val name: String,
        val armor: List<ArmorSkillPoints>
)

/**
 * Creates an adapter to display a collapsible list of armor.
 * Uses ListView instead of RecyclerView as the BaseExpandableListAdapter class we're using
 * is for ListViews.
 */
class ArmorExpandableListAdapter(val armorGroups: List<ArmorGroup>) : BaseExpandableListAdapter() {

    override fun hasStableIds() = true
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroup(groupPosition: Int) = armorGroups[groupPosition].name
    override fun getGroupCount() = armorGroups.size

    override fun getChild(groupPosition: Int, childPosition: Int) = armorGroups[groupPosition].armor[childPosition]
    override fun getChildrenCount(groupPosition: Int) = armorGroups[groupPosition].armor.size

    override fun getChildId(groupPosition: Int, childPosition: Int) = groupPosition * 100L + childPosition
    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.fragment_armor_expandablelist_group_item, parent, false)
        view.name_text.text = getGroup(groupPosition)

        return view
    }


    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent?.context)
        val view = convertView ?: inflater.inflate(R.layout.fragment_armor_set_piece_listitem, parent, false)

        val armorWithSkill = getChild(groupPosition, childPosition)
        val armor = armorWithSkill.armor
        val skills = armorWithSkill.skills

        view.icon.setImageAsset(armor)
        view.name.text = armor.name
        view.slots.text = armor.slotString

        val skillsTvs = arrayOf(view.skill_1, view.skill_2, view.skill_3, view.skill_4)

        // init skill views to invisible (in case this gets moved to a recycling view
        for (subView in skillsTvs) {
            subView.visibility = View.GONE
        }

        for((i, skill) in skills.withIndex()) {
            skillsTvs[i]?.visibility = View.VISIBLE
            val points = skill.points
            val skillString = skill.skillTree?.name + if(points>0) "+$points" else points
            skillsTvs[i]?.text = skillString
        }

        return view
    }

}