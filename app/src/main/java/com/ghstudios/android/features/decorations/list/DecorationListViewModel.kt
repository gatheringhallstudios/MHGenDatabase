package com.ghstudios.android.features.decorations.list

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.toList

class DecorationListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    private val filterSource = MutableLiveData<String>()

    val decorationData = Transformations.map(filterSource) { filter ->
        // Query the list of decorations. Null/empty strings are handled
        dataManager.queryDecorationsSearch(filter).toList { it.decoration }
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