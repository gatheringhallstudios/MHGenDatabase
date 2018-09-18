package com.ghstudios.android.features.armorsetbuilder

import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.*
import java.util.ArrayList
import java.util.HashMap

// todo: move this to some sort of data constants location
private const val TORSO_UP_ID = 203 // Skilltree ID for the TorsoUp skill
private const val SECRET_ARTS_ID = 204 // Skiltree ID. Needs 10 points in the skill for +2 to all other skills
private const val TALISMAN_BOOST_ID = 205 // Skilltree Id. Needs 10 points in the skill for x2 talisman skills


class ArmorSetCalculator(val set: ArmorSet) {
    val skillTreesInSet: MutableList<SkillTreeInSet> = ArrayList()

    private var torsoUpCount: Int = 0

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

        for (piece in set.pieces) {
            val idx = piece.idx
            Log.v("ASB", "Reading skills from armor piece $idx")

            val armorSkillTreePoints = getSkillsFromArmorPiece(piece) // A map of the current piece of armor's skills, localized so we don't have to keep calling it

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

                s.setPoints(idx, points)
            }
        }
    }

    /**
     * A helper method that converts an armor piece present in the current set into a map of the skills it provides and the respective points in each.
     * @param pieceIndex The piece of armor to get the skills from.
     * @return A map of all the skills the armor piece provides along with the number of points in each.
     */
    private fun getSkillsFromArmorPiece(armorSetPiece: ArmorSetPiece): List<SkillTreePoints> {
        // todo: Don't query here you FOOL this is a data object
        val dataManager = DataManager.get()

        val equipment = armorSetPiece.equipment
        val decorations = armorSetPiece.decorations

        // mapping of skilltree id to skilltree points. The values are returned in the end
        val skillCache = HashMap<Long, SkillTreePoints>()

        // Get points from the equipment/talisman itself first
        if (equipment is ASBTalisman) {
            equipment.firstSkill?.let {
                skillCache[it.skillTree.id] = it
            }
            equipment.secondSkill?.let {
                skillCache[it.skillTree.id] = it
            }
        } else {
            val equipmentSkills = dataManager.queryItemToSkillTreeArrayItem(equipment.id)
            for (itemToSkillTree in equipmentSkills) { // We add skills for armor
                skillCache[itemToSkillTree.skillTree.id] = itemToSkillTree
            }
        }

        // Add decorations
        for (d in decorations) {
            val decorationSkills = dataManager.queryItemToSkillTreeArrayItem(d.id)
            for (itemToSkillTree in decorationSkills) {
                val skillTree = itemToSkillTree.skillTree
                val points = itemToSkillTree.points

                // SkillPoints are immmutable, so if we're adding, create a new entry
                val totalPoints = points + (skillCache[skillTree.id]?.points ?: 0)
                skillCache[skillTree.id] = SkillTreePoints(skillTree, totalPoints)
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
            if (pieceIndex == ASBSession.BODY) {
                throw IllegalArgumentException("Use the getPoints(int, List<SkillTreeInSet>) when dealing with the chest piece!")
            }
            return getPoints(pieceIndex, null)
        }

        fun getPoints(pieceIndex: Int, trees: List<SkillTreeInSet>?): Int {
            return if (pieceIndex == ASBSession.BODY) {
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