package com.ghstudios.android.data.classes

/**
 * Created by Mark on 2/22/2015.
 * Describes a habitat for a monster
 */
class Habitat {
    var id: Long = -1             //id of habitat entry
    var monster: Monster? = null     //id of the monster
    var location: Location? = null   //if of habitat location
    var start: Long = 0          //Starting area number
    var areas: LongArray? = null        //Array of areas
    var rest: Long = 0           //Rest area of the monster
}
