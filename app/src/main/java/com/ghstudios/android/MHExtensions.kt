package com.ghstudios.android

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

val TAG = "MHGU_DATABASE"

/**
 * Runs a function in a separate thread that logs the time taken, and any errors that occur.
 */
inline fun loggedThread(name: String? = null, crossinline process: () -> Unit) {
    val nameDisplay = name ?: "Unnamed"

    thread(start=true) {
        try {
            val timeToRun = measureTimeMillis(process)
            Log.d(TAG, "Ran $nameDisplay thread in $timeToRun milliseconds")
        } catch (ex: Exception) {
            Log.e(TAG, "Error in $nameDisplay thread", ex)
        }
    }
}

/**
 * Extension: Retrieves a drawable associated with a resource id
 * via ContextCompat using the called context.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

// todo: determine proper home. Unlike the other db extensions, this is also "general use"
/**
 * Extension function that converts a cursor to a list of objects using a transformation function.
 * The cursor is closed at the completion of this method.
 */
fun <T, J : Cursor> J.toList(process: (J) -> T) : List<T> {
    return MHUtils.cursorToList(this, object: MHUtils.CursorProcessFunction<T, J> {
        override fun getValue(c: J): T = process(c)
    })
}

/**
 * Extension function that pulls one entry from a cursor using a transform function,
 * or null if the cursor is empty.
 * The cursor is closed at the completion of this method.
 */
fun <T, J : Cursor> J.firstOrNull(process: (J) -> T) : T? {
    // todo: implement asSequence() extension, use that one
    return this.toList(process).firstOrNull()
}

/**
 * Extension function that sets an ImageView to use an ITintedIcon, loaded via the AssetLoader.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun ImageView.setImageAsset(icon: ITintedIcon) {
    AssetLoader.setIcon(this, icon)
}