package com.ghstudios.android.features.weapons.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList



data class WeaponFamilyWrapper(
    val group:String?,
    val weapon:Weapon,
    val showLevel:Boolean
)

/**
 * ViewModel that manages data for the weapon detail
 */
class WeaponDetailViewModel(private val app: Application) : AndroidViewModel(app) {
    val dataManager = DataManager.get()

    val weaponData = MutableLiveData<Weapon>()

    val createComponentData = MutableLiveData<List<Component>>()
    val improveComponentData = MutableLiveData<List<Component>>()
    val familyTreeData = MutableLiveData<List<WeaponFamilyWrapper>>()

    var weaponId = -1L
        private set

    fun loadWeapon(weaponId: Long): Weapon? {
        if (this.weaponId == weaponId) {
            return weaponData.value
        }

        this.weaponId = weaponId

        val weapon = dataManager.getWeapon(weaponId)
        weaponData.postValue(weapon)

        loggedThread("Weapon Detail Loading") {
            val components = dataManager.queryComponentCreated(weaponId).toList {
                it.component
            }
            createComponentData.postValue(components.filter { it.type == Component.TYPE_CREATE })
            improveComponentData.postValue(components.filter { it.type == Component.TYPE_IMPROVE })
        }

        loggedThread("Weapon Family Loading"){
            val famData = ArrayList<WeaponFamilyWrapper>()

            // origin trees
            val originTitle = app.getString(R.string.weapon_tree_origin)
            val origins = dataManager.queryWeaponOrigins(weaponId).reversed()
            for (w in origins) {
                famData.add(WeaponFamilyWrapper(originTitle, w, false))
            }

            // current family tree
            val familyTitle = app.getString(R.string.weapon_tree_family)
            val family = dataManager.queryWeaponTree(weaponId).toList { it.weapon }
            for (w in family) {
                famData.add(WeaponFamilyWrapper(familyTitle, w, false))
            }

            // alt branches
            val branchesTitle = app.getString(R.string.weapon_tree_side_branches)
            val branches = dataManager.queryWeaponBranches(weaponId)
            for (w in branches) {
                famData.add(WeaponFamilyWrapper(branchesTitle, w, true))
            }

            // final upgrades
            val finalTitle = app.getString(R.string.weapon_tree_final)
            val finalWeapons = dataManager.queryWeaponFinal(weaponId)
            for (w in finalWeapons) {
                famData.add(WeaponFamilyWrapper(finalTitle, w, false))
            }

            familyTreeData.postValue(famData)
        }

        return weapon
    }
}