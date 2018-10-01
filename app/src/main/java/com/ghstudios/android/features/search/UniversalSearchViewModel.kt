package com.ghstudios.android.features.search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.Weapon
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

        val start = System.currentTimeMillis()
        fun logTime(label: String) {
            Log.d("SearchQuery", "$label took ${System.currentTimeMillis() - start} milliseconds")
        }

        return mutableListOf<Any>().apply {
            addAll(db.queryLocationsSearch(searchTerm))
            logTime("locations")

            addAll(db.queryMonstersSearch(searchTerm).toList { it.monster })
            logTime("monsters")

            addAll(db.queryQuestsSearch(searchTerm).toList { it.quest })
            logTime("quests")

            addAll(db.querySkillTreesSearch(searchTerm).toList { it.skillTree })
            logTime("skills")

            addAll(db.queryDecorationsSearch(searchTerm).toList { it.decoration })
            logTime("decorations")

            // retrieve all armor families
            val armorFamilies = db.queryArmorFamilyBaseSearch(searchTerm)
            addAll(armorFamilies)
            logTime("armorsets")

            // retrieve armor not in the family results
            val familyIds = armorFamilies.mapTo(mutableSetOf()) { it.id }
            val armor = db.queryArmorSearch(searchTerm)
                    .toList { it.armor }
                    .filter { it.family !in familyIds }
            addAll(armor)
            logTime("armor")

            // add the rest of the items
            // todo: query the rest of the item types separately, so that we don't have to remove the already added
            val processedTypes = listOf(ItemType.DECORATION, ItemType.ARMOR)
            addAll(db.queryItemSearch(searchTerm, omitTypes = processedTypes)
                    .toList { it.item })
            logTime("items")
        }
    }
}