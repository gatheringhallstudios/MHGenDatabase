package com.ghstudios.android

import android.app.Application
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.Location
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.mhgendatabase.R

/**
 * A static class used to load icons for various database objects.
 * Initialized when the application is loaded.
 */
object AssetLoader {
    private lateinit var application: Application

    private val ctx get() = application.applicationContext

    fun bindApplication(app: Application) {
        application = app
    }

    /**
     * Loads a tinted icon using an ITintedIcon, returning it as a Drawable
     */
    @JvmStatic
    fun loadIconFor(item: ITintedIcon): Drawable? {
        var resId = MHUtils.getDrawableId(ctx,item.getIconResourceString())
        if (resId <= 0) {
            resId = R.drawable.icon_quest_mark
        }

        val arr = MHUtils.getIntArray(ctx, item.getColorArrayId())
        val color = arr[item.getIconColorIndex()]

        val image = ContextCompat.getDrawable(ctx, resId)?.mutate()
        image?.setColorFilter(color, PorterDuff.Mode.MULTIPLY)

        return image
    }

    @JvmStatic
    fun setIcon(iv: ImageView, item: ITintedIcon) {
        iv.setImageDrawable(loadIconFor(item))
    }
}