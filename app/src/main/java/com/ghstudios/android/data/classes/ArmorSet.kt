package com.ghstudios.android.data.classes

/**
 * Represents a single armor piece of an armor set.
 * Couples the slot, the equipment, and a list of decorations.
 */
class ArmorSetPiece(val idx: Int, val equipment: Equipment) {
    val decorations = mutableListOf<Decoration>()
}

/**
 * Interface describing an armor set, which consists of multiple pieces each consisting
 * of a piece of equipment and attached decorations
 */
interface ArmorSet {
    companion object {
        const val WEAPON = 0
        const val HEAD = 1
        const val BODY = 2
        const val ARMS = 3
        const val WAIST = 4
        const val LEGS = 5
        const val TALISMAN = 6
    }

    /**
     * Returns a list of all armor set pieces, in piece index order.
     * Weapon -> Head -> Body -> Arms -> Waist -> Legs -> Talisman
     */
    val pieces: List<ArmorSetPiece>

    /**
     * Returns the armor set piece in the given location, or
     * null if there is no value.
     */
    fun getPiece(pieceIndex: Int): ArmorSetPiece?
}