package com.ghstudios.android.features.armor

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

class ArmorViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val armorData = MutableLiveData<Armor>()
    val skillData = MutableLiveData<List<ItemToSkillTree>>()
    val componentData = MutableLiveData<List<Component>>()

    var armorId = -1L
        private set

    /**
     * Sets the armor and begins loading armor data if not already loaded
     */
    fun loadArmor(armorId : Long) {
        if (this.armorId == armorId) {
            return
        }

        this.armorId = armorId

        Thread {
            armorData.postValue(dataManager.getArmor(armorId))

            skillData.postValue(dataManager.queryItemToSkillTreeArrayItem(armorId))

            val components = dataManager.queryComponentCreated(armorId).toList {
                it.component
            }
            componentData.postValue(components)
        }.start()
    }
}