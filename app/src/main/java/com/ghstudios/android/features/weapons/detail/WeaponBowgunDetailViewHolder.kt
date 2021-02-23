package com.ghstudios.android.features.weapons.detail

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.ViewWeaponDetailBowgunBinding
import com.ghstudios.android.util.getColorCompat

private fun getWaitString(context: Context, wait: Int) = when (wait) {
    0 -> context.getString(R.string.rapid_fire_short_wait)
    1 -> context.getString(R.string.rapid_fire_medium_wait)
    2 -> context.getString(R.string.rapid_fire_long_wait)
    3 -> context.getString(R.string.rapid_fire_very_long_wait)
    else -> ""
}

class WeaponBowgunDetailViewHolder(parent: ViewGroup): WeaponDetailViewHolder {
    private val binding: ViewWeaponDetailBowgunBinding
    private val ammoCells: List<TextView>
    private val internalAmmoCells: List<TextView>
    private val extraAmmoCells: List<TextView>

    val context get() = binding.root.context

    init {
        val inflater = LayoutInflater.from(parent.context)
        binding = ViewWeaponDetailBowgunBinding.inflate(inflater, parent, true)

        ammoCells = with(binding) {
            listOf(
                normal1,
                normal2,
                normal3,
                pierce1,
                pierce2,
                pierce3,
                pellet1,
                pellet2,
                pellet3,
                crag1,
                crag2,
                crag3,
                clust1,
                clust2,
                clust3,
                flaming,
                water,
                thunder,
                freeze,
                dragon,
                poison1,
                poison2,
                para1,
                para2,
                sleep1,
                sleep2,
                exhaust1,
                exhaust2,
                recov1,
                recov2
            )
        }

        internalAmmoCells = with(binding) {
            listOf(
                internalAmmo1,
                internalAmmo2,
                internalAmmo3,
                internalAmmo4,
                internalAmmo5
            )
        }
        extraAmmoCells = with(binding) {
            listOf(
                rapidAmmo1,
                rapidAmmo2,
                rapidAmmo3,
                rapidAmmo4,
                rapidAmmo5
            )
        }
    }

    override fun bindWeapon(weapon: Weapon) {
        with(binding) {
            // Usual weapon parameters
            attackValue.text = weapon.attack.toString()
            affinityValue.text = weapon.affinity + "%"
            defenseValue.text = weapon.defense.toString()
            slots.setSlots(weapon.numSlots, 0)

            // Bowgun basic data
            reloadValue.text = weapon.reloadSpeed
            recoilValue.text = weapon.recoil
            deviationValue.text = weapon.deviation
        }
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
        binding.weaponExtraTitle.setText(R.string.rapid_fire)

        val rapid = weapon.rapidFire?.split("\\*".toRegex()) ?: emptyList()

        if (rapid.isEmpty()) {
            showEmptyExtra()
            return
        }

        for ((ammoView, rapidValue) in extraAmmoCells.zip(rapid)) {
            val s = rapidValue.split(":")
            val name = s[0]
            val count = s[1]
            val rfModifier = s[2] // % damage for extra shots
            val waitString = getWaitString(context, s[3].toInt())

            ammoView.text = context.getString(R.string.weapon_rapid_row,
                    name, count, rfModifier, waitString)
            ammoView.visibility = View.VISIBLE
        }
    }

    private fun bindSiegeFire(weapon: Weapon) {
        binding.weaponExtraTitle.setText(R.string.siege_mode)

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
