package com.ghstudios.android.features.palicos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.PalicoArmor
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

class PalicoArmorListViewModel(app : Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()
    val armors = MutableLiveData<List<PalicoArmor>>()

    fun loadList() {
        if (armors.value != null) return
        loggedThread("PalicoArmorList Load") {
            armors.postValue(dataManager.queryPalicoArmor().toList { it.armor })
        }
    }
}