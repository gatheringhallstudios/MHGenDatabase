package com.ghstudios.android.features.monsters.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

enum class WeaknessRating {
    IMMUNE,
    RESISTS,
    REGULAR,
    WEAK,
    VERY_WEAK
}

data class MonsterWeaknessValue(val type: WeaknessType, val value: Int) {
    val rating = when(value) {
        0, 1, 2, 3 -> WeaknessRating.RESISTS
        4 -> WeaknessRating.REGULAR
        5 -> WeaknessRating.WEAK
        6, 7 -> WeaknessRating.VERY_WEAK
        else -> throw IllegalArgumentException("Invalid weakness value $value")
    }
}

data class MonsterWeaknessStateResult(
        val state: String,
        val elementAndStatus: List<MonsterWeaknessValue>,
        val traps: List<WeaknessType>,
        val bombs: List<WeaknessType>
)

data class MonsterWeaknessResult(
        val normalState: MonsterWeaknessStateResult,
        val altStates: List<MonsterWeaknessStateResult>,
        val weaknessesDiffer : Boolean,
        val trapsDiffer: Boolean,
        val bombsDiffer: Boolean
)

/**
 * A viewmodel for the entirety of monster detail data.
 * This should be attached to the activty or fragment owning the viewpager.
 */
class MonsterDetailViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val monsterData = MutableLiveData<Monster>()
    val rawWeaknessData = MutableLiveData<List<MonsterWeakness>>()
    val ailmentData = MutableLiveData<List<MonsterAilment>>()
    val habitatData = MutableLiveData<List<Habitat>>()
    val damageData = MutableLiveData<List<MonsterDamage>>()
    val statusData = MutableLiveData<List<MonsterStatus>>()

    var monsterId = -1L

    /**
     * Sets the viewmodel to represent a monster, and loads the viewmodels
     * Does nothing if the data is already loaded.
     */
    fun setMonster(monsterId : Long) {
        if (this.monsterId == monsterId) {
            return
        }

        this.monsterId = monsterId

        Thread {
            // load and post monster first (high priority)
            monsterData.postValue(dataManager.getMonster(monsterId))

            // then load the rest

            rawWeaknessData.postValue(dataManager.queryMonsterWeaknessArray(monsterId))
            ailmentData.postValue(dataManager.queryAilmentsFromId(monsterId).toList { it.ailment })
            habitatData.postValue(dataManager.queryHabitatMonster(monsterId).toList { it.habitat })
            damageData.postValue(dataManager.queryMonsterDamageArray(monsterId))
            statusData.postValue(dataManager.queryMonsterStatus(monsterId))
        }.start()
    }

    /**
     * A livedata that returns processed weakness results.
     * Performs a transmation on raw weakness data to get data prepared for the summary
     */
    val weaknessData = Transformations.map(rawWeaknessData) { weaknessList ->
        if (weaknessList == null || weaknessList.isEmpty()) {
            return@map null
        }

        // internal helper function to calculate top weaknesses
        fun calculateTopWeaknesses(weaknessMap: Map<WeaknessType, Int>, count: Int): List<MonsterWeaknessValue> {
            val weaknesses = weaknessMap
                    .map { MonsterWeaknessValue(it.key, it.value) }
                    .filter { it.rating != WeaknessRating.RESISTS }
                    .sortedByDescending { it.value }

            // take the top two weaknesses
            val results = weaknesses.take(count).toMutableList()

            if (weaknesses.size > count) {
                // add all weaknesses equivalent to the last taken weakness as well
                results += weaknesses.drop(count).takeWhile { it.value == results.last().value }
            }

            return results
        }

        val stateResults = weaknessList.map {
            val topElement = calculateTopWeaknesses(it.elementWeaknesses, 2)
            val topStatus = calculateTopWeaknesses(it.statusWeaknesses, 1)
            MonsterWeaknessStateResult(
                    state = it.state ?: "",
                    elementAndStatus = topElement + topStatus,
                    traps = it.vulnerableTraps,
                    bombs = it.vulnerableBombs)
        }

        val stateSequence = stateResults.asSequence()

        // returns results
        MonsterWeaknessResult(
                normalState = stateResults.first(),
                altStates = stateResults.drop(1),
                weaknessesDiffer = stateSequence.map { it.elementAndStatus }.distinct().count() > 1,
                trapsDiffer = stateSequence.map { it.traps }.distinct().count() > 1,
                bombsDiffer = stateSequence.map { it.bombs }.distinct().count() > 1
        )
    }
}