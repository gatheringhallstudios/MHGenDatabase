package com.ghstudios.android.data.database

import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.classes.meta.ArmorMetadata
import com.ghstudios.android.data.classes.meta.ItemMetadata
import com.ghstudios.android.data.classes.meta.MonsterMetadata
import com.ghstudios.android.data.util.*
import com.ghstudios.android.util.toList
import com.ghstudios.android.util.useCursor

/**
 * A database dao concerned with retrieving "metadata" objects.
 * Metadata objects are used to load an initial run of data that can be used to decide
 * what tabs to show or hide.
 */
class MetadataDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    val col_name get() = localizeColumn("name")

    /**
     * Queries for a monster's metadata
     */
    fun queryMonsterMetadata(monsterId : Long): MonsterMetadata? {
        val cursor = db.rawQuery("""
            SELECT m._id, m.$col_name name,metadata,
                (SELECT 1 FROM monster_damage d WHERE d.monster_id = m._id LIMIT 1) has_damage,
                (SELECT 1 FROM monster_status s WHERE s.monster_id = m._id LIMIT 1) has_status
            FROM monsters m
            WHERE m._id = ?
            """, arrayOf(monsterId.toString()))

        return cursor.toList {
            val meta = it.getInt("metadata")
            MonsterMetadata(
                    id = monsterId,
                    name = it.getString("name") ?: "",
                    hasDamageData = it.getBoolean("has_damage"),
                    hasStatusData = it.getBoolean("has_status"),
                    hasLowRank = meta.and(1) > 0,
                    hasHighRank = meta.and(2)> 0,
                    hasGRank =  meta.and(4)>0
            )
        }.firstOrNull()
    }

    /**
     * Queries for an item's metadata, which decides what data an item has available.
     * TODO: The query needs indices. Unfortunately right now this does not perform well
     */
    fun queryItemMetadata(itemId: Long): ItemMetadata? {
        val cursor = db.rawQuery("""
            SELECT item._id, item.$col_name name,
                (
                    SELECT 1
                    FROM combining c
                    WHERE c.item_1_id = item._id
                    OR c.item_2_id = item._id
                    LIMIT 1) usedInCombining,
                (SELECT 1 FROM components c WHERE component_item_id = item._id LIMIT 1) usedInCrafting,
                (SELECT 1 FROM hunting_rewards r WHERE item_id = item._id LIMIT 1) isMonsterReward,
                (SELECT 1 FROM quest_rewards r WHERE item_id = item._id LIMIT 1) isQuestReward,
                (SELECT 1 FROM gathering g WHERE item_id = item._id LIMIT 1) isGatherable
            FROM items item
            WHERE item._id = ?
        """, arrayOf(itemId.toString()))

        return cursor.toList {
            ItemMetadata(
                    id = itemId,
                    name = it.getString("name") ?: "",
                    usedInCombining = it.getBoolean("usedInCombining"),
                    usedInCrafting = it.getBoolean("usedInCrafting"),
                    isMonsterReward = it.getBoolean("isMonsterReward"),
                    isQuestReward = it.getBoolean("isQuestReward"),
                    isGatherable = it.getBoolean("isGatherable")
            )
        }.firstOrNull()
    }

    private fun parseArmorSetMetadataCursor(c: Cursor): List<ArmorMetadata> {
        return c.toList {
            ArmorMetadata(
                    id = it.getLong("_id"),
                    name = it.getString("name") ?: "",
                    slot = it.getString("slot") ?: "",
                    rarity = it.getInt("rarity"),
                    family = it.getLong("family"),
                    familyName = it.getString("fname") ?: "",
                    icon_name = it.getString("icon_name") ?: ""
            )
        }
    }

    fun queryArmorSetMetadataByFamily(family: Long): List<ArmorMetadata> {
        val cursor = db.rawQuery("""
            SELECT a._id, a.slot, i.$col_name name, i.icon_name,a.family, i.rarity, af.name AS fname
            FROM armor a
                JOIN items i
                    ON i._id = a._id
                JOIN armor_families af ON af._id=a.family
            WHERE family = ?
        """, arrayOf(family.toString()))

        return parseArmorSetMetadataCursor(cursor)
    }

    fun queryArmorSetMetadataByArmor(armorId: Long): List<ArmorMetadata> {
        val cursor = db.rawQuery("""
            SELECT a._id, a.slot, i.name name, i.icon_name, a.family, i.rarity, af.name AS fname
            FROM armor a
                JOIN items i
                    ON i._id = a._id
                JOIN armor_families af ON af._id=a.family
            WHERE family = (SELECT family FROM armor WHERE _id = ?)
        """, arrayOf(armorId.toString()))

        return parseArmorSetMetadataCursor(cursor)
    }

}