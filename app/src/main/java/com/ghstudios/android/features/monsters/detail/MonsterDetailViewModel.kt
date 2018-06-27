package com.ghstudios.android.features.monsters.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.cursors.MonsterAilmentCursor
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

class MonsterDetailViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val monsterData = MutableLiveData<Monster>()
    val weaknessData = MutableLiveData<List<MonsterWeakness>>()
    val ailmentData = MutableLiveData<List<MonsterAilment>>()
    val habitatData = MutableLiveData<List<Habitat>>()
    val damageData = MutableLiveData<List<MonsterDamage>>()
    val statusData = MutableLiveData<List<MonsterStatus>>()

    var monsterId = -1L

    fun setMonster(monsterId : Long) {
        if (this.monsterId == monsterId) {
            return
        }

        this.monsterId = monsterId

        Thread {
            // load and post monster first (high priority)
            monsterData.postValue(dataManager.getMonster(monsterId))

            // then load the rest

            weaknessData.postValue(dataManager.queryMonsterWeaknessArray(monsterId))
            ailmentData.postValue(dataManager.queryAilmentsFromId(monsterId).toList { it.ailment })
            habitatData.postValue(dataManager.queryHabitatMonster(monsterId).toList { it.habitat })
            damageData.postValue(dataManager.queryMonsterDamageArray(monsterId))
            statusData.postValue(dataManager.queryMonsterStatus(monsterId))
        }.start()
    }
}