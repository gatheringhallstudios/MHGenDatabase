package com.ghstudios.android.data.util

/**
 * Defines a class used to convert from one type of data to another, and back.
 * The pairs given define a conversion from the database type to the app type.
 * Note that while there can be multiple values of the DB type that map to an app type,
 * one app type can only map to one db type (only the first reverse mapping is used).
 */
open class Converter<DBClass, AppClass>(vararg pairs: Pair<DBClass, AppClass>) {

    private val toMap = mapOf(*pairs)

    private val fromMap: Map<AppClass, DBClass> by lazy {
        val results = mutableMapOf<AppClass, DBClass>()

        for ((key, value) in toMap) {
            if (!results.containsKey(value)) {
                results[value] = key
            }
        }

        results
    }
    
    /**
     * Convert from an app type to a db type.
     */
    fun serialize(obj: AppClass): DBClass {
        try {
            return fromMap.getValue(obj)
        } catch (ex: Exception) {
            throw IllegalArgumentException("Cannot serialize $obj")
        }
    }

    /**
     * Convert from a db type to an app type.
     */
    fun deserialize(obj: DBClass): AppClass {
        try {
            return toMap.getValue(obj)
        } catch (ex: Exception) {
            throw IllegalArgumentException("Cannot deserialize $obj")
        }
    }
}