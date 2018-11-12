package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.data.classes.WeaponBowCoatings
import com.ghstudios.android.data.classes.WeaponChargeLevel
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getIntOrNull
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

/**
 * A convenience class to wrap a cursor that returns rows from the "weapon"
 * table. The [] method will give you a Weapon instance
 * representing the current row.
 */
class WeaponCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Returns a Weapon object configured for the current row, or null if the
     * current row is invalid.
     */
    // note: not actually used for weapons, but here for completeness
    val weapon: Weapon
        get() {
            val weapon = Weapon()

            val wtype = getString(S.COLUMN_WEAPONS_WTYPE)

            val element = getString(S.COLUMN_WEAPONS_ELEMENT)
            val awaken = getString(S.COLUMN_WEAPONS_AWAKEN)
            val element_2 = getString(S.COLUMN_WEAPONS_ELEMENT_2)
            val element_2_attack = getLong(S.COLUMN_WEAPONS_ELEMENT_2_ATTACK)
            val element_attack = getLong(S.COLUMN_WEAPONS_ELEMENT_ATTACK)
            val awaken_attack = getLong(S.COLUMN_WEAPONS_AWAKEN_ATTACK)

            val charges = getString(S.COLUMN_WEAPONS_CHARGES)
            val coatings = getIntOrNull(S.COLUMN_WEAPONS_COATINGS) ?: 0
            val recoil = getString(S.COLUMN_WEAPONS_RECOIL)
            val reload_speed = getString(S.COLUMN_WEAPONS_RELOAD_SPEED)
            val rapid_fire = getString(S.COLUMN_WEAPONS_RAPID_FIRE)
            val deviation = getString(S.COLUMN_WEAPONS_DEVIATION)
            val ammo = getString(S.COLUMN_WEAPONS_AMMO)
            val special_ammo = getString(S.COLUMN_WEAPONS_SPECIAL_AMMO)

            weapon.wtype = wtype
            weapon.creationCost = getInt(S.COLUMN_WEAPONS_CREATION_COST)
            weapon.upgradeCost = getInt(S.COLUMN_WEAPONS_UPGRADE_COST)
            weapon.attack = getInt(S.COLUMN_WEAPONS_ATTACK)
            weapon.maxAttack = getInt(S.COLUMN_WEAPONS_MAX_ATTACK)
            weapon.element = element
            weapon.awaken = awaken
            weapon.element2 = element_2
            weapon.elementAttack = element_attack
            weapon.element2Attack = element_2_attack
            weapon.awakenAttack = awaken_attack
            weapon.defense = getInt(S.COLUMN_WEAPONS_DEFENSE)
            weapon.sharpness = getString(S.COLUMN_WEAPONS_SHARPNESS)
            weapon.affinity = getString(S.COLUMN_WEAPONS_AFFINITY)
            weapon.hornNotes = getString(S.COLUMN_WEAPONS_HORN_NOTES)
            weapon.shellingType = getString(S.COLUMN_WEAPONS_SHELLING_TYPE)
            weapon.phial = getString(S.COLUMN_WEAPONS_PHIAL)

            if (weapon.wtype == Weapon.BOW) {
                weapon.charges = parseBowCharges(charges ?: "")
                weapon.coatings = WeaponBowCoatings(coatings)
            }

            weapon.recoil = recoil
            weapon.reloadSpeed = reload_speed
            weapon.rapidFire = if (rapid_fire.isNullOrBlank()) null else rapid_fire
            weapon.specialAmmo = special_ammo
            weapon.deviation = deviation
            weapon.ammo = ammo
            weapon.numSlots = getInt(S.COLUMN_WEAPONS_NUM_SLOTS)
            weapon.wFinal = getInt(S.COLUMN_WEAPONS_FINAL)
            weapon.tree_Depth = getInt(S.COLUMN_WEAPONS_TREE_DEPTH)
            weapon.parentId = getInt(S.COLUMN_WEAPONS_PARENT_ID)

            weapon.id = getLong(S.COLUMN_ITEMS_ID)
            weapon.name = getString(S.COLUMN_ITEMS_NAME)
            weapon.jpnName = getString(S.COLUMN_ITEMS_JPN_NAME)
            weapon.type = ItemType.WEAPON
            weapon.subType = getString(S.COLUMN_ITEMS_SUB_TYPE)
            weapon.rarity = getInt(S.COLUMN_ITEMS_RARITY)
            weapon.carryCapacity = getInt(S.COLUMN_ITEMS_CARRY_CAPACITY)
            weapon.buy = getInt(S.COLUMN_ITEMS_BUY)
            weapon.sell = getInt(S.COLUMN_ITEMS_SELL)
            weapon.description = getString(S.COLUMN_ITEMS_DESCRIPTION)
            weapon.fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME)

            if (weapon.wtype != Weapon.BOW && weapon.wtype != Weapon.LIGHT_BOWGUN
                    && weapon.wtype != Weapon.HEAVY_BOWGUN) {
                weapon.initializeSharpness()
            }

            return weapon
        }

    fun parseBowCharges(chargeString: String): List<WeaponChargeLevel> {
        return chargeString.split('|').map {
            val locked = it.endsWith("*")

            val levelStr = when {
                locked -> it.substring(0, it.length - 1)
                else -> it
            }

            val parts = levelStr.split(' ')
            val name = parts[0]
            val level = parts.getOrElse(1) { _ -> "" }.toIntOrNull() ?: 0

            WeaponChargeLevel(name=name, level=level, locked=locked)
        }
    }
}