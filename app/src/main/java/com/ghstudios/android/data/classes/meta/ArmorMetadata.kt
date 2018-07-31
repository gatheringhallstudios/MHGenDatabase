package com.ghstudios.android.data.classes.meta

data class ArmorMetadata(
    val id: Long,
    val name: String,
    val slot: String,
    val rarity: Int,
    val family: Int,
    val familyName: String,
    val icon_name: String
)