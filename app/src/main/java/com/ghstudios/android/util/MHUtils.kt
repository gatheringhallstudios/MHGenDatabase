package com.ghstudios.android.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.*
import android.database.Cursor
import android.support.annotation.*
import android.util.*
import com.ghstudios.android.mhgendatabase.R

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
     * Extracts every value in a cursor, returning a list of objects.
     * This method exhausts the cursor and closes it.
     */
    @JvmStatic fun <T, J : Cursor> cursorToList(c: J, transform: CursorProcessFunction<T, J>): List<T> {
        c.use {
            val results = ArrayList<T>(c.count)

            while (c.moveToNext()) {
                results.add(transform.getValue(c))
            }

            return results
        }
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

    private val hashMap = HashMap<String,Int>()
    /**
     * Returns the resource Id for a drawable in the res/drawable by String. Caches results in a
     * dictionary for future lookups.
     */
    @JvmStatic fun getDrawableId(c : Context, s : String) : Int{
        var id = hashMap[s]
        if(id != null) return id
        id = c.resources.getIdentifier("drawable/" + s.toLowerCase(),null,c.packageName)
        hashMap[s.toLowerCase()] = id
        return id
    }

    private val colorArrayHash = HashMap<Int,IntArray>()
    
    /**
     * Returns and Caches int array. Used mostly for rare_colors and item_color arrays.
     * Probably a small optimization, but since this will be called for every image,
     * caching is a good idea.
     */
    @JvmStatic fun getIntArray(c : Context, id : Int) : IntArray{
        var array = colorArrayHash[id]
        if(array != null) return array
        array = c.resources.getIntArray(id)
        colorArrayHash[id] = array
        return array
    }

    @JvmStatic fun getNoteColor(note: Char): Int {
        when (note) {
            'B' -> return R.color.item_dark_blue
            'C' -> return R.color.item_cyan
            'G' -> return R.color.item_dark_green
            'O' -> return R.color.item_orange
            'P' -> return R.color.item_dark_purple
            'R' -> return R.color.item_dark_red
            'W' -> return R.color.item_white
            'Y' -> return R.color.item_yellow
        }
        return R.color.item_white
    }

}