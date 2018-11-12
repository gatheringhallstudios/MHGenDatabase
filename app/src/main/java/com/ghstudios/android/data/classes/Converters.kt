@file:JvmName("Converters")
package com.ghstudios.android.data.classes

import android.util.Log
import com.ghstudios.android.data.util.Converter

fun getElementFromString(elementStr: String?) = when(elementStr ?: "") {
    "" -> ElementStatus.NONE
    "Fire" -> ElementStatus.FIRE
    "Water" -> ElementStatus.WATER
    "Thunder" -> ElementStatus.THUNDER
    "Ice" -> ElementStatus.ICE
    "Dragon" -> ElementStatus.DRAGON
    "Poison" -> ElementStatus.POISON
    "Paralysis" -> ElementStatus.PARALYSIS
    "Para" -> ElementStatus.PARALYSIS
    "Sleep" -> ElementStatus.SLEEP
    "Blastblight" -> ElementStatus.BLAST
    "Blast" -> ElementStatus.BLAST
    "Exhaust" -> ElementStatus.EXHAUST
    "KO" -> ElementStatus.STUN
    "Jump" -> ElementStatus.JUMP
    "Mount" -> ElementStatus.MOUNT
    
    else -> {
        Log.w("TAG", "Failed to convert element $elementStr")
        ElementStatus.NONE
    }
}

val MonsterClassConverter = Converter(
        0 to MonsterClass.LARGE,
        1 to MonsterClass.SMALL,
        2 to MonsterClass.DEVIANT
)

val ItemTypeConverter = Converter(
        "" to ItemType.ITEM,
        "Decoration" to ItemType.DECORATION,
        "Palico Weapon" to ItemType.PALICO_WEAPON,
        "Palico Armor" to ItemType.PALICO_ARMOR,
        "Weapon" to ItemType.WEAPON,
        "Armor" to ItemType.ARMOR,
        "Materials" to ItemType.MATERIAL
)