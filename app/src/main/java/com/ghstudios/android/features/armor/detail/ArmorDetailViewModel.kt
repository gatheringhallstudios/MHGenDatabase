package com.ghstudios.android.features.armor.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

/**
 * A ViewModel representing information for a single piece of armor.
 */
class ArmorDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    val armorData = MutableLiveData<Armor>()
    val skillData = MutableLiveData<List<ItemToSkillTree>>()
    val componentData = MutableLiveData<List<Component>>()

    var armorId = -1L
        private set

    /**
     * Sets the armor and begins loading armor data if not already loaded
     */
    fun loadArmor(armorId: Long) {
        if (this.armorId == armorId) {
            return
        }

        this.armorId = armorId

        loggedThread(name = "Armor Loading") {
            armorData.postValue(dataManager.getArmor(armorId))

            skillData.postValue(dataManager.queryItemToSkillTreeArrayItem(armorId))

            val components = dataManager.queryComponentCreated(armorId).toList {
                it.component
            }
            componentData.postValue(components)
        }
    }
}