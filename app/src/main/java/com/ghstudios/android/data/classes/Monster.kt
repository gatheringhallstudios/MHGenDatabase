package com.ghstudios.android.data.classes

import com.ghstudios.android.ITintedIcon
import com.ghstudios.android.mhgendatabase.R

/*
 * Class for Monster
 */
class Monster : ITintedIcon {

    /* Getters and Setters */
    var id: Long = -1                // Monster id
    var name = ""            // Monster name
    var jpnName = ""        // Japanese name

    var monsterClass = MonsterClass.LARGE

    var fileLocation = ""    // File location for image

    /**
     * Bitwise flags assembled as an integer.
     * flags are hasLR/hasHR/hasG
     */
    var metadata = 0

    override fun getIconResourceString(): String {
        return fileLocation
    }

}
