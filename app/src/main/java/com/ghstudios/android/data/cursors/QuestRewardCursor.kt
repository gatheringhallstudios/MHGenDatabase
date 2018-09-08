package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.Quest
import com.ghstudios.android.data.classes.QuestHub
import com.ghstudios.android.data.classes.QuestReward
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

/**
 * A convenience class to wrap a cursor that returns rows from the "quest_reward"
 * table. The [] method will give you a QuestReward instance
 * representing the current row.
 */
class QuestRewardCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Returns a QuestReward object configured for the current row, or null if the
     * current row is invalid.
     */
    // Get the Item
    //			String jpnName = getString(S.COLUMN_ITEMS_JPN_NAME);
    //			String type = getString(S.COLUMN_ITEMS_TYPE);
    //			int rarity = getInt(S.COLUMN_ITEMS_RARITY);
    //			int carry_capacity = getInt(S.COLUMN_ITEMS_CARRY_CAPACITY);
    //			int buy = getInt(S.COLUMN_ITEMS_BUY);
    //			int sell = getInt(S.COLUMN_ITEMS_SELL);
    //			String description = getString(S.COLUMN_ITEMS_DESCRIPTION);
    //			String armor_dupe_name_fix = getString(S.COLUMN_ITEMS_ARMOR_DUPE_NAME_FIX);
    //			item.setJpnName(jpnName);
    //			item.setType(type);
    //			item.setRarity(rarity);
    //			item.setCarryCapacity(carry_capacity);
    //			item.setBuy(buy);
    //			item.setSell(sell);
    //			item.setDescription(description);
    //			item.setArmorDupeNameFix(armor_dupe_name_fix);
    // Get the Quest
    //			String goal = getString(S.COLUMN_QUESTS_GOAL);
    //			String type = getString(S.COLUMN_QUESTS_TYPE);
    //			int timeLimit = getInt(S.COLUMN_QUESTS_TIME_LIMIT);
    //			int fee = getInt(S.COLUMN_QUESTS_FEE);
    //			int reward = getInt(S.COLUMN_QUESTS_REWARD);
    //			int hrp = getInt(S.COLUMN_QUESTS_HRP);
    //			quest.setGoal(goal);
    //			quest.setType(type);
    //			quest.setTimeLimit(timeLimit);
    //			quest.setFee(fee);
    //			quest.setReward(reward);
    //			quest.setHrp(hrp);
    val questReward: QuestReward
        get() {
            val questReward = QuestReward().apply {
                id = getLong(S.COLUMN_QUEST_REWARDS_ID)
                rewardSlot = getString(S.COLUMN_QUEST_REWARDS_REWARD_SLOT)
                percentage = getInt(S.COLUMN_QUEST_REWARDS_PERCENTAGE)
                stackSize = getInt(S.COLUMN_QUEST_REWARDS_STACK_SIZE)
            }

            questReward.item = Item().apply {
                id = getLong(S.COLUMN_QUEST_REWARDS_ITEM_ID)
                name = getString("i" + S.COLUMN_ITEMS_NAME)
                fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME)
                iconColor = getInt(S.COLUMN_ITEMS_ICON_COLOR)
            }

            questReward.quest = Quest().apply {
                id = getLong(S.COLUMN_QUEST_REWARDS_QUEST_ID)
                name = getString("q" + S.COLUMN_QUESTS_NAME) ?: ""
                hub = QuestHub.from(getString(S.COLUMN_QUESTS_HUB)!!)
                stars = getString(S.COLUMN_QUESTS_STARS)
            }

            return questReward
        }
}
