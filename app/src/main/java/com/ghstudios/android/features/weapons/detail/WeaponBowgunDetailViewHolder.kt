package com.ghstudios.android.features.weapons.detail

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.getColorCompat
import kotlinx.android.synthetic.main.view_weapon_detail_bowgun.view.*

private fun getWaitString(context: Context, wait: Int) = when (wait) {
    0 -> context.getString(R.string.rapid_fire_short_wait)
    1 -> context.getString(R.string.rapid_fire_medium_wait)
    2 -> context.getString(R.string.rapid_fire_long_wait)
    3 -> context.getString(R.string.rapid_fire_very_long_wait)
    else -> ""
}

class WeaponBowgunDetailViewHolder(parent: ViewGroup): WeaponDetailViewHolder {
    private val view: View
    private val ammoCells: List<TextView>
    private val internalAmmoCells: List<TextView>
    private val extraAmmoCells: List<TextView>

    val context get() = view.context

    init {
        val inflater = LayoutInflater.from(parent.context)
        view = inflater.inflate(R.layout.view_weapon_detail_bowgun, parent, true)

        ammoCells = listOf(
            view.findViewById(R.id.normal1),
            view.findViewById(R.id.normal2),
            view.findViewById(R.id.normal3),
            view.findViewById(R.id.pierce1),
            view.findViewById(R.id.pierce2),
            view.findViewById(R.id.pierce3),
            view.findViewById(R.id.pellet1),
            view.findViewById(R.id.pellet2),
            view.findViewById(R.id.pellet3),
            view.findViewById(R.id.crag1),
            view.findViewById(R.id.crag2),
            view.findViewById(R.id.crag3),
            view.findViewById(R.id.clust1),
            view.findViewById(R.id.clust2),
            view.findViewById(R.id.clust3),
            view.findViewById(R.id.flaming),
            view.findViewById(R.id.water),
            view.findViewById(R.id.thunder),
            view.findViewById(R.id.freeze),
            view.findViewById(R.id.dragon),
            view.findViewById(R.id.poison1),
            view.findViewById(R.id.poison2),
            view.findViewById(R.id.para1),
            view.findViewById(R.id.para2),
            view.findViewById(R.id.sleep1),
            view.findViewById(R.id.sleep2),
            view.findViewById(R.id.exhaust1),
            view.findViewById(R.id.exhaust2)
        )

        internalAmmoCells = listOf(
                view.findViewById(R.id.internal_ammo_1),
                view.findViewById(R.id.internal_ammo_2),
                view.findViewById(R.id.internal_ammo_3),
                view.findViewById(R.id.internal_ammo_4),
                view.findViewById(R.id.internal_ammo_5)
        )

        extraAmmoCells = listOf(
                view.findViewById(R.id.rapid_ammo_1),
                view.findViewById(R.id.rapid_ammo_2),
                view.findViewById(R.id.rapid_ammo_3),
                view.findViewById(R.id.rapid_ammo_4),
                view.findViewById(R.id.rapid_ammo_5)
        )
    }

    override fun bindWeapon(weapon: Weapon) {
        // Usual weapon parameters
        view.attack_value.text = weapon.attack.toString()
        view.affinity_value.text = weapon.affinity + "%"
        view.defense_value.text = weapon.defense.toString()
        view.slots.setSlots(weapon.numSlots, 0)

        // Bowgun basic data
        view.weapon_bowgun_reload_value.text = weapon.reloadSpeed
        view.weapon_bowgun_recoil_value.text = weapon.recoil
        view.weapon_bowgun_steadiness_value.text = weapon.deviation

        // weapon ammo (todo: move this parsing to the weapon model)
        val ammos = weapon.ammo?.split("\\|".toRegex()) ?: emptyList()
        for ((ammoView, valueStr) in ammoCells.zip(ammos)) {
            val innate = valueStr[valueStr.lastIndex] == '*'
            val value = when (innate) {
                true -> valueStr.substring(0, valueStr.length - 1)
                false -> valueStr
            }

            ammoView.text = value
            if (innate) {
                ammoView.setTypeface(null, Typeface.BOLD)
                ammoView.setTextColor(context.getColorCompat(R.color.text_color_focused))
            }
        }

        // Bind gun internal and rapid/siege
        bindInternalAmmo(weapon)
        if (weapon.wtype == Weapon.LIGHT_BOWGUN) {
            bindRapidFire(weapon)
        } else {
            bindSiegeFire(weapon)
        }
    }

    private fun bindInternalAmmo(weapon: Weapon) {
        // todo: move parsing to model...
        val internal = weapon.specialAmmo?.split("\\*".toRegex()) ?: emptyList()

        if (internal.isEmpty()) {
            internalAmmoCells[0].setText(R.string.ammo_none)
            internalAmmoCells[0].visibility = View.VISIBLE
            return
        }
        for ((ammoView, internalValue) in internalAmmoCells.zip(internal)) {
            val s = internalValue.split(":")
            ammoView.text = context.getString(R.string.weapon_internal_row, s[0], s[1], s[2])
            ammoView.visibility = View.VISIBLE
        }
    }

    private fun bindRapidFire(weapon: Weapon) {
        view.weapon_extra_title.setText(R.string.rapid_fire)

        val rapid = weapon.rapidFire?.split("\\*".toRegex()) ?: emptyList()

        if (rapid.isEmpty()) {
            showEmptyExtra()
            return
        }

        for ((ammoView, rapidValue) in extraAmmoCells.zip(rapid)) {
            val s = rapidValue.split(":")
            val waitString = getWaitString(context, s[3].toInt())
            ammoView.text = context.getString(R.string.weapon_rapid_row, s[0], s[1], s[2], waitString)
            ammoView.visibility = View.VISIBLE
        }
    }

    private fun bindSiegeFire(weapon: Weapon) {
        view.weapon_extra_title.setText(R.string.siege_mode)

        val siege = weapon.rapidFire?.split("\\*".toRegex()) ?: emptyList()

        if (siege.isEmpty()) {
            showEmptyExtra()
            return
        }

        for ((ammoView, siegeValue) in extraAmmoCells.zip(siege)) {
            val s = siegeValue.split(":")
            ammoView.text = context.getString(R.string.weapon_siege_row, s[0], s[1])
            ammoView.visibility = View.VISIBLE
        }
    }

    /**
     * Helper used to show none in the extra ammo section (siege/rapid)
     */
    private fun showEmptyExtra() {
        extraAmmoCells[0].let {
            it.visibility = View.VISIBLE
            it.setText(R.string.ammo_none)
        }
    }
}