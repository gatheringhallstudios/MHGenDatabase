package com.ghstudios.android.data.classes

enum class WeaknessType {
    PITFALL_TRAP,
    SHOCK_TRAP,
    FLASH_BOMB,
    SONIC_BOMB,
    DUNG_BOMB,
    MEAT
}

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
    /* Getters and Setters */
    var id: Long = -1

    // Monster State
    var state: String? = ""

    // Fire element effectiveness
    var fire: Int = -1

    // Water element effectiveness
    var water: Int = -1

    // Thunder element effectiveness
    var thunder: Int = -1

    // Ice element effectiveness
    var ice: Int = -1

    // Dragon element effectiveness
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
     * for all elements. This is calculated every time.
     */
    val elementWeaknesses get() = mapOf(
            ElementStatus.FIRE to fire,
            ElementStatus.WATER to water,
            ElementStatus.THUNDER to thunder,
            ElementStatus.ICE to ice,
            ElementStatus.DRAGON to dragon
    )

    /**
     * Returns a map mapping the constant weakness filed names to the weakness values
     * for all status. This is calculated every time.
     */
    val statusWeaknesses get() = mapOf(
            ElementStatus.POISON to poison,
            ElementStatus.PARALYSIS to paralysis,
            ElementStatus.SLEEP to sleep
    )

    /**
     * Returns a list of all traps that work on this monster.
     * This is calculated every time
     */
    val vulnerableTraps get() = mapOf(
            WeaknessType.PITFALL_TRAP to pitfalltrap,
            WeaknessType.SHOCK_TRAP to shocktrap,
            WeaknessType.MEAT to meat
    ).filter { it.value }.keys.toList()

    /**
     * Returns a list of all bombs that work on this monster.
     * This is calculated every time.
     */
    val vulnerableBombs get() = mapOf(
            WeaknessType.FLASH_BOMB to flashbomb,
            WeaknessType.SONIC_BOMB to sonicbomb,
            WeaknessType.DUNG_BOMB to dungbomb
    ).filter { it.value }.keys.toList()
}
