package com.ghstudios.android.data.classes

/**
 * Created by Mark on 2/22/2015.
 * A container for monster status info
 */
class MonsterStatus {
    var monster: Monster? = null
    var status: String? = ""
    var initial: Long = -1
    var increase: Long = -1
    var max: Long = -1
    var duration: Long = -1
    var damage: Long = -1

    val statusEnum get() = getElementFromString(status ?: "")
}
