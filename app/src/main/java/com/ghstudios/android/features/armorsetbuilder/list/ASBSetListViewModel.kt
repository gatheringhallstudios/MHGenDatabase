package com.ghstudios.android.features.armorsetbuilder.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.util.UndoableOperation
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

/** ViewModel used to display ASB list items **/
class ASBSetListViewModel: ViewModel() {
    val asbManager = DataManager.get().asbManager

    private var previousDelete: UndoableOperation? = null
    val asbData = MutableLiveData<List<ASBSet>>()

    init {
        reload()
    }

    /**
     * Reloads all data, populating the livedata.
     */
    fun reload() {
        previousDelete?.complete()
        asbData.value = asbManager.queryASBSets().toList { it.asbSet }
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

    /**
     * Starts the process of deleting an ASB set.
     * Execute complete() on the returned operation to finish deleting and commit to the DB,
     * or execute undo in order to revert the list back.
     */
    fun startDeleteSet(id: Long): UndoableOperation {
        previousDelete?.complete()

        val previousSets = asbData.value ?: emptyList()
        asbData.value = previousSets.filter { it.id != id }

        val operation = UndoableOperation(
                onComplete = { deleteSet(id) },
                onUndo = { asbData.value = previousSets }
        )
        previousDelete = operation
        return operation
    }

    /**
     * Deletes the set from the DB and reloads the list.
     */
    fun deleteSet(id: Long) {
        asbManager.queryDeleteASBSet(id)
        reload()
    }
}