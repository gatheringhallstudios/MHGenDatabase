package com.ghstudios.android.features.armor

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.database.DataManager

class ArmorViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val armorData = MutableLiveData<Armor>()

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
        }.start()
    }
}