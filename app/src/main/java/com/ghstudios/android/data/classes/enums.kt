package com.ghstudios.android.data.classes

import com.ghstudios.android.data.util.Converter

enum class ElementStatus {
    NONE,
    FIRE,
    WATER,
    THUNDER,
    ICE,
    DRAGON,
    POISON,
    PARALYSIS,
    SLEEP,
    BLAST,
    MOUNT,
    JUMP,
    EXHAUST,
    STUN
}

enum class MonsterClass {
    SMALL,
    LARGE,
    DEVIANT
}

val MonsterClassConverter = Converter(
        0 to MonsterClass.LARGE,
        1 to MonsterClass.SMALL,
        2 to MonsterClass.DEVIANT
)