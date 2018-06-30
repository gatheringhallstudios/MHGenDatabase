@file:JvmName("Converters")
package com.ghstudios.android.data.classes

import android.util.Log

val TAG = "MHGUDataConverter"

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