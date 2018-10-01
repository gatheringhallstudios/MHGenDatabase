package com.ghstudios.android.data.util

private fun normalize(name: String) = name.trim().toLowerCase()

/**
 * An in memory class that handles search matching for filtering.
 * Use this if you want to filter in code instead of in the database.
 */
class SearchFilter(searchTerm: String) {
    val searchWords = normalize(searchTerm).split(' ')

    fun matches(other: String?): Boolean {
        other ?: return true

        val nameNormalized = normalize(other)
        return searchWords.all { nameNormalized.contains(it) }
    }
}