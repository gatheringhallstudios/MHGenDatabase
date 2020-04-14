package com.ghstudios.android.features.quests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

/**
 * A ViewModel for the entirety of quest detail data.
 * This should be attached to the activity or fragment owning the viewpager.
 */
class QuestDetailViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    val rewards = MutableLiveData<List<QuestReward>>()
    val monsters = MutableLiveData<List<MonsterToQuest>>()
    val quest = MutableLiveData<Quest>()
    val gatherings = MutableLiveData<List<Gathering>>()
    val huntingRewards = MutableLiveData<List<HuntingReward>>()

    fun setQuest(questId: Long): Quest? {
        if (questId == quest.value?.id) {
            return quest.value!!
        }

        val quest = dataManager.getQuest(questId)
        this.quest.value = quest

        if (quest == null) {
            Log.e(this.javaClass.simpleName, "Quest id is unexpectedly null")
            return null
        }

        loggedThread("Quest Load") {
            monsters.postValue(dataManager.queryMonsterToQuestQuest(questId).toList { it.monsterToQuest })
            rewards.postValue(dataManager.queryQuestRewardQuest(questId).toList { it.questReward })

            if (quest.hasGatheringItem) {
                val locationId = quest.location?.id ?: -1
                val gatherData = dataManager.queryGatheringForQuest(quest.id, locationId, quest.rank ?: "").toList {
                    it.gathering
                }
                gatherings.postValue(gatherData)
            }else if(quest.hasHuntingRewardItem){
                val rewardData = dataManager.queryHuntingRewardForQuest(quest.id,quest.rank?:"").toList {
                    it.huntingReward
                }
                huntingRewards.postValue(rewardData)
            }
        }

        return quest
    }
}