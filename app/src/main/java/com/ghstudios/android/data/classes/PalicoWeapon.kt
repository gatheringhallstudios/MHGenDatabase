package com.ghstudios.android.data.classes

/**
 * Created by Joseph on 7/9/2016.
 */
class PalicoWeapon {
    var id: Long = 0
    var attackMelee: Int = 0
    var attackRanged: Int = 0
    var isBlunt: Boolean = false
    var balance: Int = 0

    var element: String? = null
    var elementMelee: Int = 0
    var elementRanged: Int = 0

    val elementEnum get() = getElementFromString(element ?: "")

    var affinityMelee: Int = 0
    var affinityRanged: Int = 0
    var defense: Int = 0
    var creation_cost: Int = 0
    var sharpness: Int = 0
    var item: Item? = null

    val balanceString: String
        get() {
            when (balance) {
                // todo: support translations in some way
                0 -> return "Balanced"
                1 -> return "Melee+"
                else -> return "Boomerang+"
            }
        }
}
