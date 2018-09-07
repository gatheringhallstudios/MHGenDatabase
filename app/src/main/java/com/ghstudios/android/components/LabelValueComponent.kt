package com.ghstudios.android.components

/**
 * Defines an interface for any component that provides a label and a value
 */
interface LabelValueComponent {
    fun setLabelText(text: CharSequence?)
    fun setValueText(text: CharSequence?)
    fun getLabelText(): CharSequence?
    fun getValueText(): CharSequence?
}