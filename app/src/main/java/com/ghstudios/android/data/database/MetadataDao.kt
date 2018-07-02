package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.classes.meta.MonsterMetadata
import com.ghstudios.android.getBoolean
import com.ghstudios.android.getString
import com.ghstudios.android.toList

/**
 * A database dao concerned with retrieving "metadata" objects.
 * Metadata objects are used to load an initial run of data that can be used to decide
 * what tabs to show or hide.
 */
class MetadataDao(val dbMainHelper: SQLiteOpenHelper) {
    // todo: bind this somehow for cross-language queries
    val col_name = "name"

    val db get() = dbMainHelper.writableDatabase

    /**
     * Creates a query for a monster's metadata
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
}