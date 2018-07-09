package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.classes.meta.ItemMetadata
import com.ghstudios.android.data.classes.meta.MonsterMetadata
import com.ghstudios.android.data.util.getBoolean
import com.ghstudios.android.data.util.getString
import com.ghstudios.android.data.util.localizeColumn
import com.ghstudios.android.toList

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
            SELECT m._id, m.$col_name name,
                (SELECT 1 FROM monster_damage d WHERE d.monster_id = m._id LIMIT 1) has_damage,
                (SELECT 1 FROM monster_status s WHERE s.monster_id = m._id LIMIT 1) has_status
            FROM monsters m
            WHERE m._id = ?
            """, arrayOf(monsterId.toString()))

        return cursor.use {
            cursor.toList {
                MonsterMetadata(
                        id = monsterId,
                        name = it.getString("name"),
                        hasDamageData = it.getBoolean("has_damage"),
                        hasStatusData = it.getBoolean("has_status")
                )
            }.firstOrNull()
        }
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

        return cursor.use {
            cursor.toList {
                ItemMetadata(
                        id = itemId,
                        name = it.getString("name"),
                        usedInCombining = it.getBoolean("usedInCombining"),
                        usedInCrafting = it.getBoolean("usedInCrafting"),
                        isMonsterReward = it.getBoolean("isMonsterReward"),
                        isQuestReward = it.getBoolean("isQuestReward"),
                        isGatherable = it.getBoolean("isGatherable")
                )
            }.firstOrNull()
        }
    }
}