package com.ghstudios.android.data.classes

/**
 * Class for ItemToSkillTree. Represents an item associated with a skill tree and a points value
 */
class ItemToSkillTree: SkillTreePoints() {

    /* Getters and Setters */
    var id: Long = -1                // Id
    var item: Item? = null                // Item
}
