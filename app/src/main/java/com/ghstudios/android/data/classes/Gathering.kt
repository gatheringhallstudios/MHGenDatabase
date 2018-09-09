package com.ghstudios.android.data.classes

/*
 * Class for Gathering
 */
class Gathering {

    /* Getters and Setters */
    var id: Long = -1

    // Item gathered
    var item: Item? = null

    // Location the item is gathered
    var location: Location? = null

    var area: String? = ""        // Area # of location
    var site: String? = ""        // Type of gathering node; bug, mine, fish, etc.
    var rank: String? = ""        // Quest Rank found in
    var rate: Float = 0f          // Gather rate

    //What group is it a part of, (Unique within an area)
    var group: Int = 0

    //is this a fixed gathering point
    var isFixed: Boolean = false

    //Is it a rare point
    var isRare: Boolean = false

    var quantity: Int = 1
}
