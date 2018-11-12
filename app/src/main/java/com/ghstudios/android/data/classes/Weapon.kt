package com.ghstudios.android.data.classes

import android.util.Log

import java.util.ArrayList
import java.util.Arrays

data class WeaponChargeLevel(
        val name: String,
        val level: Int,

        /** True if LoadUp is required to access */
        val locked: Boolean
)

/**
 * Contains all available coating types for a Bow.
 * Initialized using the coatingCode field that comes from the database.
 */
class WeaponBowCoatings(val coatingCode: Int) {
    val power1 = hasCoating(1 shl 10)
    val power2 = hasCoating(1 shl 9)
    val elem1 = hasCoating(1 shl 8)
    val elem2 = hasCoating(1 shl 7)
    val crange = hasCoating(1 shl 6)
    val poison = hasCoating(1 shl 5)
    val para = hasCoating(1 shl 4)
    val sleep = hasCoating(1 shl 3)
    val exhaust = hasCoating(1 shl 2)
    val blast = hasCoating(1 shl 1)
    val paint = hasCoating(1 shl 0)

    private fun hasCoating(code: Int): Boolean {
        return (coatingCode and code) > 0
    }

    /**
     * If this weapon supports either power1 or power2
     */
    val hasPower = power1 || power2

    /**
     * If this weapon supports either elem up 1 or elem up 2.
     */
    val hasElem = elem1 || elem2
}

/**
 * Class for Weapon
 */
class Weapon : Item() {
    companion object {
        /* Constant weapon types */
        const val GREAT_SWORD = "Great Sword"
        const val LONG_SWORD = "Long Sword"
        const val SWORD_AND_SHIELD = "Sword and Shield"
        const val DUAL_BLADES = "Dual Blades"
        const val HAMMER = "Hammer"
        const val HUNTING_HORN = "Hunting Horn"
        const val LANCE = "Lance"
        const val GUNLANCE = "Gunlance"
        const val SWITCH_AXE = "Switch Axe"
        const val CHARGE_BLADE = "Charge Blade"
        const val INSECT_GLAIVE = "Insect Glaive"
        const val LIGHT_BOWGUN = "Light Bowgun"
        const val HEAVY_BOWGUN = "Heavy Bowgun"
        const val BOW = "Bow"
    }

    // Weapon type
    var wtype: String? = ""

    var creationCost = -1
    var upgradeCost = -1
    var attack = -1
    var maxAttack = -1

    // Elemental attack type
    var element: String? = ""

    // Second element type
    var element2: String? = ""

    // Awakened elemental type
    var awaken: String? = ""

    val elementEnum get() = getElementFromString(element)
    val element2Enum get() = getElementFromString(element2)
    val awakenElementEnum get() = getElementFromString(awaken)

    var elementAttack: Long = 0
    var element2Attack: Long = 0
    var awakenAttack: Long = 0

    // Defense given by the weapon
    var defense = -1

    // Sharpness values
    var sharpness: String? = ""

    // Affinity
    var affinity: String? = ""

    // Horn notes
    var hornNotes: String? = ""

    // Shelling type
    var shellingType: String? = ""

    // Phial type
    var phial: String? = ""

    // Charges for bows
    var charges: List<WeaponChargeLevel> = emptyList()

    /**
     * Supported bow coatings. Null for non-bow weapons.
     */
    var coatings: WeaponBowCoatings? = null

    var recoil: String? = ""                    // Recoils for bowguns; arc for bows
    var reloadSpeed: String? = ""               // Reload speed for bowguns
    var rapidFire: String? = ""                 // Rapid fire/crouching fire for bowguns
    var deviation: String? = ""                 // Deviation for bowguns
    var ammo: String? = ""                      // Ammo for bowguns
    var specialAmmo: String? = ""               // Special ammo for bowguns (rapid fire ammo?)

    // Set the slot to view
    // Unicode White Circle \u25CB
    // Unicode FIGURE DASH \u2012
    var numSlots = -1

    // Final in weapon tree or not
    var wFinal = -1

    // Depth of weapon in weapon tree
    var tree_Depth = 0

    var parentId: Int = 0

    val slotString get() = when (this.numSlots) {
        0 -> "\u2012\u2012\u2012"
        1 -> "\u25CB\u2012\u2012"
        2 -> "\u25CB\u25CB\u2012"
        3 -> "\u25CB\u25CB\u25CB"
        else -> "error!!"
    }

    var sharpness1: IntArray? = null
        private set
    var sharpness2: IntArray? = null
        private set
    var sharpness3: IntArray? = null
        private set

    val attackString: String
        get() = Integer.toString(attack)

    fun initializeSharpness() {
        // Sharpness is in the format "1.1.1.1.1.1.1 1.1.1.1.1.1.1" where each
        // 1 is an int representing the sharpness value of a certain color.
        // The order is red, orange, yellow, green, white, purple.
        // First set is for regular sharpness, second set is for sharpness+1

        // early exit if sharpness is null
        val sharpness = this.sharpness ?: return

        //Joe: For MHX, there is only 6 levels of sharpness, but the extra doesn't hurt anything.
        var sharpness1 = IntArray(7)
        var sharpness2 = IntArray(7)
        var sharpness3 = IntArray(7)

        //separate both sets of sharpness
        val strSharpnessBoth = sharpness.split(" ".toRegex()).dropLastWhile { it.isEmpty() }

        //convert sharpness strings to arrays
        val strSharpness1 = strSharpnessBoth[0].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
        val strSharpness2 = strSharpnessBoth[1].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()
        val strSharpness3 = strSharpnessBoth[2].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()

        //add leading 0s to those with less than purple sharpness
        while (strSharpness1.size <= 7) {
            strSharpness1.add("0")
        }
        while (strSharpness2.size <= 7) {
            strSharpness2.add("0")
        }
        while (strSharpness3.size <= 7) {
            strSharpness3.add("0")
        }

        // Error handling logs error and passes empty sharpness bars
        for (i in 0..6) {
            try {
                sharpness1[i] = Integer.parseInt(strSharpness1[i])
            } catch (e: Exception) {
                Log.v("ParseSharpness", "Error in sharpness $sharpness")
                sharpness1 = intArrayOf(0, 0, 0, 0, 0, 0, 0)
                break
            }
        }
        for (i in 0..6) {
            try {
                sharpness2[i] = Integer.parseInt(strSharpness2[i])
            } catch (e: Exception) {
                Log.v("ParseSharpness", "Error in sharpness $sharpness")
                sharpness2 = intArrayOf(0, 0, 0, 0, 0, 0, 0)
                break
            }
        }

        for (i in 0..6) {
            try {
                sharpness3[i] = Integer.parseInt(strSharpness3[i])
            } catch (e: Exception) {
                Log.v("ParseSharpness", "Error in sharpness $sharpness")
                sharpness3 = intArrayOf(0, 0, 0, 0, 0, 0, 0)
                break
            }
        }

        this.sharpness1 = sharpness1
        this.sharpness2 = sharpness2
        this.sharpness3 = sharpness3
    }
}
