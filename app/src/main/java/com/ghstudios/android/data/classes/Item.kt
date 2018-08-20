package com.ghstudios.android.data.classes

import com.ghstudios.android.ITintedIcon
import com.ghstudios.android.mhgendatabase.R

/**
 * Class for Item
 */
open class Item : ITintedIcon {
    var id: Long = -1
    var name: String? = ""

    /**
     * The items's japanese name
     */
    var jpnName: String? = ""

    var type: String? = ""
    var subType: String? = ""

    var rarity: Int = -1
    var carryCapacity: Int = -1

    /**
     * Buy price
     */
    var buy: Int = -1

    /**
     * Sell price
     */
    var sell: Int = -1

    var description: String? = ""
    var fileLocation: String? = ""            // File location for image

    var isAccountItem: Boolean = false
    var iconColor: Int = 0

    val rarityString: String
        get() = if (rarity == 11) "X" else Integer.toString(rarity)

    override fun getIconResourceString() = fileLocation ?: ""

    internal fun usesRarity() = when (type) {
        "Weapon", "Armor", "Palico Weapon", "Palico Armor" -> true
        else -> false
    }

    override fun getIconColorIndex(): Int {
        return if (usesRarity()) rarity - 1 else iconColor
    }

    override fun getColorArrayId(): Int {
        return if (usesRarity()) R.array.rare_colors else R.array.item_colors
    }
}
