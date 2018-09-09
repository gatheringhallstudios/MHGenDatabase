package com.ghstudios.android.features.search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.util.ThrottledExecutor
import com.ghstudios.android.util.toList
import kotlin.system.measureTimeMillis

class UniversalSearchViewModel(app: Application): AndroidViewModel(app) {
    private val TAG = javaClass.simpleName
    private val db = DataManager.get()

    // this prevents search from being overwhelmed and makes everything orderly
    private val executor = ThrottledExecutor()

    /**
     * Publicly exposed livedata that contains the search results
     */
    val searchResults = MutableLiveData<List<Any>>()

    // prevent double searching by storing the last search attempt
    private var lastSearchFilter = ""

    /**
     * Updates the search filter and begins searching.
     * SearchResults are populated when the search finishes.
     */
    fun updateSearchFilter(searchFilter: String) {
        val updatedFilter = searchFilter.trim()
        if (updatedFilter == lastSearchFilter) {
            return
        }

        lastSearchFilter = updatedFilter

        executor.execute {
            try {
                val time = measureTimeMillis {
                    val results = getResultsSync(updatedFilter)
                    searchResults.postValue(results)
                }
                Log.d(TAG, "Search performed in $time milliseconds")
            } catch (ex: Exception) {
                Log.e(TAG, "Error while performing search", ex)
            }
        }
    }

    private fun getResultsSync(searchTerm: String): List<Any> {
        if (searchTerm.isEmpty()) {
            return emptyList()
        }

        return mutableListOf<Any>().apply {
            addAll(db.queryMonstersSearch(searchTerm).toList { it.monster })
            addAll(db.queryQuestsSearch(searchTerm).toList { it.quest })
            addAll(db.querySkillTreesSearch(searchTerm).toList { it.skillTree })
            addAll(db.queryItemSearch(searchTerm).toList { it.item })
        }
    }
}