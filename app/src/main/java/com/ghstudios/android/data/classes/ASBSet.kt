package com.ghstudios.android.data.classes

/**
 * The external part of an Armor Set Builder set.
 * Holds metadata, such as the set's name.
 */
class ASBSet {
    var id: Long = 0
    var name: String? = null
    var rank: Int = 0
    var hunterType: Int = 0 // 0 is undefined, 1 is blademaster, 2 is gunner

    init {
        id = -1
        name = ""
        rank = -1
        hunterType = -1
    }
}
