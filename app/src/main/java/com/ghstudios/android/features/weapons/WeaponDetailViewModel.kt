package com.ghstudios.android.features.weapons

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.data.database.DataManager

class WeaponDetailViewModel(app: Application) : AndroidViewModel(app) {
    val dataManager = DataManager.get(app.applicationContext)

    val weaponData = MutableLiveData<Weapon>()

    var weaponId = -1L
        private set

    fun loadWeapon(weaponId: Long) {
        if (this.weaponId == weaponId) {
            return
        }

        this.weaponId = weaponId

        Thread {
            weaponData.postValue(dataManager.getWeapon(weaponId))
        }.start()
    }
}