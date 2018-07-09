package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.AppSettings
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterSize
import com.ghstudios.android.data.cursors.MonsterCursor
import com.ghstudios.android.data.util.SqlFilter
import com.ghstudios.android.data.util.localizeColumn
import com.ghstudios.android.toList

class MonsterDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    private val column_name
        get() = localizeColumn("name")

    private val monster_columns
        get() = "_id, $column_name name, name_ja, class, icon_name, signature_move"

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
     * Get all small monsters
     */
    fun queryMonsters(size: MonsterSize): MonsterCursor {
        val monsterClass = when(size) {
            MonsterSize.SMALL -> 1
            MonsterSize.LARGE -> 0
        }

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
}