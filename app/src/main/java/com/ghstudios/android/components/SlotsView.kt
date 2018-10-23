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
    var imageSize: Int = -1

    constructor(context: Context): super(context) {
        init(0, 0)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SlotsView)
        try {
            hideExtras = attributes.getBoolean(R.styleable.SlotsView_hideExtras, false)
            val maxSlots = attributes.getInt(R.styleable.SlotsView_maxSlots, 0)
            val usedSlots = attributes.getInt(R.styleable.SlotsView_usedSlots, 0)
            imageSize = attributes.getDimensionPixelSize(R.styleable.SlotsView_imageSize, -1)
            if (imageSize < 0) {
                imageSize = context.resources.getDimension(R.dimen.image_size_xsmall).roundToInt()
            }

            init(maxSlots, usedSlots)
        } finally {
            attributes.recycle()
        }
    }

    private fun init(maxSlots: Int, usedSlots: Int) {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val gap = imageSize / 5

        // Create image views. They should have gaps between them
        decorationImageViews = listOf(ImageView(context), ImageView(context), ImageView(context))
        for ((idx, decorationImage) in decorationImageViews.withIndex()) {
            decorationImage.layoutParams = LayoutParams(imageSize, imageSize).apply {
                // add a left margin to each image unless its the first one
                if (idx != 0) {
                    setMargins(gap, 0, 0, 0)
                }
            }
            addView(decorationImage)
        }

        setSlots(maxSlots, usedSlots)
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