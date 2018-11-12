package com.ghstudios.android.data.util

/**
 * A class that computes the SQL where clause predicate and parameters to add
 * to allow a query to search for a value.
 */
class SqlFilter(private val columnName: String, filter: String) {
    // todo: normalize filter
    private val words = filter.trim().split("\\s+".toRegex())

    /**
     * The SQL predicate to put in the where clause
     * If filter is empty, returns TRUE
     */
    val predicate = when {
        words.isEmpty() -> "TRUE"
        else -> words.joinToString(" AND ") { "$columnName LIKE ?" }
    }

    /**
     * The parameters that bind to the predicate of the where clause
     */
    val parameters = words.map { "%$it%" }.toTypedArray()
}