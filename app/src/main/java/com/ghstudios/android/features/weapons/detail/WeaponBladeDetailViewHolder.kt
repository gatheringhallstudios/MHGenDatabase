package com.ghstudios.android.features.weapons.detail

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ElementStatus
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.ViewWeaponDetailBladeBinding
import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.util.getColorCompat

/**
 * Handles binding to the view for blademaster weapons
 * Used by the WeaponDetailFragment
 */
class WeaponBladeDetailViewHolder(parent: ViewGroup): WeaponDetailViewHolder {
    private val binding: ViewWeaponDetailBladeBinding
    init {
        val inflater = LayoutInflater.from(parent.context)
        binding = ViewWeaponDetailBladeBinding.inflate(inflater, parent, true)
    }

    override fun bindWeapon(weapon: Weapon) {
        val context = binding.root.context

        // Usual weapon parameters
        with(binding) {
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
            if (weapon.element2Enum != ElementStatus.NONE) {
                element2Icon.setImageDrawable(AssetLoader.loadIconFor(weapon.element2Enum))
                element2Value.text = weapon.element2Attack.toString()
                element2Group.visibility = View.VISIBLE
            }

            // Sharpness
            sharpness.init(weapon.sharpness1, weapon.sharpness2, weapon.sharpness3)
            sharpness.invalidate() // todo: sharpness init should auto invalidate....

            // Blade weapon specific binding
            when (weapon.wtype) {
                Weapon.GUNLANCE -> {
                    bladeExtraLabel.text = context.getString(R.string.weapon_shelling)
                    bladeExtra.text = AssetLoader.localizeWeaponShelling(weapon.shellingType)
                }
                Weapon.SWITCH_AXE, Weapon.CHARGE_BLADE -> {
                    bladeExtraLabel.text = context.getString(R.string.weapon_phial_type)
                    bladeExtra.text = AssetLoader.localizeWeaponPhialType(weapon.phial)
                }
                Weapon.HUNTING_HORN -> {
                    weaponNoteContainer.visibility = View.VISIBLE

                    val notes = weapon.hornNotes ?: "WWW"
                    weaponNote1.setColorFilter(context.getColorCompat(MHUtils.getNoteColor(notes[0])), PorterDuff.Mode.MULTIPLY)
                    weaponNote2.setColorFilter(context.getColorCompat(MHUtils.getNoteColor(notes[1])), PorterDuff.Mode.MULTIPLY)
                    weaponNote3.setColorFilter(context.getColorCompat(MHUtils.getNoteColor(notes[2])), PorterDuff.Mode.MULTIPLY)
                }
            }
        }
    }
}
