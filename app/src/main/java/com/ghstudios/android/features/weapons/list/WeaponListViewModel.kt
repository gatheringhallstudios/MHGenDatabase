package com.ghstudios.android.features.weapons.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ghstudios.android.components.WeaponListEntry
import com.ghstudios.android.data.DataManager

/**
 * An immutable class used to represent the WeaponListViewModel's state.
 * Kotlin classes implement equals()
 */
private data class ViewModelState(val weaponType : String?, val filterFinal: Boolean)

/**
 * A viewmodel that stores state data for the WeaponListFragment
 */
class WeaponListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    /**
     * Private variable that stores the internal state of the view model.
     * If a new state is not equal to this, data needs to be reloaded.
     */
    private var currentState = ViewModelState(null, false)

    /**
     * Returns the currently selected weapon type
     */
    val weaponType get() = currentState.weaponType

    /**
     * LiveData that contains the list of weapon entries.
     * Updates when the list of weapon entries changes
     */
    val weaponListData = MutableLiveData<List<WeaponListEntry>>()

    /**
     * Collapsed UI groups. Used to restore collapsed weapons on configuration change
     */
    var collapsedGroups : List<Int>? = null

    /**
     * Sets whether the weapon list should only contain final upgrades or not.
     * The livedata is immediately updated when changing this value
     */
    var filterFinal: Boolean
            get() = currentState.filterFinal
            set(value) {
                setState(currentState.copy(filterFinal=value))
            }

    /**
     * Sets the weapon type and begins loading.
     * Once weapon type is set, update the filterFinal property to update the list.
     */
    fun loadWeaponType(weaponType : String) {
        val newState = currentState.copy(weaponType=weaponType)
        setState(newState)
    }

    private fun setState(newState : ViewModelState) {
        if (this.currentState == newState) {
            return // nothing changed
        }

        this.currentState = newState

        val weaponType = newState.weaponType
        if (weaponType == null || weaponType == "") {
            return // nothing to load
        }

        Thread {
            weaponListData.postValue(when (newState.filterFinal) {
                true -> dataManager.queryWeaponTreeArrayFinal(weaponType)
                false -> dataManager.queryWeaponTreeArray(weaponType)
            })
        }.start()
    }
}