package com.ghstudios.android.features.armorsetbuilder.talismans

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.util.UndoableOperation

/**
 * ViewModel used to contain and manage a bank of talismans.
 * Loads all data when initialized.
 */
class ASBTalismanListViewModel: ViewModel() {
    val TAG = javaClass.name

    val dataManager = DataManager.get()
    val asbManager = dataManager.asbManager

    private var previousDelete: UndoableOperation? = null

    /** Returns talisman data. Updates with a new list on every change. */
    val talismanData = MutableLiveData<List<ASBTalisman>>()

    init {
        reload()
    }

    /**
     * Completes any pending deletes and reloads the talisman list.
     */
    fun reload() {
        previousDelete?.complete()
        talismanData.value = asbManager.getTalismans()
    }

    /**
     * Adds a talisman to the list, and updates the talisman list.
     */
    fun saveTalisman(data: ASBTalisman) {
        talismanData.value = asbManager.saveTalisman(data)
    }

    /**
     * Adds a talisman to the list, and updates the talisman list.
     * A talisman is constructed from the TalismanMetadata.
     */
    fun saveTalisman(data: TalismanMetadata) {
        val talisman = ASBTalisman(data.typeIndex)
        talisman.id = data.id
        talisman.numSlots = data.numSlots
        for ((skillId, points) in data.skills) {
            if (skillId == -1L) {
                continue
            }

            val skillTree = dataManager.getSkillTree(skillId)
            if (skillTree != null) {
                talisman.addSkill(skillTree, points)
            }
        }

        saveTalisman(talisman)
    }

    /**
     * Starts the process of deleting a talisman by removing the talisman locally from the viewmodel,
     * and then returns a method to either complete the delete, or undo it.
     * Calling this method with an ongoing operation will complete the previous one.
     */
    fun startRemoveTalisman(id: Long): UndoableOperation {
        previousDelete?.complete()

        val previousTalismans = talismanData.value ?: emptyList()
        talismanData.value = previousTalismans.filter {
            it.id != id
        }

        val operation = UndoableOperation(
                onComplete = {
                    removeTalisman(id)
                },
                onUndo = {
                    talismanData.value = previousTalismans
                }
        )
        previousDelete = operation
        return operation
    }

    /**
     * Removes the talisman, and saves the state before to facilitate undo.
     */
    fun removeTalisman(id: Long) {
        val talismans = asbManager.getTalismans()
        val newTalismans = talismans.filter { it.id != id }
        asbManager.saveTalismans(newTalismans)
        talismanData.value = newTalismans

        Log.d(TAG, "Talisman deleted")
    }

    fun moveTalismanByIndex(idxStart: Int, idxEnd: Int) {
        if (idxStart == idxEnd) {
            return
        }

        // toMutableList() always makes a copy.
        val talismans = talismanData.value?.toMutableList()
        if (talismans == null) {
            Log.e(javaClass.name, "Talisman data is null")
            return
        }

        // Remove talisman and re-insert.
        // Handle the scenario where we are insert after the list shifts
        val talisman = talismans.removeAt(idxStart)
        val destination = when (idxEnd > idxStart) {
            true -> idxEnd - 1
            false -> idxEnd
        }
        talismans.add(destination, talisman)

        asbManager.saveTalismans(talismans)
    }
}