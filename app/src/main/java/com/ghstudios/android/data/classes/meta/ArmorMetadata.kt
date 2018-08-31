package com.ghstudios.android.data.classes.meta

import com.ghstudios.android.ITintedIcon

data class ArmorMetadata(
    val id: Long,
    val name: String,
    val slot: String,
    val rarity: Int,
    val family: Long,
    val familyName: String,
    val icon_name: String
) : ITintedIcon {
    override fun getIconResourceString() = icon_name

    // uncomment the below if we want to re-introduce rarity colors
//    override fun getColorArrayId() = R.array.rare_colors
//    override fun getIconColorIndex() = rarity - 1
}