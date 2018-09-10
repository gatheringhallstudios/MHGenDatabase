package com.ghstudios.android.data.classes

/**
 * Class for ItemToSkillTree. Represents an item associated with a skill tree and a points value
 */
class ItemToSkillTree(skillTree: SkillTree): SkillTreePoints(skillTree) {

    /**
     * Id of the ItemToSkillTree entry. This is NOT the skilltree id
     */
    var id: Long = -1

    var item: Item? = null                // Item
}
