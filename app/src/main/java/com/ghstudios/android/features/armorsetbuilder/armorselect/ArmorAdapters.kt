package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseExpandableListAdapter
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ArmorSkillPoints
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.setImageAsset
import kotlinx.android.synthetic.main.listitem_armor_header.view.*
import kotlinx.android.synthetic.main.listitem_armor_piece.view.*

/**
 * A list of armor grouped by rarity.
 */
class ArmorGroup(
        val rarity: Int,
        val armor: List<ArmorSkillPoints>
)

/**
 * Internal helper to bind armor skill points to a view.
 */
private fun bindArmorView(view: View,  armorWithSkill: ArmorSkillPoints) {
    val armor = armorWithSkill.armor
    val skills = armorWithSkill.skills

    view.icon.setImageAsset(armor)
    view.name.text = armor.name
    view.slots.setSlots(armor.numSlots, 0)

    val skillsTvs = arrayOf(view.skill_1, view.skill_2, view.skill_3, view.skill_4)

    // init skill views to invisible (in case this gets moved to a recycling view
    for (subView in skillsTvs) {
        subView.visibility = View.GONE
    }

    for((i, skill) in skills.withIndex()) {
        skillsTvs[i]?.visibility = View.VISIBLE
        val points = skill.points
        val skillString = skill.skillTree.name + if(points>0) "+$points" else points
        skillsTvs[i]?.text = skillString
    }
}

/**
 * Creates an adapter to display a collapsible list of armor.
 * Uses ListView instead of RecyclerView as the BaseExpandableListAdapter class we're using
 * is for ListViews.
 */
class ArmorExpandableListAdapter(val armorGroups: List<ArmorGroup>) : BaseExpandableListAdapter() {
    /**
     * Sets the on armor selection callback
     */
    var onArmorSelected: ((Armor) -> Unit)? = null

    override fun hasStableIds() = true
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroup(groupPosition: Int) = armorGroups[groupPosition]
    override fun getGroupCount() = armorGroups.size

    override fun getChild(groupPosition: Int, childPosition: Int) = armorGroups[groupPosition].armor[childPosition]
    override fun getChildrenCount(groupPosition: Int) = armorGroups[groupPosition].armor.size

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


    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent?.context)
        val view = convertView ?: inflater.inflate(R.layout.listitem_armor_piece, parent, false)
        val armorWithSkill = getChild(groupPosition, childPosition)

        bindArmorView(view, armorWithSkill)
        view.setOnClickListener {
            onArmorSelected?.invoke(armorWithSkill.armor)
        }

        return view
    }
}

/**
 * Adapter used to display the armor pieces in a list view
 */
class ArmorListAdapter(
        ctx: Context,
        val armor: List<ArmorSkillPoints>
): ArrayAdapter<ArmorSkillPoints>(ctx, R.layout.listitem_armor_header, armor) {
    /**
     * Sets the on armor selection callback
     */
    var onArmorSelected: ((Armor) -> Unit)? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        val view = convertView ?: inflater.inflate(R.layout.listitem_armor_piece, parent, false)
        val armorWithSkill = checkNotNull(getItem(position))
        bindArmorView(view, armorWithSkill)

        view.setOnClickListener {
            onArmorSelected?.invoke(armorWithSkill.armor)
        }

        return view
    }
}
