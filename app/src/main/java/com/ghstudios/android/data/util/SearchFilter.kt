package com.ghstudios.android.data.util

private fun normalize(name: String) = name.trim().toLowerCase()

/**
 * An in memory class that handles search matching for filtering.
 * Use this if you want to filter in code instead of in the database.
 */
class SearchFilter(searchTerm: String) {
    val searchWords = normalize(searchTerm).split(' ')

    /**
     * Tests if other is a match for this search filter.
     * If other is null, returns false, otherwise checks if all words in this filter
     * is contained a substring in other.
     */
    fun matches(other: String?): Boolean {
        other ?: return false

        val otherNormalized = normalize(other)
        return searchWords.all { otherNormalized.contains(it) }
    }
}