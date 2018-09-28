package com.ghstudios.android.data.classes

import com.ghstudios.android.ITintedIcon
import com.ghstudios.android.mhgendatabase.R

class ArmorFamily: ITintedIcon {
    override fun getIconResourceString() = "armor_body"
    override fun getColorArrayId() = R.array.rare_colors
    override fun getIconColorIndex() = rarity - 1

    var id: Long = -1
    var name:String? = ""
    var minDef = 0
    var maxDef = 0
    var rarity = 1
    val skills : MutableList<String> = mutableListOf()
}