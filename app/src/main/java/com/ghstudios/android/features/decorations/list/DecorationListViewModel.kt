package com.ghstudios.android.features.decorations.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.util.SearchFilter
import com.ghstudios.android.util.toList

class DecorationListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    /**
     * LiveData containing the current value of the filter.
     * The decoration list derives off this to load the query.
     */
    private val filterSource = MutableLiveData<String>()

    // done synchronously as you can't really map multiple transformations simultaneously...
    private val allDecorationData = dataManager.queryDecorations().toList { it.decoration }

    /**
     * Contains the list of decorations, with a filter applied.
     * Returns decorations with a name or at least one skill match.
     */
    val decorationData = Transformations.map(filterSource) { searchTerm ->
        // note: we filter in memory because
        // A) more performance
        // B) dataManager.queryDecorationSearch only filters on name

        val filter = SearchFilter(searchTerm ?: "")
        allDecorationData.filter {
            filter.matches(it.name)
                    || filter.matches(it.skill1Name)
                    || filter.matches(it.skill2Name)
        }
    }

    init {
        setFilter("") // sets filter to blank to trigger initial load
    }

    /**
     * Sets the filter value for the decoration list view model,
     */
    fun setFilter(filter: String) {
        filterSource.value = filter
    }
}