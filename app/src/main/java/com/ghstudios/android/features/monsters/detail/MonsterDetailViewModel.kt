package com.ghstudios.android.features.monsters.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.classes.meta.MonsterMetadata
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

enum class WeaknessRating {
    IMMUNE,
    RESISTS,
    REGULAR,
    WEAK,
    VERY_WEAK
}

fun getRatingFromValue(value: Int) = when(value) {
    0, 1, 2, 3 -> WeaknessRating.RESISTS
    4 -> WeaknessRating.REGULAR
    5 -> WeaknessRating.WEAK
    6, 7 -> WeaknessRating.VERY_WEAK
    else -> throw IllegalArgumentException("Invalid weakness value $value")
}

data class MonsterWeaknessValue<T>(val type: T, val value: Int) {
    val rating = getRatingFromValue(value)
}

data class MonsterWeaknessResult(
        var state: String, // editable since under certain conditions it becomes "all states"
        val element: List<MonsterWeaknessValue<ElementStatus>>,
        val status: List<MonsterWeaknessValue<ElementStatus>>,
        val items: List<WeaknessType>
)

/**
 * A viewmodel for the entirety of monster detail data.
 * This should be attached to the activty or fragment owning the viewpager.
 */
class MonsterDetailViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()


    val monsterData = MutableLiveData<Monster>()
    val weaknessData = MutableLiveData<List<MonsterWeaknessResult>>()
    val ailmentData = MutableLiveData<List<MonsterAilment>>()
    val habitatData = MutableLiveData<List<Habitat>>()
    val damageData = MutableLiveData<List<MonsterDamage>>()
    val statusData = MutableLiveData<List<MonsterStatus>>()
    val rewardLRData = MutableLiveData<List<HuntingReward>>()
    val rewardHRData = MutableLiveData<List<HuntingReward>>()
    val rewardGData = MutableLiveData<List<HuntingReward>>()

    var monsterId = -1L
    private var metadata: MonsterMetadata? = null

    /**
     * Sets the viewmodel to represent a monster, and loads the viewmodels
     * Does nothing if the data is already loaded.
     */
    fun setMonster(monsterId : Long): MonsterMetadata {
        if (this.monsterId == monsterId && this.metadata != null) {
            return metadata!!
        }

        this.monsterId = monsterId
        this.metadata = dataManager.queryMonsterMetadata(monsterId)

        loggedThread(name = "Monster Loading") {
            // load and post metadata and monster first (high priority)
            monsterData.postValue(dataManager.getMonster(monsterId))

            // then load the rest
            val weaknessRaw = dataManager.queryMonsterWeaknessArray(monsterId)

            // this is a bigger process, so run this in a different thread while we continue loading data
            loggedThread(name = "Weakness processing") {
                weaknessData.postValue(processWeaknessData(weaknessRaw))
            }

            ailmentData.postValue(dataManager.queryAilmentsFromId(monsterId).toList { it.ailment })
            habitatData.postValue(dataManager.queryHabitatMonster(monsterId).toList { it.habitat })
            damageData.postValue(dataManager.queryMonsterDamageArray(monsterId))
            statusData.postValue(dataManager.queryMonsterStatus(monsterId))

            if (metadata?.hasLowRank == true) rewardLRData.postValue(dataManager.queryHuntingRewardMonsterRank(monsterId, "LR").toList { it.huntingReward })
            if (metadata?.hasHighRank == true) rewardHRData.postValue(dataManager.queryHuntingRewardMonsterRank(monsterId, "HR").toList { it.huntingReward })
            if (metadata?.hasGRank == true) rewardGData.postValue(dataManager.queryHuntingRewardMonsterRank(monsterId, "G").toList { it.huntingReward })
        }

        return metadata!!
    }

    /**
     * Internal method to extract the "biggest weaknesses" monster.
     * Returns a custom MonsterWeaknessResult view object
     */
    private fun processWeaknessData(weaknessList: List<MonsterWeakness>): List<MonsterWeaknessResult> {
        if (weaknessList.isEmpty()) {
            return emptyList()
        }

        val stateResults = weaknessList.map(::processWeaknessState)

        // detect if any categories are equivalent, so that we don't show them multiple times
        val stateSequence = stateResults.asSequence()
        val elementsDiffer = stateSequence.map { it.element }.distinct().count() > 1
        val statusesDiffer = stateSequence.map { it.status }.distinct().count() > 1
        val itemsDiffer = stateSequence.map { it.items }.distinct().count() > 1

        // If all categories are equivalent, we need to remove alternative states
        // if there is more than one state, we'll also have to rename the first one
        val anyDiffer = elementsDiffer || statusesDiffer || itemsDiffer
        if (!anyDiffer && stateResults.size > 1) {
            // todo: translate
            stateResults.first().state = "All States"
        }

        return when(anyDiffer) {
            true -> stateResults
            false -> stateResults.take(1) // only take one if states are the same
        }
    }

    /**
     * Internal method
     */
    private fun processWeaknessState(it: MonsterWeakness) : MonsterWeaknessResult {
        // internal helper function to calculate top weaknesses
        fun <T> calculateTopWeaknesses(weaknessMap: Map<T, Int>, count: Int): List<MonsterWeaknessValue<T>> {
            val weaknesses = weaknessMap.asSequence()
                        .filter { getRatingFromValue(it.value) != WeaknessRating.RESISTS }
                        .map { MonsterWeaknessValue(it.key, it.value) }
                        .sortedByDescending { it.value }
                        .toList()

            // take the top two weaknesses
            val results = weaknesses.take(count).toMutableList()

            // add all extra weaknesses equivalent to the last taken weakness as well
            if (weaknesses.size > count) {
                results += weaknesses.drop(count).takeWhile { it.value == results.last().value }
            }

            return results
        }

        val topElement = calculateTopWeaknesses(it.elementWeaknesses, 2)
        val topStatus = calculateTopWeaknesses(it.statusWeaknesses, 1)
        return MonsterWeaknessResult(
                state = it.state ?: "",
                element = topElement,
                status = topStatus,
                items = it.vulnerableTraps + it.vulnerableBombs)
    }
}