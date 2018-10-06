package com.ghstudios.android.features.weapons.detail

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ElementStatus
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.util.getColorCompat
import kotlinx.android.synthetic.main.view_weapon_detail_blade.view.*

/**
 * Handles binding to the view for blademaster weapons
 * Used by the WeaponDetailFragment
 */
class WeaponBladeDetailViewHolder(parent: ViewGroup): WeaponDetailViewHolder {
    private val view: View
    init {
        val inflater = LayoutInflater.from(parent.context)
        view = inflater.inflate(R.layout.view_weapon_detail_blade, parent, true)
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
        if (weapon.element2Enum != ElementStatus.NONE) {
            view.element2_icon.setImageDrawable(AssetLoader.loadIconFor(weapon.element2Enum))
            view.element2_value.text = weapon.element2Attack.toString()
            view.element2_group.visibility = View.VISIBLE
        }

        // Sharpness
        view.sharpness.init(weapon.sharpness1, weapon.sharpness2, weapon.sharpness3)
        view.sharpness.invalidate() // todo: sharpness init should auto invalidate....

        // Blade weapon specific binding
        when (weapon.wtype) {
            Weapon.GUNLANCE -> {
                view.blade_extra_label.text = context.getString(R.string.weapon_shelling)
                view.blade_extra.text = AssetLoader.localizeWeaponShelling(weapon.shellingType)
            }
            Weapon.SWITCH_AXE, Weapon.CHARGE_BLADE -> {
                view.blade_extra_label.text = context.getString(R.string.weapon_phial_type)
                view.blade_extra.text = AssetLoader.localizeWeaponPhialType(weapon.phial)
            }
            Weapon.HUNTING_HORN -> {
                view.weapon_note_container.visibility = View.VISIBLE

                val notes = weapon.hornNotes ?: "WWW"
                view.weapon_note1.setColorFilter(context.getColorCompat(MHUtils.getNoteColor(notes[0])), PorterDuff.Mode.MULTIPLY)
                view.weapon_note2.setColorFilter(context.getColorCompat(MHUtils.getNoteColor(notes[1])), PorterDuff.Mode.MULTIPLY)
                view.weapon_note3.setColorFilter(context.getColorCompat(MHUtils.getNoteColor(notes[2])), PorterDuff.Mode.MULTIPLY)
            }
        }
    }
}