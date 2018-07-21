package com.ghstudios.android.features.quests

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.MonsterToQuest
import com.ghstudios.android.data.classes.Quest
import com.ghstudios.android.data.classes.QuestReward
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.loggedThread
import com.ghstudios.android.toList

/**
 * A ViewModel for the entirety of quest detail data.
 * This should be attached to the activity or fragment owning the viewpager.
 */
class QuestDetailViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val rewards = MutableLiveData<List<QuestReward>>()
    val monsters = MutableLiveData<List<MonsterToQuest>>()
    val quest = MutableLiveData<Quest>()

    fun setQuest(questId: Long): Quest{
        this.quest.value = dataManager.getQuest(questId)

        loggedThread("Quest Load") {
            monsters.postValue(dataManager.queryMonsterToQuestQuest(questId).toList { it.monsterToQuest })
            rewards.postValue(dataManager.queryQuestRewardQuest(questId).toList { it.questReward })
        }
        return quest.value!!
    }
}