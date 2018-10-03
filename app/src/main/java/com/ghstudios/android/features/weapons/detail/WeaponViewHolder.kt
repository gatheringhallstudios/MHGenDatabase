package com.ghstudios.android.features.weapons.detail

import com.ghstudios.android.data.classes.Weapon

/**
 * Base class for weapon detail data inflation and binding.
 */
interface WeaponViewHolder {
    fun bindWeapon(weapon: Weapon)
}