package com.ghstudios.android.features.combining

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.ghstudios.android.MHUtils
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

class CombiningListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)
    val combinationData = MHUtils.createLiveData {
        dataManager.queryCombinings().toList { it.combining }
    }
}