package com.ghstudios.android.data.classes

/**
 * Defines an investment of skill points into a skill tree.
 */
open class SkillTreePoints(
        var skillTree: SkillTree,

       /**
        * Number of points in the skill tree
        */
       var points: Int
) {
    constructor(skillTree: SkillTree) : this(skillTree, -1)
}