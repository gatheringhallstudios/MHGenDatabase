package com.ghstudios.android

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.*
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.util.*

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

/**
 * A static class that provides helper methods for accessing and managing the `res` directory.
 */
object MHUtils {

    // internal interface to represent functions that map a cursor to an object
    interface CursorProcessFunction<T, J : Cursor> {
        fun getValue(c: J): T
    }

    // internal class to represent functions that create an object
    interface Builder<T> {
        fun build(): T
    }

    /**
     * Attempts to split the specified string by a comma.
     * Note: Avoid using this for new code. Its...awkward
     * @param stringArray The `string-array` resource from which to parse.
     * @param index The index in the string array to fetch a string from.
     * @param part The 0-based index of final piece of the string to retrieve.
     * @return The `part` index of a group of strings created by splitting the desired string by commas.
     */
    @JvmStatic fun splitStringInArrayByComma(@ArrayRes stringArray: Int, index: Int, part: Int, context: Context): String {
        val fullString: String

        try {
            fullString = context.resources.getStringArray(stringArray)[index]
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e("App", "The string array resource does not have the specified index.")
            return ""
        }

        try {
            return fullString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[part]
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e("App", "The specified string in the array does not have a comma in it.")
            return ""
        }

    }

    /**
     * Loads an image from the Assets folder as a drawable
     * @param ctx
     * @param path
     * @return
     */
    @JvmStatic fun loadAssetDrawable(ctx: Context, path: String): Drawable? {
        return try {
            ctx.assets.open(path).use {
                Drawable.createFromStream(it, null)
            }
        } catch (ex: Exception) {
            Log.e("MHGenUtils", "Failed to load asset $path", ex)
            null
        }
    }

    /**
     * Extracts every value in a cursor, returning a list of objects.
     * This method exhausts the cursor and closes it.
     * @param c
     * @param process
     * @param <T>
     * @return
    </T> */
    @JvmStatic fun <T, J : Cursor> cursorToList(c: J, transform: (J) -> T): List<T> {
        try {
            val results = ArrayList<T>(c.count)

            while (c.moveToNext()) {
                results.add(transform(c))
            }

            return results

        } finally {
            if (!c.isClosed) {
                c.close()
            }
        }
    }

    /**
     * Extracts every value in a cursor, returning a list of objects.
     * This method exhausts the cursor and closes it.
     */
    @JvmStatic fun <T, J : Cursor> cursorToList(c: J, transform: CursorProcessFunction<T, J>): List<T> {
        return cursorToList(c) { transform.getValue(c) }
    }

    /**
     * Creates a new livedata that is populated asynchronously using the provided
     * builder function. The builder function runs in a different thread.
     */
    @JvmStatic fun <T> createLiveData(builder: () -> T): LiveData<T> {
        val result = MutableLiveData<T>()
        Thread { result.postValue(builder()) }.start()
        return result
    }

    /**
     * Creates a new livedata that is populated asynchronously using the provided
     * builder function. The builder function runs in a different thread.
     * @param builder
     * @param <T>
     * @return
     */
    @JvmStatic fun <T> createLiveData(builder: Builder<T>): LiveData<T> {
        return createLiveData { builder.build() }
    }
}