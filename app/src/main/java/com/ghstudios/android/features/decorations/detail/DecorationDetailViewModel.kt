package com.ghstudios.android.features.decorations.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Decoration
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.loggedThread
import com.ghstudios.android.toList

/**
 * Internal view data class to encapsulate skill information.
 */
data class SkillPoints(val skillId: Long, val skillName: String, val points: Int)

class DecorationDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)
    val decorationData = MutableLiveData<Decoration>()
    val componentData = MutableLiveData<Map<String, List<Component>>>()

    val decorationSkillData = Transformations.map(decorationData) { decoration ->
        val results = ArrayList<SkillPoints>()
        if (decoration.skill1Id > 0) {
            results.add(SkillPoints(
                    decoration.skill1Id, decoration.skill1Name, decoration.skill1Point))
        }
        if (decoration.skill2Id > 0) {
            results.add(SkillPoints(
                    decoration.skill2Id, decoration.skill2Name, decoration.skill2Point))
        }
        results
    }

    private var decorationId : Long = -1

    fun setDecoration(decorationId : Long) {
        if (this.decorationId == decorationId) {
            return
        }

        this.decorationId = decorationId

        loggedThread(name="Decoration Loading") {
            val decoration = dataManager.getDecoration(decorationId)
            val components = dataManager.queryComponentCreated(decorationId).toList {
                it.component
            }.groupBy { it.type }

            decorationData.postValue(decoration)
            componentData.postValue(components)
        }
    }
}
