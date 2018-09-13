package com.ghstudios.android.features.weapons.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

data class WeaponElementData(
        val element: String?,
        val value: Long
)

data class WeaponFamilyWrapper(
    val group:String?,
    val weapon:Weapon,
    val showLevel:Boolean
)

class WeaponDetailViewModel(app: Application) : AndroidViewModel(app) {
    val dataManager = DataManager.get()

    val weaponData = MutableLiveData<Weapon>()

    val createComponentData = MutableLiveData<List<Component>>()
    val improveComponentData = MutableLiveData<List<Component>>()
    val familyTreeData = MutableLiveData<List<WeaponFamilyWrapper>>()

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

        loggedThread("Weapon Detail Loading") {
            weaponData.postValue(dataManager.getWeapon(weaponId))
            val components = dataManager.queryComponentCreated(weaponId).toList {
                it.component
            }
            createComponentData.postValue(components.filter { it.type == Component.TYPE_CREATE })
            improveComponentData.postValue(components.filter { it.type == Component.TYPE_IMPROVE })
        }

        loggedThread("Weapon Family Loading"){
            val famData = ArrayList<WeaponFamilyWrapper>()
            val origins = dataManager.queryWeaponOrigins(weaponId).reversed()
            for (w in origins) famData.add(WeaponFamilyWrapper("Origin",w,false))
            val family = dataManager.queryWeaponTree(weaponId).toList { it.weapon }
            for (w in family) famData.add(WeaponFamilyWrapper("Family",w,false))
            val branches = dataManager.queryWeaponBranches(weaponId)
            for (w in branches) famData.add(WeaponFamilyWrapper("Branches",w,true))
            familyTreeData.postValue(famData)
        }
    }
}