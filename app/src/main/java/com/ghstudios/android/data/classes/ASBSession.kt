package com.ghstudios.android.data.classes

import android.util.Log
import com.ghstudios.android.data.DataManager

import java.util.ArrayList
import java.util.HashMap

/**
 * Contains all of the juicy stuff regarding ASB sets, like the armor inside and the skills it provides.
 */
class ASBSession: ArmorSet {
    private var asbSet: ASBSet? = null

    private val equipment: Array<Equipment?> = arrayOfNulls(6)
    private val decorations: Array<MutableList<Decoration>> = Array(6) { mutableListOf<Decoration>() }

    val id: Long
        get() = asbSet!!.id

    val rank: Int
        get() = asbSet!!.rank

    val hunterType: Int
        get() = asbSet!!.hunterType

    override val pieces: List<ArmorSetPiece> get() {
        // todo: swap the ASB internal implementation with ArmorSetPiece instead of reconstructing
        val results = mutableListOf<ArmorSetPiece>()
        for (i in equipment.indices) {
            val equipment = getEquipment(i)
            if (equipment != null) {
                val piece = ArmorSetPiece(i, equipment)
                piece.decorations.addAll(getDecorations(i))
                results.add(piece)
            }
        }
        return results
    }

    /**
     * @return The set's talisman.
     */
    val talisman: ASBTalisman?
        get() = equipment[ArmorSet.TALISMAN] as ASBTalisman?

    fun setASBSet(set: ASBSet) {
        asbSet = set
    }

    fun getDecoration(pieceIndex: Int, decorationIndex: Int): Decoration? {
        return decorations[pieceIndex].getOrNull(decorationIndex)
    }

    fun getDecorations(pieceIndex: Int): List<Decoration> {
        return decorations[pieceIndex]
    }

    fun getAvailableSlots(pieceIndex: Int): Int {
        val equipment = getEquipment(pieceIndex) ?: return 0
        val usedSlots = getDecorations(pieceIndex).sumBy { it.numSlots }
        return equipment.numSlots - usedSlots
    }

    /**
     * Attempts to add a decoration to the specified armor piece.
     * @param pieceIndex   The index of a piece in the set to fetch, according to [ASBSession].
     * @param decoration   The decoration to add.
     * @return The 0-based index of the slot that the decoration was added to.
     */
    fun addDecoration(pieceIndex: Int, decoration: Decoration): Int {
        Log.v("ASB", "Adding decoration at piece index $pieceIndex")
        if (getAvailableSlots(pieceIndex) >= decoration.numSlots) {
            decorations[pieceIndex].add(decoration)
            return decorations[pieceIndex].size - 1
        } else {
            Log.e("ASB", "Cannot add that decoration!")
            return -1
        }
    }

    /**
     * Removes the decoration at the specified location from the specified armor piece.
     * Will fail if the decoration in question is non-existent or a dummy.
     */
    fun removeDecoration(pieceIndex: Int, decorationIndex: Int) {
        val list = decorations[pieceIndex]
        if (list.getOrNull(decorationIndex) == null) {
            return
        }

        list.removeAt(decorationIndex)
    }

    /**
     * @return A piece of the armor set based on the provided piece index.
     * Returns null if there is no equipment in that slot.
     */
    fun getEquipment(pieceIndex: Int): Equipment? {
        return equipment[pieceIndex]
    }

    /**
     * Changes the equipment at the specified location.
     */
    fun setEquipment(pieceIndex: Int, equip: Equipment) {
        equipment[pieceIndex] = equip
    }

    /**
     * Removes the equipment at the specified location.
     */
    fun removeEquipment(pieceIndex: Int) {
        equipment[pieceIndex] = null
        decorations[pieceIndex].clear()
    }
}
