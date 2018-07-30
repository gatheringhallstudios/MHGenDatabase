package com.ghstudios.android

import android.app.Application
import android.graphics.drawable.Drawable
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.Location
import com.ghstudios.android.data.classes.Monster

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

    @JvmStatic
    fun loadIconFor(monster: Monster): Drawable? {
        val cellImage = "icons_monster/" + monster.fileLocation
        return ctx.getAssetDrawable(cellImage)
    }

    @JvmStatic
    fun loadIconFor(location: Location): Drawable? {
        val path = "icons_location/" + location.fileLocationMini
        return ctx.getAssetDrawable(path)
    }

    @JvmStatic
    fun loadIconFor(item: Item): Drawable? {
        return ctx.getAssetDrawable(item.itemImage)
    }

    // todo: add more overloads for "base" objects. Don't add special ones for composite objects.
}