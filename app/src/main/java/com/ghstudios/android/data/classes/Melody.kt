package com.ghstudios.android.data.classes

/*
 * Class for Melody
 */
class Melody {

    /* Getters and Setters */
    var id: Long = 0                // id
    var name: String = ""
    var notes: String = ""           // notes available
    var song: String = ""            // song available
    var effect1: String? = null         // initial effect
    var effect2: String? = null         // encore effect
    var duration: String? = null        // duration
    var extension: String? = null       // encore duration

    /* Default Constructor */
    init {
        this.id = -1
        this.effect1 = ""
        this.effect2 = ""
        this.duration = ""
        this.extension = ""
    }

}
