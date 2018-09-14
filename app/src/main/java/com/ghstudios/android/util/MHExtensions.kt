package com.ghstudios.android.util

import android.util.Log
import android.widget.ImageView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ITintedIcon
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
 * Extension function that sets an ImageView to use an ITintedIcon, loaded via the AssetLoader.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun ImageView.setImageAsset(icon: ITintedIcon?) {
    AssetLoader.setIcon(this, icon)
}