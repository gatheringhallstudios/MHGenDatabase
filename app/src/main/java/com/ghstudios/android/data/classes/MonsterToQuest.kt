package com.ghstudios.android.data.classes

/*
 * Class for MonsterToQuest
 */
class MonsterToQuest {
    var id: Long = -1
    var monster: Monster? = null
    var quest: Quest? = null
    var habitat: Habitat? = null

    var isUnstable: Boolean = false
    var isHyper: Boolean = false
}
