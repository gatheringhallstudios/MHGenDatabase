package com.ghstudios.android.features.weapons.detail

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ElementStatus

import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.getColorCompat
import kotlinx.android.synthetic.main.view_weapon_detail_bow.view.*

/**
 * Inflates bow data into a view and binds the bow data.
 * Use in the WeaponDetailFragment or any sort of fragment to show full bow data.
 */
class WeaponBowDetailViewHolder(parent: ViewGroup) : WeaponDetailViewHolder {
    private val view: View
    private val chargeCells: List<TextView>
    private val coatingCells: List<TextView>

    init {
        val inflater = LayoutInflater.from(parent.context)
        view = inflater.inflate(R.layout.view_weapon_detail_bow, parent, true)

        chargeCells = listOf(
                view.weapon_bow_charge1,
                view.weapon_bow_charge2,
                view.weapon_bow_charge3,
                view.weapon_bow_charge4
        )
        
        coatingCells = listOf(
                view.power_1_text,
                view.power_2_text,
                view.element_1_text,
                view.element_2_text,
                view.crange_text,
                view.poison_text,
                view.para_text,
                view.sleep_text,
                view.exhaust_text,
                view.blast_text,
                view.paint_text
        )
    }

    override fun bindWeapon(weapon: Weapon) {
        val context = view.context

        // Usual weapon parameters
        view.attack_value.text = weapon.attack.toString()
        view.affinity_value.text = weapon.affinity + "%"
        view.defense_value.text = weapon.defense.toString()
        view.slots.setSlots(weapon.numSlots, 0)

        // bind weapon element (todo: if awaken element ever returns...make an isAwakened flag instead)
        if (weapon.elementEnum != ElementStatus.NONE) {
            view.element1_icon.setImageDrawable(AssetLoader.loadIconFor(weapon.elementEnum))
            view.element1_value.text = weapon.elementAttack.toString()
            view.element1_group.visibility = View.VISIBLE
        }

        view.weapon_bow_arc.text = weapon.recoil

        // Charge levels
        for ((view, level) in chargeCells.zip(weapon.charges)) {
            view.visibility = View.VISIBLE
            view.text = AssetLoader.localizeChargeLevel(level)

            if (level.locked) {
                val lockColor = context.getColorCompat(R.color.text_color_secondary)
                view.setTextColor(lockColor)
            }
        }

        // Coatings (note: this is carry over logic from gen. I have no idea what it does...)
        val coatings = Integer.parseInt(weapon.coatings)
        for (i in 10 downTo 0) {
            val show = coatings and (1 shl i) > 0
            if (show) {
                val color = ContextCompat.getColor(context, R.color.text_color_focused)
                coatingCells[10 - i].setTextColor(color)
                coatingCells[10 - i].setTypeface(null, Typeface.BOLD)
            }
        }
    }
}
