package com.ghstudios.android.features.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ItemType
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

    /**
     * Contains the current search filter.
     */
    var searchFilter = ""
        private set

    /**
     * Updates the search filter and begins searching.
     * SearchResults are populated when the search finishes.
     */
    fun updateSearchFilter(searchFilter: String) {
        val updatedFilter = searchFilter.trim()
        if (updatedFilter == this.searchFilter) {
            return
        }

        this.searchFilter = updatedFilter

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

        var start = System.currentTimeMillis()
        fun logTime(label: String) {
            Log.d("SearchQuery", "$label took ${System.currentTimeMillis() - start} milliseconds")
            start = System.currentTimeMillis()
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

            // prefetch items. We do this because we want to insert armor in the middle
            // To improve this, add db index to item type, but might not be necessary.
            val types = listOf(ItemType.DECORATION, ItemType.WEAPON,
                    ItemType.PALICO_WEAPON, ItemType.PALICO_ARMOR,
                    ItemType.ITEM, ItemType.MATERIAL)
            val items = db.queryItemSearch(searchTerm, includeTypes=types).toList { it.item }
            logTime("items pre-fetch")

            // Add decorations before the rest of the items
            // if we need more info, queryDecorationsSearch() directly, and add indices to item_to_skill_tree
            addAll(items.filter { it.type == ItemType.DECORATION })
            logTime("decorations from items")

            // retrieve all armor families
            val armorFamilies = db.queryArmorFamilyBaseSearch(searchTerm, skipSolos = true)
            addAll(armorFamilies)
            logTime("armorsets")

            // retrieve armor not included in above families
            val familyIds = armorFamilies.mapTo(mutableSetOf()) { it.id }
            val armor = db.queryArmorSearch(searchTerm)
                    .toList { it.armor }
                    .filter { it.family !in familyIds }
            addAll(armor)
            logTime("armor")

            // add the rest of the items
            addAll(items.filter { it.type != ItemType.DECORATION })
            logTime("rest of items")
        }
    }
}