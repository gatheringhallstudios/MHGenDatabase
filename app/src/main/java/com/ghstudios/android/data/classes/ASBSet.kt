package com.ghstudios.android.data.classes



/**
 * The external part of an Armor Set Builder set.
 * Holds metadata, such as the set's name.
 */
open class ASBSet(
        var id: Long = -1,
        var name: String = "",
        var rank: Rank = Rank.ANY,
        var hunterType: Int = -1 // 0 is undefined, 1 is blademaster, 2 is gunner
)
