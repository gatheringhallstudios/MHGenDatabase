package com.ghstudios.android

import android.database.Cursor

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
