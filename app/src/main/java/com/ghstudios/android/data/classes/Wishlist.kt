package com.ghstudios.android.data.classes

/**
 * Class that represents basic wishlist information
 */
class Wishlist {
    var id: Long = -1            // Wishlist id
    var name: String? = ""        // Wishlist name

    constructor()

    constructor(id: Long, name: String) {
        this.id = id
        this.name = name
    }
}
