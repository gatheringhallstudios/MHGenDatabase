package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.cursors.HuntingRewardCursor
import com.ghstudios.android.data.util.localizeColumn

/**
 * Returns data related to monster item rewards.
 * Rewards can be retrieved relative to an item, or relative to a monster
 */
class HuntingRewardsDao(val dbMainHelper: SQLiteOpenHelper) {
    private val db get() = dbMainHelper.writableDatabase

    private val column_item_name get() = localizeColumn("i.name")
    private val column_monster_name get() = localizeColumn("m.name")

    private val reward_columns
        get() = "h._id, h.monster_id, h.item_id, h.condition, h.rank, h.stack_size, h.percentage, " +
                "$column_item_name item_name, i.icon_name item_icon_name, " +
                "$column_monster_name monster_name, m.icon_name monster_icon_name"

    /*
	 * Get all hunting reward monsters based on item
	 */
    fun queryHuntingRewardItem(id: Long): HuntingRewardCursor {
        return HuntingRewardCursor(db.rawQuery("""
            SELECT $reward_columns
            FROM hunting_rewards h
                LEFT OUTER JOIN items i
                    ON h.item_id = i._id
                LEFT OUTER JOIN monsters m
                    ON h.monster_id = m._id
            WHERE h.item_id = ?
            ORDER BY m._id ASC, h.rank DESC, h._id ASC
        """, arrayOf(id.toString())))
    }

    /*
     * Get all hunting reward items based on monster
     */
    fun queryHuntingRewardMonster(id: Long): HuntingRewardCursor {
        return HuntingRewardCursor(db.rawQuery("""
            SELECT $reward_columns
            FROM hunting_rewards h
                LEFT OUTER JOIN items i
                    ON h.item_id = i._id
                LEFT OUTER JOIN monsters m
                    ON h.monster_id = m._id
            WHERE h.monster_id = ?
        """, arrayOf(id.toString())))
    }

    /*
     * Get all hunting reward items based on monster and rank
     */
    fun queryHuntingRewardMonsterRank(id: Long, rank: String): HuntingRewardCursor {
        return HuntingRewardCursor(db.rawQuery("""
            SELECT $reward_columns
            FROM hunting_rewards h
                LEFT OUTER JOIN items i
                    ON h.item_id = i._id
                LEFT OUTER JOIN monsters m
                    ON h.monster_id = m._id
            WHERE h.monster_id = ?
              AND h.rank = ?
        """, arrayOf(id.toString(), rank)))
    }
}