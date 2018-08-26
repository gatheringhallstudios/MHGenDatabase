package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.AppSettings
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.cursors.*
import com.ghstudios.android.data.util.SqlFilter
import com.ghstudios.android.data.util.localizeColumn
import com.ghstudios.android.util.firstOrNull
import com.ghstudios.android.util.toList

class ItemDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    private val column_name
        get() = localizeColumn("name")

    private val column_description
        get() = localizeColumn("description")

    private val item_columns
        get() = "_id, $column_name name, name_ja, $column_description description, " +
                "type, sub_type, rarity, carry_capacity, buy, sell, icon_name, icon_color, account "

    // todo: add family, remove "item only" fields like carry cap
    private val armor_columns
        get() = "_id, $column_name name, name_ja, $column_description description, " +
                "rarity, slot, gender, hunter_type, num_slots, " +
                "defense, max_defense, fire_res, thunder_res, dragon_res, water_res, ice_res, " +
                "type, sub_type, carry_capacity, buy, sell, icon_name, icon_color "


    /**
     * ****************************** ITEM QUERIES *****************************************
     */

    /**
     * Get all items, including equipment like armor and weapons
     */
    fun queryItems(): ItemCursor {
        return ItemCursor(db.rawQuery("""
            SELECT $item_columns
            FROM items
            ORDER BY _id
        """, emptyArray()))
    }

    /**
     * Queries all normal items, controllable via an optional search filter
     */
    fun queryBasicItems(searchTerm: String = ""): ItemCursor {
        val typeItem = ItemTypeConverter.serialize(ItemType.ITEM)

        if (searchTerm.isEmpty()) {
            // return all basic items
            return ItemCursor(db.rawQuery("""
                SELECT $item_columns
                FROM items
                WHERE type IN (?)
                ORDER BY _id
            """, arrayOf(typeItem)))
        } else {
            // return all basic items, filtered
            val filter = SqlFilter(column_name, searchTerm)
            return ItemCursor(db.rawQuery("""
                SELECT $item_columns
                FROM items
                WHERE type in (?)
                  AND ${filter.predicate}
                ORDER BY _id
            """, arrayOf(typeItem, *filter.parameters)))
        }
    }

    /**
     * Get a specific item
     */
    fun queryItem(id: Long): Item? {
        return ItemCursor(db.rawQuery("""
            SELECT $item_columns
            FROM items
            WHERE _id = ?
        """, arrayOf(id.toString()))).toList { it.item }.firstOrNull()
    }

    /**
     * Get items based on search text. Gets all items, including armor and equipment.
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
     * ****************************** COMBINING QUERIES *****************************************
     */

    /**
     * Internal helper that returns the column names for a sub-item in a combine recipe.
     */
    private fun combiningItemColumns(table: String, prefix: String): String {
        val t = table
        val p = prefix

        val columns = arrayOf(
                "_id", "name_ja", "type", "sub_type", "rarity", "carry_capacity",
                "buy", "sell", "icon_name", "icon_color")

        val colName = localizeColumn("$table.name")
        val colDescription = localizeColumn("$table.description")
        return "$colName ${p}name, $colDescription ${p}description, " +
                columns.joinToString(", ") { "$table.$it AS $prefix$it" }
    }

    /*
	 * Get all combinings
	 */
    fun queryCombinings(): CombiningCursor {
        return CombiningCursor(db.rawQuery("""
            SELECT c._id, c.amount_made_min, c.amount_made_max, c.percentage,
                ${combiningItemColumns("crt", "crt")},
                ${combiningItemColumns("mat1", "mat1")},
                ${combiningItemColumns("mat2", "mat2")}
            FROM combining c
                LEFT OUTER JOIN items crt ON c.created_item_id = crt._id
                LEFT OUTER JOIN items mat1 ON c.item_1_id = mat1._id
                LEFT OUTER JOIN items mat2 ON c.item_2_id = mat2._id
        """, emptyArray()))
    }

    /**
     * Get a specific combining
     */
    fun queryCombining(id: Long): Combining? {
        return CombiningCursor(db.rawQuery("""
            SELECT c._id, c.amount_made_min, c.amount_made_max, c.percentage,
                ${combiningItemColumns("crt", "crt")},
                ${combiningItemColumns("mat1", "mat1")},
                ${combiningItemColumns("mat2", "mat2")}
            FROM combining c
                LEFT OUTER JOIN items crt ON c.created_item_id = crt._id
                LEFT OUTER JOIN items mat1 ON c.item_1_id = mat1._id
                LEFT OUTER JOIN items mat2 ON c.item_2_id = mat2._id
            WHERE c._id = ?
        """, arrayOf(id.toString()))).firstOrNull { it.combining }
    }

    fun queryCombinationsOnItemID(id: Long): CombiningCursor {
        return CombiningCursor(db.rawQuery("""
            SELECT c._id, c.amount_made_min, c.amount_made_max, c.percentage,
                ${combiningItemColumns("crt", "crt")},
                ${combiningItemColumns("mat1", "mat1")},
                ${combiningItemColumns("mat2", "mat2")}
            FROM combining c
                LEFT OUTER JOIN items crt ON c.created_item_id = crt._id
                LEFT OUTER JOIN items mat1 ON c.item_1_id = mat1._id
                LEFT OUTER JOIN items mat2 ON c.item_2_id = mat2._id
            WHERE crt._id = @id
              OR mat1._id = @id
              OR mat2._id = @id
        """, arrayOf(id.toString())))
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
     * Get armor for family
     */
    fun queryArmorByFamily(id: Long): List<Armor>? {
        return ArmorCursor(db.rawQuery("""
            SELECT $armor_columns
            FROM armor a LEFT OUTER JOIN items i USING (_id)
            WHERE a.family = ?
        """, arrayOf(id.toString()))).toList { it.armor }
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

    fun queryArmorFamilies(type: Int): ArmorFamilyCursor{
        return ArmorFamilyCursor(db.rawQuery("""
            SELECT af._id,af.name,af.rarity,a.hunter_type,st.$column_name AS st_name,SUM(its.point_value) AS point_value,SUM(a.defense) AS min,SUM(a.max_defense) AS max
            FROM armor_families af
                JOIN armor a on a.family=af._id
                JOIN item_to_skill_tree its on a._id=its.item_id
                JOIN skill_trees st on st._id=its.skill_tree_id
            WHERE a.hunter_type=@type OR a.hunter_type=2
            GROUP BY af._id,its.skill_tree_id;
        """, arrayOf(type.toString())))
    }

    fun queryComponentsByArmorFamily(family: Long): ComponentCursor {
        return ComponentCursor(db.rawQuery("""
            SELECT c._id,SUM(c.quantity) AS quantity,c.type,
                   c.created_item_id,cr.name AS crname,cr.type AS crtype,
                        cr.rarity AS crrarity,cr.icon_name AS cricon_name,cr.sub_type AS crsub_type,
                        cr.icon_color AS cricon_color,
                   c.component_item_id,co.name AS coname,co.type AS cotype,
                        co.rarity AS corarity,co.icon_name AS coicon_name,co.sub_type AS cosub_type,
                        co.icon_color AS coicon_color
            FROM armor a
                JOIN components c ON c.created_item_id = a._id
                JOIN items cr ON cr._id=a._id
                JOIN items co ON co._id=c.component_item_id
            WHERE a.family=?
            GROUP BY c.component_item_id
            ORDER BY quantity DESC
        """,arrayOf(family.toString())))
    }

}