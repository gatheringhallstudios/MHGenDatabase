package com.ghstudios.android

/**
 * Interface for objects that can be displayed as icons
 */
interface ITintedIcon {
    /**
     * Returns the name of the resource file associated with this icon.
     */
    fun getIconResourceString(): String

    /**
     * Returns the associated color array id.
     * If it returns 0, that means this icon should not be tinted.
     */
    fun getColorArrayId(): Int = 0

    /**
     * Returns the position in the color array for tinting this icon.
     */
    fun getIconColorIndex():Int = 0
}