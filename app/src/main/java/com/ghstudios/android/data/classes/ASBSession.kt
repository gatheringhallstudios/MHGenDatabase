package com.ghstudios.android.data.classes

import android.util.Log
import com.ghstudios.android.data.database.DataManager

import java.util.ArrayList
import java.util.HashMap


// todo: move this to some sort of data constants location
private const val TORSO_UP_ID = 203 // Skilltree ID for the TorsoUp skill
private const val SECRET_ARTS_ID = 204 // Skiltree ID. Needs 10 points in the skill for +2 to all other skills
private const val TALISMAN_BOOST_ID = 205 // Skilltree Id. Needs 10 points in the skill for x2 talisman skills

/**
 * Contains all of the juicy stuff regarding ASB sets, like the armor inside and the skills it provides.
 */
class ASBSession {
    companion object {
        const val HEAD = 0
        const val BODY = 1
        const val ARMS = 2
        const val WAIST = 3
        const val LEGS = 4
        const val TALISMAN = 5
    }

    private var asbSet: ASBSet? = null

    private val equipment: Array<Equipment?> = arrayOfNulls(6)
    private val decorations: Array<MutableList<Decoration>> = Array(6) { mutableListOf<Decoration>() }

    val skillTreesInSet: MutableList<SkillTreeInSet>

    private var torsoUpCount: Int = 0

    val id: Long
        get() = asbSet!!.id

    val rank: Int
        get() = asbSet!!.rank

    val hunterType: Int
        get() = asbSet!!.hunterType

    /**
     * @return The set's talisman.
     */
    val talisman: ASBTalisman?
        get() = equipment[TALISMAN] as ASBTalisman?

    init {
        skillTreesInSet = ArrayList()
    }

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
     * @param updateSkills Whether or not to call [.updateSkillTreePointsSets] upon completion.
     * @return The 0-based index of the slot that the decoration was added to.
     */
    @JvmOverloads
    fun addDecoration(pieceIndex: Int, decoration: Decoration, updateSkills: Boolean = true): Int {
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
     * @param updateSkills Whether or not to call [.updateSkillTreePointsSets] upon completion.
     */
    @JvmOverloads
    fun removeDecoration(pieceIndex: Int, decorationIndex: Int, updateSkills: Boolean = true) {
        val list = decorations[pieceIndex]
        if (list.getOrNull(decorationIndex) == null) {
            return
        }

        list.removeAt(decorationIndex)

        if (updateSkills) {
            updateSkillTreePointsSets()
        }
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
     * @param updateSkills Whether or not to call [.updateSkillTreePointsSets] upon completion.
     */
    @JvmOverloads
    fun setEquipment(pieceIndex: Int, equip: Equipment, updateSkills: Boolean = true) {
        equipment[pieceIndex] = equip

        if (updateSkills) {
            updateSkillTreePointsSets()
        }
    }

    /**
     * Removes the equipment at the specified location.
     * @param updateSkills Whether or not to call [.updateSkillTreePointsSets] upon completion.
     */
    @JvmOverloads
    fun removeEquipment(pieceIndex: Int, updateSkills: Boolean = true) {
        if (pieceIndex == TALISMAN) {
            equipment[pieceIndex] = null
        } else {
            equipment[pieceIndex] = null
        }

        decorations[pieceIndex].clear()

        if (updateSkills) {
            updateSkillTreePointsSets()
        }
    }

    /**
     * Adds any skills to the armor set's skill trees that were not there before, and removes those no longer there.
     * Adding decorations and armor does not update skilltrees unless this method is called
     */
    fun updateSkillTreePointsSets() {
        skillTreesInSet.clear()

        val skillTreeToSkillTreeInSet = HashMap<Long, SkillTreeInSet>() // A map of the skill trees in the set and their associated SkillTreePointsSets

        for (pointsSet in skillTreesInSet) {
            skillTreeToSkillTreeInSet[pointsSet.skillTree!!.id] = pointsSet
        }

        torsoUpCount = 0 // We're going to recount this every time.

        for (i in equipment.indices) {

            Log.v("ASB", "Reading skills from armor piece $i")

            val armorSkillTreePoints = getSkillsFromArmorPiece(i) // A map of the current piece of armor's skills, localized so we don't have to keep calling it

            for (skillTreePoints in armorSkillTreePoints) {
                val skillTree = skillTreePoints.skillTree
                val points = skillTreePoints.points

                // Count TorsoUp occurrences
                if (skillTree.id == TORSO_UP_ID.toLong()) {
                    torsoUpCount++
                }

                val s: SkillTreeInSet // The actual points set that we are working with that will be shown to the user

                if (!skillTreeToSkillTreeInSet.containsKey(skillTree.id)) { // If the armor set does not yet have this skill tree registered...
                    Log.v("ASB",
                            "Adding skill tree " + skillTree.name + " to the list of Skill Trees in the armor set.")

                    s = SkillTreeInSet() // We add it...
                    s.skillTree = skillTree
                    skillTreesInSet.add(s)

                    skillTreeToSkillTreeInSet[skillTree.id] = s

                } else {
                    Log.v("ASB", "Skill tree " + skillTree.name + " already registered!")
                    s = skillTreeToSkillTreeInSet[skillTree.id]!! // Otherwise, we just find the skill tree set that's already there
                }

                s.setPoints(i, points)
            }
        }
    }

    /**
     * A helper method that converts an armor piece present in the current session into a map of the skills it provides and the respective points in each.
     * @param pieceIndex The piece of armor to get the skills from.
     * @return A map of all the skills the armor piece provides along with the number of points in each.
     */
    private fun getSkillsFromArmorPiece(pieceIndex: Int): List<SkillTreePoints> {
        // todo: Don't query here you FOOL this is a data object
        val dataManager = DataManager.get()

        val equipment = equipment[pieceIndex] ?: return emptyList()

        // mapping of skilltree id to skilltree points. The values are returned in the end
        val skillCache = HashMap<Long, SkillTreePoints>()

        if (pieceIndex != TALISMAN) {
            val equipmentSkills = dataManager.queryItemToSkillTreeArrayItem(equipment.id)
            for (itemToSkillTree in equipmentSkills) { // We add skills for armor
                skillCache[itemToSkillTree.skillTree.id] = itemToSkillTree
            }
        } else {
            val talisman = talisman
            talisman?.firstSkill?.let {
                skillCache[it.skillTree.id] = it
            }
            talisman?.secondSkill?.let {
                skillCache[it.skillTree.id] = it
            }
        }

        for (d in decorations[pieceIndex]) {
            val decorationSkills = dataManager.queryItemToSkillTreeArrayItem(d.id)
            for (itemToSkillTree in decorationSkills) {
                val skillTree = itemToSkillTree.skillTree
                val points = itemToSkillTree.points

                if (skillTree.id in skillCache) {
                    skillCache[skillTree.id]!!.points += points
                } else {
                    skillCache[skillTree.id] = SkillTreePoints(skillTree, points)
                }
            }
        }

        return skillCache.values.toList()
    }

    /**
     * A container class that represents a skill tree as well as a specific number of points provided by each armor piece in a set.
     */
    inner class SkillTreeInSet {

        var skillTree: SkillTree? = null
        private val points: IntArray

        init {
            points = IntArray(6)
        }

        fun getPoints(pieceIndex: Int): Int {
            if (pieceIndex == BODY) {
                throw IllegalArgumentException("Use the getPoints(int, List<SkillTreeInSet>) when dealing with the chest piece!")
            }
            return getPoints(pieceIndex, null)
        }

        fun getPoints(pieceIndex: Int, trees: List<SkillTreeInSet>?): Int {
            return if (pieceIndex == BODY) {
                // TorsoUp stacks, so you multiply the skill * number of occurrences
                points[pieceIndex] * (torsoUpCount + 1)
            } else {
                points[pieceIndex]
            }
        }

        /**
         * @return The total number of skill points provided to the skill by all pieces in the set.
         */
        fun getTotal(trees: List<SkillTreeInSet>): Int {
            var total = 0
            for (i in points.indices) {
                total += getPoints(i, trees)
            }
            return total
        }

        fun setPoints(pieceIndex: Int, piecePoints: Int) {
            points[pieceIndex] = piecePoints
        }
    }
}