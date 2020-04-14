package com.ghstudios.android.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.ghstudios.android.GenericActionBarActivity
import com.ghstudios.android.mhgendatabase.R


// A collection of extension functions used by the app.
// Only those that could reasonably belong to a separate library should be here.
// Anything with stronger coupling on non-android objects should not be here

/**
 * Adds a bundle to a fragment and then returns the fragment.
 * A lambda is used to set values to the bundle
 */
fun <T: androidx.fragment.app.Fragment> T.applyArguments(block: Bundle.() -> Unit): T {
    val bundle = Bundle()
    bundle.block()
    this.arguments = bundle
    return this
}

/**
 * Uses the cursor, automatically closing after executing the process.
 * Use this instead of use {} because Cursor's are not "Closeable" pre API 16
 */
inline fun <J : Cursor, R> J.useCursor(process: (J) -> R): R {
    try {
        return process(this)
    } catch (e: Throwable) {
        throw e
    } finally {
        try {
            close()
        } catch (closeException: Throwable) {}
    }
}

/**
 * Extension function that iterates over a cursor, doing an operation for each step
 * The cursor is closed at the completion of this method.
 */
inline fun <T, J : Cursor> J.forEach(process: (J) -> T) {
    this.useCursor {
        while (moveToNext()) {
            process.invoke(this)
        }
    }
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
    // todo: optimize. Use cursor.moveToFirst() and check cursor.isAfterLast
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

/**
 * Extension: Retrieves a color associated with a resource id
 * via ContextCompat using the called context.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Context.getColorCompat(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

/**
 * Creates a block where "this" is the editor of the shared preferences.
 * The changes are commited asynchronously.
 */
inline fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    block(editor)
    editor.apply()
}

/**
 * Extension function that sends the activity result. If target fragment is not null,
 * it will call onActivityResult on the fragment. Otherwise, it will call it on the activity.
 *
 * This was created to allow a uniform interface between fragments and activities.
 */
fun androidx.fragment.app.DialogFragment.sendDialogResult(resultCode: Int, intent: Intent) {
    if (this.targetFragment != null) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, intent)
        return
    }

    val activity = activity as? GenericActionBarActivity
    activity ?: throw TypeCastException("sendDialogResult() only works on fragments and GenericActionBarActivity")
    activity.sendActivityResult(targetRequestCode, resultCode, intent)
}

/**
 * Creates a livedata from a block of code that is run in another thread.
 * The other thread is run in a background thread, and not on the UI thread.
 */
fun <T> createLiveData(block: () -> T): LiveData<T> {
    val result = MutableLiveData<T>()
    loggedThread("createLiveData") {
        result.postValue(block())
    }
    return result
}

/**
 * Helper function used to create a snackbar with an undo action.
 * The onComplete function is called when completed, onUndo is called if undone.
 */
fun View.createSnackbarWithUndo(message: String, onComplete: () -> Unit, onUndo: () -> Unit) {
    var wasUndone = false

    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    snackbar.setAction(R.string.undo) {
        wasUndone = true
        onUndo()
    }
    snackbar.addCallback(object: Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            if (!wasUndone) {
                onComplete()
            }
        }
    })
    snackbar.show()
}

/**
 * Helper function used to create a snackbar with an undo action.
 */
fun View.createSnackbarWithUndo(message: String, operation: UndoableOperation) {
    this.createSnackbarWithUndo(message,
            onComplete = operation::complete,
            onUndo = operation::undo)
}