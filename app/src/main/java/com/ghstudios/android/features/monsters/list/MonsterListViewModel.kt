package com.ghstudios.android.features.monsters.list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterClass
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.loggedThread
import com.ghstudios.android.toList

/**
 * A viewmodel meant to be used by the MonsterListFragment
 */
class MonsterListViewModel(app: Application): AndroidViewModel(app) {
    private val dataManager = DataManager.get(app)

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