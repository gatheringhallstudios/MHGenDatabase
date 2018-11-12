package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemTypeConverter
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

/**
 * A convenience class to wrap a cursor that returns rows from the "armor"
 * table. The [] method will give you a Armor instance
 * representing the current row.
 */
class ArmorCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Returns a Armor object configured for the current row
     */
    val armor: Armor
        get() {
            return Armor().apply {
                id = getLong(S.COLUMN_ITEMS_ID)
                name = getString(S.COLUMN_ITEMS_NAME)
                jpnName = getString(S.COLUMN_ITEMS_JPN_NAME)

                slot = getString(S.COLUMN_ARMOR_SLOT)
                defense = getInt(S.COLUMN_ARMOR_DEFENSE)
                maxDefense = getInt(S.COLUMN_ARMOR_MAX_DEFENSE)
                fireRes = getInt(S.COLUMN_ARMOR_FIRE_RES)
                thunderRes = getInt(S.COLUMN_ARMOR_THUNDER_RES)
                dragonRes = getInt(S.COLUMN_ARMOR_DRAGON_RES)
                waterRes = getInt(S.COLUMN_ARMOR_WATER_RES)
                iceRes = getInt(S.COLUMN_ARMOR_ICE_RES)
                gender = getInt(S.COLUMN_ARMOR_GENDER)
                hunterType = getInt(S.COLUMN_ARMOR_HUNTER_TYPE)
                family = getLong("family")
                numSlots = getInt(S.COLUMN_ARMOR_NUM_SLOTS)

                type = ItemTypeConverter.deserialize(getString(S.COLUMN_ITEMS_TYPE) ?: "")
                subType = getString(S.COLUMN_ITEMS_SUB_TYPE)
                rarity = getInt(S.COLUMN_ITEMS_RARITY)
                carryCapacity = getInt(S.COLUMN_ITEMS_CARRY_CAPACITY)
                buy = getInt(S.COLUMN_ITEMS_BUY)
                sell = getInt(S.COLUMN_ITEMS_SELL)
                description = getString(S.COLUMN_ITEMS_DESCRIPTION)
                fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME)
                iconColor = getInt(S.COLUMN_ITEMS_ICON_COLOR)
            }
        }
}