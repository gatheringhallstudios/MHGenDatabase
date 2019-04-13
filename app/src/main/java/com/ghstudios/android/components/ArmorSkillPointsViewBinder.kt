package com.ghstudios.android.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ArmorSkillPoints
import com.ghstudios.android.mhgendatabase.R

/**
 * Class used to create views that bind to ArmorSkillPoints objects.
 * Can be used internally in a recyclerview adapter or standalone.
 */
class ArmorSkillPointsViewBinder {
    fun createView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(R.layout.listitem_armor_piece, parent,false)
    }

    fun bindView(view: View, data: ArmorSkillPoints) {
        val armor = data.armor
        val skills = data.skills

        val icon: ImageView? = view.findViewById(R.id.icon)
        val name: TextView? = view.findViewById(R.id.name)
        val slots: SlotsView? = view.findViewById(R.id.slots)

        AssetLoader.setIcon(icon!!,armor)
        name?.text = armor.name
        slots?.setSlots(armor.numSlots, 0)

        val skillsTvs : Array<TextView?> = arrayOf(view.findViewById(R.id.skill_1),
                view.findViewById(R.id.skill_2),
                view.findViewById(R.id.skill_3),
                view.findViewById(R.id.skill_4))
        skillsTvs.forEach { it?.visibility= View.GONE }

        for((i, skill) in skills.withIndex()) {
            skillsTvs[i]?.visibility = View.VISIBLE
            val points = skill.points
            val skillString = skill.skillTree.name + if(points>0) "+$points" else points
            skillsTvs[i]?.text = skillString
        }
    }
}