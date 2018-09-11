package com.ghstudios.android.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.ghstudios.android.mhgendatabase.R
import kotlin.math.roundToInt

/**
 * Custom view used to render a list of slots for a piece of equipments.
 * These slots can be empty or filled in.
 */
class SlotsView : LinearLayout {
    private lateinit var decorationImageViews: List<ImageView>

    /**
     * A flag used to control whether extra non-existant slots are not rendered.
     * If false, non-existant slots are rendered using a line.
     * If true, their visibility is set to GONE
     */
    var hideExtras: Boolean = false

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init()
    }

    fun init() {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val imageSize = context.resources.getDimension(R.dimen.image_size_xsmall).roundToInt()
        val gap = context.resources.getDimension(R.dimen.margin_small).roundToInt()

        // Create image views. They should have gaps between them
        decorationImageViews = listOf(ImageView(context), ImageView(context), ImageView(context))
        for ((idx, decorationImage) in decorationImageViews.withIndex()) {
            val isLast = decorationImageViews.size - 1 == idx
            decorationImage.layoutParams = LayoutParams(imageSize, imageSize).apply {
                if (!isLast) {
                    setMargins(0, 0, gap, 0)
                }
            }
            addView(decorationImage)
        }

        setSlots(0, 0)
    }

    /**
     * Updates this view's count of max slots and used slots.
     * Elements past the maximum number of slots may be hidden based on the value of hideExtras.
     */
    fun setSlots(maxSlots: Int, usedSlots: Int) {
        for (i in 0..2) {
            val view = decorationImageViews[i]

            if (i >= maxSlots && hideExtras) {
                view.visibility = View.GONE
                continue
            }
            view.visibility = View.VISIBLE

            val resource = when {
                i >= maxSlots -> R.drawable.decoration_none
                i >= usedSlots -> R.drawable.decoration_empty
                else -> R.drawable.decoration_real
            }

            view.setImageResource(resource)
        }
    }
}