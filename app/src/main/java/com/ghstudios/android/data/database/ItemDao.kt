package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.AppSettings
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.cursors.*
import com.ghstudios.android.data.util.*
import com.ghstudios.android.util.firstOrNull
import com.ghstudios.android.util.forEach
import com.ghstudios.android.util.toList
import com.ghstudios.android.util.useCursor

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
    /**
     * Returns columns for armor, prefixed using an armor prefix and item prefix
     * to avoid ambiguity
     */
    private fun armor_columns(a : String, i: String) =
            "$a._id, $i.$column_name name, $i.name_ja, $column_description description, " +
            "$a.family, rarity, slot, gender, hunter_type, num_slots, " +
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
    fun queryItemSearch(searchTerm: String?, includeTypes: List<ItemType> = emptyList()): ItemCursor {
        if (searchTerm == null || searchTerm.isBlank()) {
            return queryItems()
        }

        val filter = SqlFilter(column_name, searchTerm)

        val typePredicate = when {
            includeTypes.isEmpty() -> "TRUE"
            else -> "(" + includeTypes.joinToString(" OR ") {
                "type = '${ItemTypeConverter.serialize(it)}'"
            } + ")"
        }

        return ItemCursor(db.rawQuery("""
            SELECT $item_columns
            FROM items
            WHERE ${filter.predicate} AND $typePredicate
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
            SELECT ${armor_columns("a", "i")}
            FROM armor a JOIN items i USING (_id)
        """, emptyArray()))
    }

    fun queryArmorSearch(searchTerm: String): ArmorCursor {
        val filter = SqlFilter(column_name, searchTerm)
        return ArmorCursor(db.rawQuery("""
            SELECT ${armor_columns("a", "i")}
            FROM armor a JOIN items i USING (_id)
            WHERE ${filter.predicate}
        """, filter.parameters))
    }

    /**
     * Get a specific armor
     */
    fun queryArmor(id: Long): Armor? {
        return ArmorCursor(db.rawQuery("""
            SELECT ${armor_columns("a", "i")}
            FROM armor a JOIN items i USING (_id)
            WHERE a._id = ?
        """, arrayOf(id.toString()))).firstOrNull { it.armor }
    }

    /**
     * Get armor for family
     */
    fun queryArmorByFamily(id: Long): List<Armor> {
        return ArmorCursor(db.rawQuery("""
            SELECT ${armor_columns("a", "i")}
            FROM armor a JOIN items i USING (_id)
            WHERE a.family = ?
        """, arrayOf(id.toString()))).toList { it.armor }
    }

    /**
     * Get a list of armor based on hunter type.
     * If "BOTH" is passed, then its equivalent to querying all armor
     */
    fun queryArmorType(type: Int): ArmorCursor {
        return ArmorCursor(db.rawQuery("""
            SELECT ${armor_columns("a", "i")}
            FROM armor a JOIN items i USING (_id)
            WHERE a.hunter_type = @type OR a.hunter_type = 2 OR @type = '2'
        """, arrayOf(type.toString())))
    }

    /**
     * Get a list of armor based on hunter type with a list of all awarded skill points.
     * If "BOTH" is passed, then its equivalent to querying all armor.
     * If NULL is passed for armorSlot, then it queries all armor types.
     */
    fun queryArmorSkillPointsByType(armorSlot: String, hunterType: Int): List<ArmorSkillPoints> {
        // note we use armor cursor as its basically armor + a few columns
        val cursor = ArmorCursor(db.rawQuery("""
            SELECT ${armor_columns("a", "i")}, st._id as st_id, st.$column_name AS st_name, st.name_ja as st_name_ja, its.point_value
            FROM armor a
                JOIN items i USING (_id)
                LEFT JOIN item_to_skill_tree its on its.item_id = a._id
                LEFT JOIN skill_trees st on st._id = its.skill_tree_id
            WHERE (a.hunter_type = @type OR a.hunter_type = 2 OR @type = '2')
              AND (a.slot = @slot OR @slot IS NULL)
            ORDER BY i.rarity, i.$column_name ASC
        """, arrayOf(hunterType.toString(), armorSlot)))

        // stores armor and skills as its processed
        val armorMap = LinkedHashMap<Long, Armor>()
        val armorToSkills = LinkedHashMap<Long, MutableList<SkillTreePoints>>()

        // Iterate cursor
        cursor.useCursor {
            while (cursor.moveToNext()) {
                val id = cursor.getLong("_id")
                armorMap.getOrPut(id) { cursor.armor }

                // Ensure there is a skills entry for this armor piece
                val skills = armorToSkills.getOrPut(id) { mutableListOf() }

                // Add skill (if non-null)
                if (!cursor.isNull("st_id")) {
                    val skillPoints = SkillTreePoints(SkillTree(
                            cursor.getLong("st_id"),
                            cursor.getString("st_name"),
                            cursor.getString("st_name_ja")
                    ), cursor.getInt("point_value"))

                    skills.add(skillPoints)
                }
            }
        }

        // assemble results
        return armorToSkills.map {
            ArmorSkillPoints(armorMap[it.key]!!, it.value)
        }
    }

    /**
     * Returns an armor families cursor
     * @param searchFilter the search predicate to filter on
     * @param skipSolos true to skip armor families with a single child, otherwise returns all.
     */
    @JvmOverloads fun queryArmorFamilyBaseSearch(searchFilter: String, skipSolos: Boolean = false): List<ArmorFamilyBase> {
        val sqlFilter = SqlFilter("name", searchFilter)

        val soloPredicate = when (skipSolos) {
            false -> "TRUE"
            true -> "(SELECT count(*) FROM armor a WHERE a.family = af._id) > 1"
        }

        // todo: localize
        return db.rawQuery("""
            SELECT af._id, COALESCE(af.$column_name, af.name) name, af.rarity, af.hunter_type
            FROM armor_families af
            WHERE ${sqlFilter.predicate}
              AND $soloPredicate
            ORDER BY af.rarity, af._id
        """, sqlFilter.parameters).toList {
            ArmorFamilyBase().apply {
                id = it.getLong("_id")
                name = it.getString("name")
                rarity = it.getInt("rarity")
                hunterType = it.getInt("hunter_type")
            }
        }
    }

    fun queryArmorFamilies(type: Int): List<ArmorFamily> {
        // todo: localize
        // todo: clean up. Should be 3 queries, not 2. This mechanism is harder to understand

        val slotsByFamilyId = linkedMapOf<Long, MutableList<Int>>()

        db.rawQuery("""
            SELECT af._id, a.num_slots
            FROM armor a
                JOIN armor_families af ON af._id = a.family
            WHERE a.hunter_type=@type OR a.hunter_type=2
            ORDER BY a._id ASC""", arrayOf(type.toString())
        ).forEach {
            val id = it.getLong("_id")
            val slots = it.getInt("num_slots")
            slotsByFamilyId.getOrPut(id) { mutableListOf() }.add(slots)
        }

        val cursor = ArmorFamilyCursor(db.rawQuery("""
            SELECT af._id, COALESCE(af.$column_name, af.name) name, af.rarity, af.hunter_type,
                st.$column_name AS st_name,SUM(its.point_value) AS point_value,SUM(a.defense) AS min,SUM(a.max_defense) AS max
            FROM armor_families af
                JOIN armor a on a.family=af._id
                JOIN item_to_skill_tree its on a._id=its.item_id
                JOIN skill_trees st on st._id=its.skill_tree_id
            WHERE a.hunter_type=@type OR a.hunter_type=2
            GROUP BY af._id,its.skill_tree_id;
        """, arrayOf(type.toString())))

        val results = linkedMapOf<Long, ArmorFamily>()

        cursor.forEach {
            val newFamily = cursor.armor
            if (newFamily.id in results) {
                results[newFamily.id]?.skills?.add(newFamily.skills[0])
            } else {
                newFamily.slots.addAll(slotsByFamilyId[newFamily.id] ?: emptyList())
                results[newFamily.id] = newFamily
            }
        }

        return results.values.toList()
    }

    fun queryComponentsByArmorFamily(family: Long): ComponentCursor {
        return ComponentCursor(db.rawQuery("""
            SELECT c._id,SUM(c.quantity) AS quantity,c.type,MAX(c.key) AS key,
                   c.created_item_id,cr.$column_name AS crname,cr.type AS crtype,
                        cr.rarity AS crrarity,cr.icon_name AS cricon_name,cr.sub_type AS crsub_type,
                        cr.icon_color AS cricon_color,
                   c.component_item_id,co.$column_name AS coname,co.type AS cotype,
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

    /**
     * Returns a list of ItemToSkillTree, where each item is of type
     */
    fun queryArmorSkillTreePointsBySkillTree(skillTreeId: Long): List<ItemToSkillTree> {
        val cursor = db.rawQuery("""
            SELECT ${armor_columns("a", "i")},
            st._id as st_id, st.$column_name AS st_name, st.name_ja as st_name_ja, its.point_value
            FROM armor a
                JOIN items i USING (_id)
                LEFT JOIN item_to_skill_tree its on its.item_id = a._id
                LEFT JOIN skill_trees st on st._id = its.skill_tree_id
            WHERE st._id = ?
            ORDER BY i.rarity DESC, its.point_value DESC
        """, arrayOf(skillTreeId.toString()))

        return ArmorCursor(cursor).toList {
            val armor = it.armor

            val skillTree = SkillTree(
                    id=it.getLong("st_id"),
                    name=it.getString("st_name"),
                    jpnName= it.getString("st_name_ja"))
            val points = it.getInt("point_value")

            ItemToSkillTree(skillTree, points).apply {
                this.item = armor
            }
        }
    }
}