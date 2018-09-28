package com.ghstudios.android.features.armor.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.data.classes.ArmorFamily
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.setImageAsset
import kotlinx.android.synthetic.main.listitem_armor_family.view.*
import kotlinx.android.synthetic.main.listitem_armor_header.view.*

class ArmorFamilyGroup(
        val rarity: Int,
        val families: List<ArmorFamily>
)

class ArmorFamilyListAdapter(private val groups: List<ArmorFamilyGroup>) : BaseExpandableListAdapter() {
    override fun hasStableIds() = true
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroup(groupPosition: Int) = groups[groupPosition]
    override fun getGroupCount() = groups.size

    override fun getChild(groupPosition: Int, childPosition: Int) = groups[groupPosition].families[childPosition]
    override fun getChildrenCount(groupPosition: Int) = groups[groupPosition].families.size

    override fun getChildId(groupPosition: Int, childPosition: Int) = groupPosition * 100L + childPosition
    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(i: Int, b: Boolean, view: View,
                              viewGroup: ViewGroup): View {
        var v: View? = view
        val context = viewGroup.context

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.listitem_armor_header, viewGroup, false)
        }

        val armorGroupTextView = v!!.findViewById<TextView>(R.id.name_text)

        armorGroupTextView.text = getGroup(i).toString()

        return v
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
        view.rank_text.text = when (family.rarity) {
            in 0..3 -> view.resources.getString(R.string.armor_list_header_sub_lr)
            in 3..7 -> view.resources.getString(R.string.armor_list_header_sub_hr)
            in 7..10 -> view.resources.getString(R.string.armor_list_header_sub_g)
            11 -> view.resources.getString(R.string.armor_list_header_sub_deviant)
            else -> ""
        }

        val minDef = view.findViewById<TextView>(R.id.min_defense)
        val maxDef = view.findViewById<TextView>(R.id.max_defense)
        minDef.text = Integer.toString(family.minDef)
        maxDef.text = Integer.toString(family.maxDef)

        //Set Skills
        val skills = arrayOf(view.findViewById(R.id.skill_1), view.findViewById(R.id.skill_2), view.findViewById(R.id.skill_3), view.findViewById(R.id.skill_4), view.findViewById<TextView>(R.id.skill_5))

        for (i in skills.indices) {
            if (i < family.skills.size) {
                skills[i].visibility = View.VISIBLE
                skills[i].text = family.skills[i]
            } else {
                skills[i].visibility = View.GONE
            }
        }

        view.tag = family.id
        view.setOnClickListener(ArmorClickListener(context, family.id, true))

        return view
    }
}