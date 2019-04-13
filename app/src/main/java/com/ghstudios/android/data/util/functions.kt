package com.ghstudios.android.data.util

import com.ghstudios.android.AppSettings
import org.json.JSONArray

/**
 * Computes the column name to allow the function to work in a specific locale
 */
fun localizeColumn(locale: String, columnName: String) = when(locale) {
    "en" -> columnName
    else -> "${columnName}_$locale"
}

/**
 * Returns the localized form of the base column name for the current locale
 */
fun localizeColumn(columnName: String) = localizeColumn(AppSettings.dataLocale, columnName)


/**
 * Extension used to build an iterator from a JSONArray. Each index is evaluated using transform.
 */
fun <T> JSONArray.iter(transform: JSONArray.(Int) -> T) = sequence {
    for (i in 0..(this@iter.length() - 1)) {
        yield(transform(i))
    }
}