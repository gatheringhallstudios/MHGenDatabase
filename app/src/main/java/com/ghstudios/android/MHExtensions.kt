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