package com.ghstudios.android.data.classes

enum class ElementStatus {
    NONE,
    FIRE,
    WATER,
    THUNDER,
    ICE,
    DRAGON,
    POISON,
    PARALYSIS,
    SLEEP,
    BLAST,
    MOUNT,
    JUMP,
    EXHAUST,
    STUN
}

enum class MonsterClass {
    SMALL,
    LARGE,
    DEVIANT
}

enum class ItemType {
    ITEM,
    DECORATION,
    WEAPON,
    PALICO_WEAPON,
    PALICO_ARMOR,
    ARMOR,
    MATERIAL
}

/**
 * Defines a rank of an arbitrary object. Includes an ANY rank as a default option for cases
 * where there is no rank specification or filter.
 */
enum class Rank(val value: Int) {
    ANY(-1),
    LOW(0),
    HIGH (1),
    G (2);

    companion object {
        @JvmStatic val all = listOf(ANY, G, HIGH, LOW)

        private val reverseMap = Rank.values().map {
            it.value to it
        }.toMap()

        /**
         * Creates a rank object from an integer
         */
        @JvmStatic @JvmOverloads
        fun from(value: Int, default: Rank = Rank.ANY) = reverseMap[value] ?: default

        /**
         * Returns a Rank enumeration value depending on an armor rarity value.
         */
        @JvmStatic fun fromArmorRarity(rarity: Int) = when(rarity) {
            in 1..3 -> Rank.LOW
            in 3..7 -> Rank.HIGH
            in 8..10 -> Rank.G
            else -> Rank.ANY
        }
    }
}