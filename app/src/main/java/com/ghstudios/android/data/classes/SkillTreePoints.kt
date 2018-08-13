package com.ghstudios.android.data.classes

/**
 * Defines an investment of skill points into a skill tree.
 */
open class SkillTreePoints {
    var skillTree: SkillTree? = null

    /**
     * Number of points in the skill tree
     */
    var points: Int = -1
}