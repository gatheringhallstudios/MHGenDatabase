package com.ghstudios.android.data.classes

/*
 * Represents a set of weaknesses for a monster in a specific state.
 *
 * Each element or ailment is set from 0-7
 * 0 is Immune or Ineffective
 * at 4 its effective,
 * at 5 its very effective
 * and at 7 its super effective
 */

class MonsterWeakness {
    companion object {
        const val FIRE = 0
        const val WATER = 1
        const val THUNDER = 2
        const val ICE = 3
        const val DRAGON = 4
        const val POISON = 5
        const val PARALYSIS = 6
        const val SLEEP = 7
        const val PITFALL_TRAP = 8
        const val SHOCK_TRAP = 9
        const val FLASH_BOMB = 10
        const val SONIC_BOMB = 11
        const val DUNG_BOMB = 12
        const val MEAT = 13
    }

    /* Getters and Setters */
    var id: Long = -1

    // Monster State
    var state: String? = ""

    // Fire element effectiveness (unused)
    var fire: Int = -1

    // Water element effectiveness (unused)
    var water: Int = -1

    // Thunder element effectiveness (unused)
    var thunder: Int = -1

    // Ice element effectiveness (unused)
    var ice: Int = -1

    // Dragon element effectiveness (unused)
    var dragon: Int = -1

    // Poison effectiveness
    var poison: Int = -1

    // Paralysis effectiveness
    var paralysis: Int = -1

    // Sleep effectiveness
    var sleep: Int = -1

    // Pitfall trap effectiveness
    var pitfalltrap: Boolean = false

    // Shock trap effectiveness
    var shocktrap: Boolean = false

    // Flash bomb effectiveness
    var flashbomb: Boolean = false

    // Sonic bomb effectiveness
    var sonicbomb: Boolean = false

    // Dung bomp effectiveness
    var dungbomb: Boolean = false

    // Meat effectiveness
    var meat: Boolean = false

    /**
     * Returns a map mapping the constant weakness field names to the weakness value
     */
    fun getElementAndStatusWeaknesses(): Map<Int, Int> {
        return mapOf(
                FIRE to fire,
                WATER to water,
                THUNDER to thunder,
                ICE to ice,
                DRAGON to dragon,
                POISON to poison,
                PARALYSIS to paralysis,
                SLEEP to sleep
        )
    }
}
