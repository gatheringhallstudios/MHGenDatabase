package com.ghstudios.android.features.combining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.toList

class CombiningListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()
    val combinationData = MHUtils.createLiveData {
        dataManager.queryCombinings().toList { it.combining }
    }
}