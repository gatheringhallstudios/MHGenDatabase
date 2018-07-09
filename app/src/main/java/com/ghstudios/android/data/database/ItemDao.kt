package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.cursors.ArmorCursor
import com.ghstudios.android.data.cursors.ItemCursor
import com.ghstudios.android.data.util.SqlFilter
import com.ghstudios.android.data.util.localizeColumn
import com.ghstudios.android.firstOrNull
import com.ghstudios.android.toList

class ItemDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    private val column_name
        get() = localizeColumn("name")

    private val column_description
        get() = localizeColumn("description")

    private val item_columns
        get() = "_id, $column_name name, name_ja, $column_description description, " +
                "type, sub_type, rarity, carry_capacity, buy, sell, icon_name, armor_dupe_name_fix "

    // todo: add family, remove "item only" fields like carry cap
    private val armor_columns
        get() = "_id, $column_name name, name_ja, $column_description description, " +
                "rarity, slot, gender, hunter_type, num_slots, " +
                "defense, max_defense, fire_res, thunder_res, dragon_res, water_res, ice_res, " +
                "type, sub_type, carry_capacity, buy, sell, icon_name, armor_dupe_name_fix"


    /**
     * ****************************** ITEM QUERIES *****************************************
     */

    /**
     * Get all items
     */
    fun queryItems(): ItemCursor {
        return ItemCursor(db.rawQuery("""
            SELECT $item_columns
            FROM items
            ORDER BY _id
        """, emptyArray()))
    }

    /*
     * Get a specific item
     */
    fun queryItem(id: Long): Item? {
        return ItemCursor(db.rawQuery("""
            SELECT $item_columns
            FROM items
            WHERE _id = ?
        """, arrayOf(id.toString()))).toList { it.item }.firstOrNull()
    }

    /*
     * Get items based on search text
     */
    fun queryItemSearch(searchTerm: String?): ItemCursor {
        if (searchTerm?.trim().isNullOrBlank()) {
            return queryItems()
        }

        val filter = SqlFilter(column_name, searchTerm!!)

        return ItemCursor(db.rawQuery("""
            SELECT $item_columns
            FROM items
            WHERE ${filter.predicate}
            ORDER BY _id
        """, arrayOf(*filter.parameters)))
    }

    /**
     * ****************************** ARMOR QUERIES *****************************************
     */

    /**
     * Get all armor
     */
    fun queryArmor(): ArmorCursor {
        return ArmorCursor(db.rawQuery("""
            SELECT $armor_columns
            FROM armor a LEFT OUTER JOIN items i USING (_id)
        """, emptyArray()))
    }

    /**
     * Get a specific armor
     */
    fun queryArmor(id: Long): Armor? {
        return ArmorCursor(db.rawQuery("""
            SELECT $armor_columns
            FROM armor a LEFT OUTER JOIN items i USING (_id)
            WHERE a._id = ?
        """, arrayOf(id.toString()))).firstOrNull { it.armor }
    }

    /**
     * Get a specific armor based on hunter type.
     * If "BOTH" is passed, then its equivalent to querying all armor
     */
    fun queryArmorType(type: Int): ArmorCursor {
        return ArmorCursor(db.rawQuery("""
            SELECT $armor_columns
            FROM armor a LEFT OUTER JOIN items i USING (_id)
            WHERE a.hunter_type = @type OR a.hunter_type = 2 OR @type = '2'
        """, arrayOf(type.toString())))
    }
}