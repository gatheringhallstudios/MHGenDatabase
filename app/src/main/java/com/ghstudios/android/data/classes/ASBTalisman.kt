package com.ghstudios.android.data.classes

import com.ghstudios.android.mhgendatabase.R

/**
 * Represents an ArmorSetBuilder Talisman as Equipment.
 */
class ASBTalisman(var typeIndex: Int = 0) : Equipment() {
    private val skillPoints = arrayOfNulls<SkillTreePoints>(2)

    val firstSkill get() = skillPoints[0]
    val secondSkill get() = skillPoints[1]

    /**
     * Returns a list of all skill points in this talisman
     */
    val skills get() = skillPoints.filterNotNull()

    fun setFirstSkill(skillTree: SkillTree, points: Int) {
        skillPoints[0] = SkillTreePoints(skillTree, points)
    }

    fun setSecondSkill(skillTree: SkillTree?, points: Int) {
        if (skillTree == null) {
            skillPoints[1] = null
        } else {
            skillPoints[1] = SkillTreePoints(skillTree, points)
        }
    }

    /**
     * Adds a skill. If talisman is empty, sets to first slot,
     * otherwise always overwrites second slot.
     */
    fun addSkill(skillTree: SkillTree, points: Int) {
        if (skillPoints[0] == null) {
            setFirstSkill(skillTree, points)
        } else {
            setSecondSkill(skillTree, points)
        }
    }

    override fun getIconResourceString() = "talisman"
    override fun getColorArrayId() = R.array.talisman_colors
    override fun getIconColorIndex() = typeIndex
}
