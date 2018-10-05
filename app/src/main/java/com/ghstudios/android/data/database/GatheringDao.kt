package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.cursors.GatheringCursor
import com.ghstudios.android.data.util.localizeColumn

/**
 * A class used to perform queries retrieving quest data,
 * and data stemming from a quests
 */
class GatheringDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    private val column_name
        get() = localizeColumn("i.name")

    private val gathering_columns
        get() = "$column_name iname, i.name_ja, g.area, g.quantity, g.percentage, g.rare, g.fixed, g.site "

    //Query that returns all gathering locations for a quest
    fun queryGatheringsForQuest(questId:Long, locationId:Long, rank:String):GatheringCursor{
        return GatheringCursor(db.rawQuery("""
            SELECT $gathering_columns
            FROM item_to_quest itq
                INNER JOIN gathering g ON g.item_id = itq.item_id
                INNER JOIN items i ON i._id = itq.item_id
            WHERE itq.quest_id=? AND g.location_id = ? AND g.rank = ?
            ORDER BY i._id, g._id
        """, arrayOf(questId.toString(),locationId.toString(),rank)))
    }

}