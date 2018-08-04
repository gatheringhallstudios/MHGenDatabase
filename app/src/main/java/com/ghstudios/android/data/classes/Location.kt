package com.ghstudios.android.data.classes

import com.ghstudios.android.ITintedIcon

/*
 * Class for Location
 */
class Location : ITintedIcon {

    /* Getters and Setters */
    var id: Long = 0                // Location id
    var name: String? = null            // Location name
    var fileLocation: String? = null    // File location for image

    /* Default Constructor */
    init {
        this.id = -1
        this.name = ""
        this.fileLocation = ""
    }

    fun getFileLocationMini(): String? {
        return fileLocation
    }

    override fun getIconResourceString(): String {
        return "loc_" + String.format("%02d", id % 100)
    }
}
