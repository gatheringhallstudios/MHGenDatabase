package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper
import com.ghstudios.android.data.classes.*

import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.*

/**
 * A convenience class to wrap a cursor that returns rows from the "monster_to_quest"
 * table. The getMonsterToQuest() method will give you a MonsterToQuest instance
 * representing the current row.
 */
class MonsterToQuestCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Returns a MonsterToQuest object configured for the current row, or null if the
     * current row is invalid.
     */
    // Get the Quest
    //			String goal = getString(S.COLUMN_QUESTS_GOAL));
    //			String type = getString(S.COLUMN_QUESTS_TYPE));
    //			int timeLimit = getInt(S.COLUMN_QUESTS_TIME_LIMIT));
    //			int fee = getInt(S.COLUMN_QUESTS_FEE));
    //			int reward = getInt(S.COLUMN_QUESTS_REWARD));
    //			int hrp = getInt(S.COLUMN_QUESTS_HRP));
    //			quest.setGoal(goal);
    //			quest.setType(type);
    //			quest.setTimeLimit(timeLimit);
    //			quest.setFee(fee);
    //			quest.setReward(reward);
    //			quest.setHrp(hrp);
    // Get the Monster
    //			String monsterClass = getString(S.COLUMN_MONSTERS_CLASS));
    //			monster.setMonsterClass(monsterClass);
    val monsterToQuest: MonsterToQuest
        get() {
            val monster_to_quest = MonsterToQuest()

            monster_to_quest.id = getLong(S.COLUMN_MONSTER_TO_QUEST_ID)
            monster_to_quest.isUnstable = getBoolean(S.COLUMN_MONSTER_TO_QUEST_UNSTABLE)
            monster_to_quest.isHyper = getBoolean(S.COLUMN_MONSTER_TO_QUEST_HYPER)

            monster_to_quest.quest = Quest().apply {
                id = getLong(S.COLUMN_MONSTER_TO_QUEST_QUEST_ID)
                name = getString("q" + S.COLUMN_QUESTS_NAME) ?: ""
                hub = QuestHub.from(getString(S.COLUMN_QUESTS_HUB)!!)
                stars = getString(S.COLUMN_QUESTS_STARS)
            }

            monster_to_quest.monster = Monster().apply {
                id = getLong(S.COLUMN_MONSTER_TO_QUEST_MONSTER_ID)
                name = getString("m" + S.COLUMN_MONSTERS_NAME) ?: ""
                fileLocation = getString(S.COLUMN_MONSTERS_FILE_LOCATION) ?: ""
            }

            if (hasColumn(S.COLUMN_HABITAT_AREAS)) {
                val areas = getString(S.COLUMN_HABITAT_AREAS)

                if (areas != null) {
                    val areasInt = areas
                            .split(",")
                            .dropLastWhile { it.isEmpty() }
                            .map { it.toLong() }
                            .toLongArray()

                    val hab = Habitat()
                    hab.start = getLong(S.COLUMN_HABITAT_START)
                    hab.rest = getLong(S.COLUMN_HABITAT_REST)
                    hab.areas = areasInt
                    monster_to_quest.habitat = hab
                }
            }

            return monster_to_quest
        }
}