package com.ghstudios.android.data.classes

/**
 * Class for HuntingReward
 */
class HuntingReward {

    /* Getters and Setters */
    var id: Long = -1            // HuntingReward id
    var item: Item? = null            // Item id
    var condition: String? = ""    // Condition to obtain Item
    var monster: Monster? = null    // Monster that drops Item
    var rank: String? = ""        // Quest rank
    var stackSize: Int = -1        // Amount of Item dropped
    var percentage: Int = -1        // Percentage of obtaining Item
}
