package com.ghstudios.android.features.monsters.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterClass
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

/**
 * A viewmodel meant to be used by the MonsterListFragment
 */
class MonsterListViewModel(app: Application): AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    private var initialized = false
    private var currentClass: MonsterClass? = null
    val monsterData = MutableLiveData<List<Monster>>()

    fun loadMonsters(monsterClass: MonsterClass?) {
        if (initialized && currentClass == monsterClass) {
            return
        }

        initialized = true
        currentClass = monsterClass
        loggedThread("Monster List Load") {
            monsterData.postValue(dataManager.queryMonsters(monsterClass).toList { it.monster })
        }
    }
}