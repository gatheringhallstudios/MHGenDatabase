package com.ghstudios.android.features.armorsetbuilder.talismans

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ASBTalisman

class ASBTalismanListViewModel: ViewModel() {
    val dataManager = DataManager.get()
    val asbManager = dataManager.asbManager

    /** Returns talisman data. */
    val talismanData = MutableLiveData<List<ASBTalisman>>()

    private var previousTalismans: List<ASBTalisman>? = null

    fun reload() {
        talismanData.value = asbManager.getTalismans()
    }

    fun saveTalisman(data: ASBTalisman) {
        talismanData.value = asbManager.saveTalisman(data)
    }

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
     * Removes the talisman, and saves the state before to facilitate undo.
     */
    fun removeTalisman(id: Long) {
        val talismans = asbManager.getTalismans()
        previousTalismans = talismans

        val newTalismans = talismans.filter { it.id != id }
        asbManager.saveTalismans(newTalismans)
        talismanData.value = newTalismans
    }

    fun removeTalismanByIndex(idx: Int) {
        val talisman = talismanData.value?.get(idx)
        talisman?.let { removeTalisman(talisman.id) }
    }

    fun moveTalismanByIndex(idxStart: Int, idxEnd: Int) {
        if (idxStart == idxEnd) {
            return
        }

        // Save previous talismans and get current set. toMutableList() always makes a copy.
        previousTalismans = talismanData.value
        val talismans = previousTalismans?.toMutableList()
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

    /**
     * Restores the previous talisman state. Use after remove.
     * Doesn't check for staleness, so its only good for snackbar undos.
     */
    fun restorePrevious() {
        previousTalismans?.let { previousTalismans ->
            asbManager.saveTalismans(previousTalismans)
            talismanData.value = previousTalismans
        }
        previousTalismans = null
    }
}