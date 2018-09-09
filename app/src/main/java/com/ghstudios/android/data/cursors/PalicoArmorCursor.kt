package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.PalicoArmor
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

class PalicoArmorCursor(c: Cursor) : CursorWrapper(c) {
    val armor: PalicoArmor
    get(){
        return PalicoArmor().apply {
            id = getLong(S.COLUMN_PALICO_ARMOR_ID)
            defense = getInt(S.COLUMN_PALICO_ARMOR_DEFENSE)
            fireRes = getInt(S.COLUMN_PALICO_ARMOR_FIRE_RES)
            thunderRes = getInt(S.COLUMN_PALICO_ARMOR_THUNDER_RES)
            dragonRes = getInt(S.COLUMN_PALICO_ARMOR_DRAGON_RES)
            iceRes = getInt(S.COLUMN_PALICO_ARMOR_ICE_RES)
            waterRes = getInt(S.COLUMN_PALICO_ARMOR_WATER_RES)
            item = Item().apply {
                id = getLong(S.COLUMN_ITEMS_ID)
                name = getString(S.COLUMN_ITEMS_NAME)
                description = getString(S.COLUMN_ITEMS_DESCRIPTION)
                rarity = getInt(S.COLUMN_ITEMS_RARITY)
                fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME)
                iconColor = getInt(S.COLUMN_ITEMS_ICON_NAME)
                type = ItemType.PALICO_ARMOR
            }
        }
    }
}