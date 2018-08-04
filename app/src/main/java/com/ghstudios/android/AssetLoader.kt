package com.ghstudios.android

import android.app.Application
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
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

    @JvmStatic
    fun setIcon(iv:ImageView,item:ITintedIcon){
        var resId = MHUtils.getDrawableId(ctx,item.getIconResourceString())
        if(resId <= 0) resId = R.drawable.icon_quest_mark
        iv.setImageResource(resId)
        val arr = MHUtils.getIntArray(ctx,item.getColorArrayId())
        iv.setColorFilter(arr[item.getIconColorIndex()],PorterDuff.Mode.MULTIPLY)
    }

}