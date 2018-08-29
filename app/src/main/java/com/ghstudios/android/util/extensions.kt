package com.ghstudios.android.util

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat


// A collection of extension functions used by the app.
// Only those that could reasonably belong to a separate library should be here.
// Anything with stronger coupling on non-android objects should not be here

/**
 * Adds a bundle to a fragment and then returns the fragment.
 * A lambda is used to set values to the bundle
 */
fun <T: Fragment> T.applyArguments(block: Bundle.() -> Unit): T {
    val bundle = Bundle()
    bundle.block()
    this.arguments = bundle
    return this
}

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
 * or throws a NoSuchElementException if it doesn't exist.
 */
fun <T, J : Cursor> J.first(process: (J) -> T): T {
    return this.toList(process).first()
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
 * Extension: Retrieves a drawable associated with a resource id
 * via ContextCompat using the called context.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}