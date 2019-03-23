package com.ghstudios.android.features.armorsetbuilder.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

/** ViewModel used to display ASB list items **/
class ASBSetListViewModel: ViewModel() {
    val asbManager = DataManager.get().asbManager
    val asbData = MutableLiveData<List<ASBSet>>()

    init {
        reload()
    }

    /**
     * Reloads all data, populating the livedata.
     */
    fun reload() {
        loggedThread("Load ASB Sets") {
            asbData.postValue(asbManager.queryASBSets().toList { it.asbSet })
        }
    }

    /**
     * Adds a new ASBSet based off the provided data.
     */
    fun addSet(name: String, rank: Rank, hunterType: Int) {
        asbManager.queryAddASBSet(name, rank, hunterType)
        reload()
    }

    /**
     * Updates the values of an ASBSet with a certain id, then reloads the data.
     */
    fun updateASBSet(id: Long, name: String, rank: Rank, hunterType: Int) {
        asbManager.queryUpdateASBSet(id, name, rank, hunterType)
        reload()
    }

    fun deleteSet(id: Long) {
        asbManager.queryDeleteASBSet(id)
        reload()
    }
}