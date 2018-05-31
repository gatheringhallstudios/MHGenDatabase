package com.ghstudios.android.features.decorations

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Decoration
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

class DecorationViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)
    val decorationData = MutableLiveData<Decoration>()
    val componentData = MutableLiveData<Map<String, List<Component>>>()

    private var decorationId : Long = -1

    fun setDecoration(decorationId : Long) {
        if (this.decorationId == decorationId) {
            return
        }

        this.decorationId = decorationId

        Thread {
            val decoration = dataManager.getDecoration(decorationId)
            val components = dataManager.queryComponentCreated(decorationId).toList {
                it.component
            }.groupBy { it.type }

            decorationData.postValue(decoration)
            componentData.postValue(components)
        }.start()
    }
}
