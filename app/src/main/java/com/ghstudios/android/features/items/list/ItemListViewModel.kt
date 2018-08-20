package com.ghstudios.android.features.items.list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.ghstudios.android.MHUtils
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

class ItemListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val itemData = MHUtils.createLiveData {
        dataManager.queryBasicItems().toList { it.item }
    }
}