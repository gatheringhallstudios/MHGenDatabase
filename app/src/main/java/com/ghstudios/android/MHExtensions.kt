package com.ghstudios.android

import android.database.Cursor
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
 * Extension function
 */
inline fun <T, J : Cursor> J.toList(crossinline process: (J) -> T) : List<T> {
    return MHUtils.cursorToList(this, {
        process(it)
    })
}

/**
 * Retrieves the value of the requested column as a string, using the column name.
 */
fun Cursor.getString(columnName: String) : String {
    return this.getString(getColumnIndex(columnName))
}

/**
 * Retrieves the value of the requested column as a string, using the column name.
 */
fun Cursor.getInt(columnName: String) : Int {
    return this.getInt(getColumnIndex(columnName))
}

/**
 * Retrieves the value of the requested column as an integer evaluated as a boolean.
 * All non-zero values evaluate to true. Zero is false
 */
fun Cursor.getBoolean(columnName : String) : Boolean {
    return this.getInt(columnName) != 0
}
