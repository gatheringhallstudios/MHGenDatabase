package com.ghstudios.android.data.classes



/**
 * The external part of an Armor Set Builder set.
 * Holds metadata, such as the set's name.
 */
open class ASBSet(
        var id: Long = -1,
        var name: String = "",
        var rank: Rank = Rank.ANY,

        /**
         * The session's hunter type. 0 is blademaster, 1 is gunner, 2 is either, -1 is undefined
         */
        var hunterType: Int = -1
)
