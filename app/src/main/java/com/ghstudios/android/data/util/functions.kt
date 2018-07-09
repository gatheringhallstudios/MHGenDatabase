package com.ghstudios.android.data.util

import com.ghstudios.android.AppSettings

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