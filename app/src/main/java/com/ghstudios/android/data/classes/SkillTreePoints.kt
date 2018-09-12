package com.ghstudios.android.data.classes

/**
 * Defines an investment of skill points into a skill tree.
 */
open class SkillTreePoints(
        val skillTree: SkillTree,

        /**
         * Number of points in the skill tree
         */
        val points: Int
)