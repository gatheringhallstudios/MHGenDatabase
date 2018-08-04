package com.ghstudios.android

import com.ghstudios.android.mhgendatabase.R

/*
Interface for objects that can be displayed as icons
 */
interface ITintedIcon{
    fun getIconResourceString():String
    //Gets color array id
    fun getColorArrayId():Int{
        return R.array.rare_colors
    }
    //Gets color index
    fun getIconColorIndex():Int{
        return 0
    }
}