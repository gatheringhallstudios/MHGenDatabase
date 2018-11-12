package com.ghstudios.android.data.classes

/*
 * Class for Armor
 *
 * Note: Subclass of Item
 */
class Armor : Equipment() {
    companion object {
        const val ARMOR_TYPE_BLADEMASTER = 0
        const val ARMOR_TYPE_GUNNER = 1
        const val ARMOR_TYPE_BOTH = 2

        const val GENDER_MALE = 0
        const val GENDER_FEMALE = 1
        const val GENDER_BOTH = 2

        const val ARMOR_SLOT_HEAD = "Head"
        const val ARMOR_SLOT_BODY = "Body"
        const val ARMOR_SLOT_ARMS = "Arms"
        const val ARMOR_SLOT_WAIST = "Waist"
        const val ARMOR_SLOT_LEGS = "Legs"
    }

    /* Getters and Setters */

    /** Equipment type */
    var slot: String? = ""

    var defense: Int = -1                 // Base defense
    var maxDefense: Int = -1              // Max defense
    var fireRes: Int = -1                 // Fire resistance
    var thunderRes: Int = -1              // Thunder resistance
    var dragonRes: Int = -1               // Dragon resistance
    var waterRes: Int = -1                // Water resistance
    var iceRes: Int = -1                  // Ice resistance
    var gender: Int = 2                   // Which gender can equip (0 = Male Only,1=Female Only,2 = Both)
    var hunterType: Int = ARMOR_TYPE_BOTH // Which hunter type can equip: Blademaster/Gunner (0 = Blademaster,1 = Gunner, 2 = Both)

    /** The id of the armor family this armorpiece is a part of */
    var family: Long = -1

    var slotString: String? = null
        // Unicode White Circle \u25CB
        // Unicode Dash \u2015
        get() = when (numSlots) {
            0 -> "\u2015\u2015\u2015"
            1 -> "\u25CB\u2015\u2015"
            2 -> "\u25CB\u25CB\u2015"
            3 -> "\u25CB\u25CB\u25CB"
            else -> "error!!"
        }


    override fun toString(): String {
        return this.name ?: ""
    }
}
