package com.ghstudios.android.features.weapons.detail

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ElementStatus

import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.ViewWeaponDetailBowBinding
import com.ghstudios.android.util.getColorCompat

/**
 * Inflates bow data into a view and binds the bow data.
 * Use in the WeaponDetailFragment or any sort of fragment to show full bow data.
 */
class WeaponBowDetailViewHolder(parent: ViewGroup) : WeaponDetailViewHolder {
    private val binding: ViewWeaponDetailBowBinding
    private val chargeCells: List<TextView>

    init {
        val inflater = LayoutInflater.from(parent.context)
        binding = ViewWeaponDetailBowBinding.inflate(inflater, parent, true)

        chargeCells = with(binding) {
            listOf(
                weaponBowCharge1,
                weaponBowCharge2,
                weaponBowCharge3,
                weaponBowCharge4
            )
        }
    }

    override fun bindWeapon(weapon: Weapon) {
        val context = binding.root.context

        with(binding) {
            // Usual weapon parameters
            attackValue.text = weapon.attack.toString()
            affinityValue.text = weapon.affinity + "%"
            defenseValue.text = weapon.defense.toString()
            slots.setSlots(weapon.numSlots, 0)

            // bind weapon element (todo: if awaken element ever returns...make an isAwakened flag instead)
            if (weapon.elementEnum != ElementStatus.NONE) {
                element1Icon.setImageDrawable(AssetLoader.loadIconFor(weapon.elementEnum))
                element1Value.text = weapon.elementAttack.toString()
                element1Group.visibility = View.VISIBLE
            }

            weaponBowArc.text = weapon.recoil

            // Charge levels
            for ((view, level) in chargeCells.zip(weapon.charges)) {
                view.visibility = View.VISIBLE
                view.text = AssetLoader.localizeChargeLevel(level)

                if (level.locked) {
                    val lockColor = context.getColorCompat(R.color.text_color_secondary)
                    view.setTextColor(lockColor)
                }
            }

            // Internal function to "enable" a weapon coating view
            fun setCoating(enabled: Boolean, view: TextView) {
                if (enabled) {
                    val color = context.getColorCompat(R.color.text_color_focused)
                    view.setTextColor(color)
                    view.setTypeface(null, Typeface.BOLD)
                }
            }

            weapon.coatings?.let { coatings ->
                setCoating(coatings.power1, power1Text)
                setCoating(coatings.power2, power2Text)
                setCoating(coatings.elem1, element1Text)
                setCoating(coatings.elem2, element2Text)
                setCoating(coatings.crange, crangeText)
                setCoating(coatings.poison, poisonText)
                setCoating(coatings.para, paraText)
                setCoating(coatings.sleep, sleepText)
                setCoating(coatings.exhaust, exhaustText)
                setCoating(coatings.blast, blastText)

                setCoating(coatings.hasPower, powerLabel)
                setCoating(coatings.hasElem, elementLabel)
            }
        }
    }
}
