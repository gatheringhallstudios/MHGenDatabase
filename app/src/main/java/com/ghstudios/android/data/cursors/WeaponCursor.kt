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
            val creation_cost = getInt(S.COLUMN_WEAPONS_CREATION_COST)
            val upgrade_cost = getInt(S.COLUMN_WEAPONS_UPGRADE_COST)
            val attack = getInt(S.COLUMN_WEAPONS_ATTACK)
            val max_attack = getInt(S.COLUMN_WEAPONS_MAX_ATTACK)
            val element = getString(S.COLUMN_WEAPONS_ELEMENT)
            val awaken = getString(S.COLUMN_WEAPONS_AWAKEN)
            val element_2 = getString(S.COLUMN_WEAPONS_ELEMENT_2)
            val element_2_attack = getLong(S.COLUMN_WEAPONS_ELEMENT_2_ATTACK)
            val element_attack = getLong(S.COLUMN_WEAPONS_ELEMENT_ATTACK)
            val awaken_attack = getLong(S.COLUMN_WEAPONS_AWAKEN_ATTACK)
            val defense = getInt(S.COLUMN_WEAPONS_DEFENSE)
            val sharpness = getString(S.COLUMN_WEAPONS_SHARPNESS)
            val affinity = getString(S.COLUMN_WEAPONS_AFFINITY)
            val horn_notes = getString(S.COLUMN_WEAPONS_HORN_NOTES)
            val shelling_type = getString(S.COLUMN_WEAPONS_SHELLING_TYPE)
            val phial = getString(S.COLUMN_WEAPONS_PHIAL)
            val charges = getString(S.COLUMN_WEAPONS_CHARGES)
            val coatings = getIntOrNull(S.COLUMN_WEAPONS_COATINGS) ?: 0
            val recoil = getString(S.COLUMN_WEAPONS_RECOIL)
            val reload_speed = getString(S.COLUMN_WEAPONS_RELOAD_SPEED)
            val rapid_fire = getString(S.COLUMN_WEAPONS_RAPID_FIRE)
            val deviation = getString(S.COLUMN_WEAPONS_DEVIATION)
            val ammo = getString(S.COLUMN_WEAPONS_AMMO)
            val special_ammo = getString(S.COLUMN_WEAPONS_SPECIAL_AMMO)
            val num_slots = getInt(S.COLUMN_WEAPONS_NUM_SLOTS)
            val wfinal = getInt(S.COLUMN_WEAPONS_FINAL)
            val tree_depth = getInt(S.COLUMN_WEAPONS_TREE_DEPTH)
            val parent_id = getInt(S.COLUMN_WEAPONS_PARENT_ID)

            weapon.wtype = wtype
            weapon.creationCost = creation_cost
            weapon.upgradeCost = upgrade_cost
            weapon.attack = attack
            weapon.maxAttack = max_attack
            weapon.element = element
            weapon.awaken = awaken
            weapon.element2 = element_2
            weapon.elementAttack = element_attack
            weapon.element2Attack = element_2_attack
            weapon.awakenAttack = awaken_attack
            weapon.defense = defense
            weapon.sharpness = sharpness
            weapon.affinity = affinity
            weapon.hornNotes = horn_notes
            weapon.shellingType = shelling_type
            weapon.phial = phial
            if (weapon.wtype == Weapon.BOW) {
                weapon.charges = parseBowCharges(charges ?: "")
                weapon.coatings = WeaponBowCoatings(coatings)
            }
            weapon.recoil = recoil
            weapon.reloadSpeed = reload_speed
            weapon.rapidFire = rapid_fire
            weapon.specialAmmo = special_ammo
            weapon.deviation = deviation
            weapon.ammo = ammo
            weapon.numSlots = num_slots
            weapon.wFinal = wfinal
            weapon.tree_Depth = tree_depth

            val itemId = getLong(S.COLUMN_ITEMS_ID)
            val name = getString(S.COLUMN_ITEMS_NAME)
            val jpnName = getString(S.COLUMN_ITEMS_JPN_NAME)
            val subType = getString(S.COLUMN_ITEMS_SUB_TYPE)
            val rarity = getInt(S.COLUMN_ITEMS_RARITY)
            val carry_capacity = getInt(S.COLUMN_ITEMS_CARRY_CAPACITY)
            val buy = getInt(S.COLUMN_ITEMS_BUY)
            val sell = getInt(S.COLUMN_ITEMS_SELL)
            val description = getString(S.COLUMN_ITEMS_DESCRIPTION)
            val fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME)

            weapon.id = itemId
            weapon.name = name
            weapon.jpnName = jpnName
            weapon.type = ItemType.WEAPON
            weapon.subType = subType
            weapon.rarity = rarity
            weapon.carryCapacity = carry_capacity
            weapon.buy = buy
            weapon.sell = sell
            weapon.description = description
            weapon.parentId = parent_id
            weapon.fileLocation = fileLocation

            if (weapon.wtype != Weapon.BOW && weapon.wtype != Weapon.LIGHT_BOWGUN
                    && weapon.wtype != Weapon.HEAVY_BOWGUN) {
                weapon.initializeSharpness()
            }

            return weapon
        }

    fun parseBowCharges(chargeString: String): List<WeaponChargeLevel> {
        return chargeString.split('|').map {
            var levelStr = it
            val locked = chargeString.endsWith("*")
            if (locked) {
                levelStr = levelStr.substring(0, levelStr.length - 1)
            }

            val parts = levelStr.split(' ')
            val name = parts[0]
            val level = parts.getOrElse(1) { _ -> "" }.toIntOrNull() ?: 0

            WeaponChargeLevel(name=name, level=level, locked=locked)
        }
    }
}