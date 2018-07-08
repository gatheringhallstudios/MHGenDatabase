package com.ghstudios.android.data.classes

// todo: move elsewhere
enum class MonsterSize {
    SMALL,
    LARGE
}

/*
 * Class for Monster
 */
class Monster {

    /* Getters and Setters */
    var id: Long = -1                // Monster id
    var name = ""            // Monster name
    var jpnName = ""        // Japanese name
    var monsterClass = 0    // Large / small
    var fileLocation = ""    // File location for image
    var signatureMove = "" // Signature moves

    val monsterSize get() = when(monsterClass) {
        0 -> MonsterSize.LARGE
        else -> MonsterSize.SMALL
    }
}
