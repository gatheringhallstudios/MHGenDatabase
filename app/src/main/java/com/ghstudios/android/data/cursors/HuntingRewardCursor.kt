package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.HuntingReward
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

/**
 * A convenience class to wrap a cursor that returns rows from the "hunting_reward"
 * table. The [] method will give you a HuntingReward instance
 * representing the current row.
 */
class HuntingRewardCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Returns a HuntingReward object configured for the current row
     */
    val huntingReward: HuntingReward
        get() {
            val huntingReward = HuntingReward().apply {
                id = getLong(S.COLUMN_HUNTING_REWARDS_ID)
                condition = getString(S.COLUMN_HUNTING_REWARDS_CONDITION)
                rank = getString(S.COLUMN_HUNTING_REWARDS_RANK)
                stackSize = getInt(S.COLUMN_HUNTING_REWARDS_STACK_SIZE)
                percentage = getInt(S.COLUMN_HUNTING_REWARDS_PERCENTAGE)
            }

            huntingReward.item = Item().apply {
                id = getLong(S.COLUMN_HUNTING_REWARDS_ITEM_ID)
                name = getString("item_name")
                fileLocation = getString("item_icon_name")
                iconColor = getInt("item_icon_color")
            }

            huntingReward.monster = Monster().apply {
                id = getLong(S.COLUMN_HUNTING_REWARDS_MONSTER_ID)
                name = getString("monster_name") ?: ""
                fileLocation = getString("monster_icon_name") ?: ""
            }

            return huntingReward
        }

}