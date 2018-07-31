package com.ghstudios.android.data.classes

/*
 * Class for Monster
 */
class Monster {

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
}
