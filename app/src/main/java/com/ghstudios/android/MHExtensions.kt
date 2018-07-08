package com.ghstudios.android

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.util.Log
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
 * Extension: Loads a drawable from the assets folder.
 * Returns null on failure.
 */
fun Context.getAssetDrawable(path: String?): Drawable? {
    return MHUtils.loadAssetDrawable(this, path ?: "")
}

// todo: determine proper home. Unlike the other db extensions, this is also "general use"
/**
 * Extension function that converts a cursor to a list of objects using a transformation function.
 * The cursor is closed at the completion of this method.
 */
fun <T, J : Cursor> J.toList(process: (J) -> T) : List<T> {
    return MHUtils.cursorToList(this, process)
}