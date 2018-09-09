package com.ghstudios.android.data.classes

class ArmorFamily{
    var id: Long = -1
    var name:String? = ""
    var minDef = 0
    var maxDef = 0
    var rarity = 1
    val skills : MutableList<String> = mutableListOf()
}