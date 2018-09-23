package com.ghstudios.android.data

import android.content.Context
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.data.cursors.ASBSessionCursor
import com.ghstudios.android.data.cursors.ASBSetCursor
import com.ghstudios.android.data.database.MonsterHunterDatabaseHelper
import com.ghstudios.android.util.firstOrNull

class ASBManager internal constructor(
        private val mAppContext: Context,
        private val mHelper: MonsterHunterDatabaseHelper
) {
    fun queryASBSets(): ASBSetCursor {
        return mHelper.queryASBSets()
    }

    fun getASBSet(id: Long): ASBSet? {
        return mHelper.queryASBSet(id).firstOrNull { it.asbSet }
    }

    /** Get a cursor with a list of all armor sets.  */
    fun queryASBSessions(): ASBSessionCursor {
        return mHelper.queryASBSessions()
    }

    /** Get a specific armor set.  */
    fun getASBSession(id: Long): ASBSession? {
        return mHelper.queryASBSession(id).firstOrNull { it.getASBSession(mAppContext) }
    }

    /** Adds a new ASB set to the list.  */
    fun queryAddASBSet(name: String, rank: Int, hunterType: Int) {
        mHelper.queryAddASBSet(name, rank, hunterType)
    }

    // todo: rename, or replace for something else
    /** Adds a new set that is a copy of the designated set to the list.  */
    fun queryAddASBSet(setId: Long) {
        val set = getASBSet(setId)
        mHelper.queryAddASBSet(set!!.name!!, set.rank, set.hunterType)
    }

    fun queryDeleteASBSet(setId: Long) {
        mHelper.queryDeleteASBSet(setId)
    }

    fun queryUpdateASBSet(setId: Long, name: String, rank: Int, hunterType: Int) {
        mHelper.queryUpdateASBSet(setId, name, rank, hunterType)
    }

    fun queryPutASBSessionArmor(asbSetId: Long, armorId: Long, pieceIndex: Int) {
        mHelper.queryAddASBSessionArmor(asbSetId, armorId, pieceIndex)
    }

    fun queryRemoveASBSessionArmor(asbSetId: Long, pieceIndex: Int) {
        mHelper.queryAddASBSessionArmor(asbSetId, -1, pieceIndex)
    }

    fun queryPutASBSessionDecoration(asbSetId: Long, decorationId: Long, pieceIndex: Int, decorationIndex: Int) {
        mHelper.queryPutASBSessionDecoration(asbSetId, decorationId, pieceIndex, decorationIndex)
    }

    fun queryRemoveASBSessionDecoration(asbSetId: Long, pieceIndex: Int, decorationIndex: Int) {
        mHelper.queryPutASBSessionDecoration(asbSetId, -1, pieceIndex, decorationIndex)
    }

    fun queryCreateASBSessionTalisman(asbSetId: Long, type: Int, slots: Int, skill1Id: Long, skill1Points: Int, skill2Id: Long, skill2Points: Int) {
        mHelper.queryCreateASBSessionTalisman(asbSetId, type, slots, skill1Id, skill1Points, skill2Id, skill2Points)
    }

    fun queryRemoveASBSessionTalisman(asbSetId: Long) {
        mHelper.queryRemoveASBSessionTalisman(asbSetId)
    }
}