package com.ghstudios.android.features.weapons.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.util.toList

data class WeaponElementData(
        val element: String?,
        val value: Long
)

class WeaponDetailViewModel(app: Application) : AndroidViewModel(app) {
    val dataManager = DataManager.get(app.applicationContext)

    val weaponData = MutableLiveData<Weapon>()
    val componentData = MutableLiveData<List<Component>>();

    /**
     * Live data that returns weapon element or status data once a weapon is loaded.
     * Null is returned if its not a weapon that can have element data.
     */
    val weaponElementData: LiveData<List<WeaponElementData>> = Transformations.map(weaponData) {
        when (it.wtype) {
            Weapon.HEAVY_BOWGUN, Weapon.LIGHT_BOWGUN -> null

            else -> {
                if (it.element == "") {
                    return@map arrayListOf(WeaponElementData("None", 0))
                }

                val elements = ArrayList<WeaponElementData>()
                elements.add(WeaponElementData(it.element, it.elementAttack))

                if (it.element2 != "") {
                    elements.add(WeaponElementData(it.element2, it.element2Attack))
                }

                return@map elements
            }
        }
    }

    var weaponId = -1L
        private set

    fun loadWeapon(weaponId: Long) {
        if (this.weaponId == weaponId) {
            return
        }

        this.weaponId = weaponId

        Thread {
            weaponData.postValue(dataManager.getWeapon(weaponId))
            val components = dataManager.queryComponentCreated(weaponId).toList {
                it.component
            }
            componentData.postValue(components)
        }.start()
    }
}