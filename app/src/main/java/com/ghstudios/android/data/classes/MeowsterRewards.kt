package com.ghstudios.android.data.classes

import com.ghstudios.android.ITintedIcon
import com.ghstudios.android.mhgendatabase.R

enum class MeowsterType(val type:Int){
    STONE(0),
    FISH(1),
    GRASS(2),
    NUTS(3),
    BONE(4),
    BAIT(5),
    HONEY(6),
    INSECT(7),
    MUSHROOM(8)
}

class MeowsterRewards : ITintedIcon {
    var item:Item? = null
    var location:String? = null
    var isRare: Boolean = false
    var rank: String? = null
    var count:Int = 0
    var percentage: Int = 0
    var type: MeowsterType? = null

    override fun getIconResourceString(): String {
        return when(type){
            MeowsterType.STONE -> "icon_ore"
            MeowsterType.BAIT -> "icon_bait"
            MeowsterType.BONE -> "icon_bone"
            MeowsterType.FISH -> "icon_fish"
            MeowsterType.GRASS -> "icon_herb"
            MeowsterType.HONEY -> "icon_webbing"
            MeowsterType.INSECT -> "icon_bug"
            MeowsterType.MUSHROOM -> "icon_mushroom"
            else -> "icon_unknown"
        }
    }

    override fun getColorArrayId(): Int {
        return R.array.item_colors
    }

    override fun getIconColorIndex(): Int {
        return 0
    }
}
