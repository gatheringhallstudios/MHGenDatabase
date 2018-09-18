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
    val pieces: List<ArmorSetPiece>
}