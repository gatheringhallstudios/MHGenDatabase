package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterClass
import com.ghstudios.android.data.classes.MonsterClassConverter
import com.ghstudios.android.data.cursors.MonsterCursor
import com.ghstudios.android.data.util.SqlFilter
import com.ghstudios.android.data.util.localizeColumn
import com.ghstudios.android.util.toList

/**
 * A class used to perform queries retrieving monster data,
 * and data stemming from a monster (including hunting rewards)
 */
class MonsterDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    private val column_name
        get() = localizeColumn("name")

    private val monster_columns
        get() = "_id, $column_name name, name_ja, class, icon_name, metadata "

    /**
     * Get all monsters
     */
    fun queryMonsters(): MonsterCursor {
        return MonsterCursor(db.rawQuery("""
            SELECT $monster_columns
            FROM monsters
            ORDER BY $column_name
        """, emptyArray()))
    }

    /**
     * Get all monsters of a particular size. If null, returns all monsters
     */
    fun queryMonsters(size: MonsterClass?): MonsterCursor {
        if (size == null) {
            return queryMonsters()
        }

        val monsterClass = MonsterClassConverter.serialize(size)

        return MonsterCursor(db.rawQuery("""
            SELECT $monster_columns
            FROM monsters
            WHERE ${S.COLUMN_MONSTERS_CLASS} = ?
            ORDER BY $column_name
        """, arrayOf(monsterClass.toString())))
    }

    fun queryMonstersSearch(searchTerm: String?): MonsterCursor {
        if (searchTerm?.trim().isNullOrBlank()) {
            return queryMonsters()
        }

        val filter = SqlFilter(column_name, searchTerm!!)

        return MonsterCursor(db.rawQuery("""
            SELECT $monster_columns
            FROM monsters
            WHERE ${filter.predicate}
            ORDER BY $column_name ASC
        """, arrayOf(*filter.parameters)))
    }

    /**
     * Get a specific monster
     */
    fun queryMonster(id: Long): Monster? {
        return MonsterCursor(db.rawQuery("""
            SELECT $monster_columns
            FROM monsters
            WHERE _id = ?
        """, arrayOf(id.toString()))).toList { it.monster }.firstOrNull()
    }

    fun queryDeviantMonsterNames():Array<String>{
        return db.rawQuery("""
            SELECT DISTINCT permit_monster_id, m.$column_name
            FROM quests
            JOIN monsters m ON m._id=permit_monster_id
            WHERE hub="Permit"
            ORDER BY stars
        """, emptyArray()).toList { it.getString(1) }.toTypedArray()
    }
}