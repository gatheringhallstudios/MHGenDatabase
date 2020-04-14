package com.ghstudios.android.features.items.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.util.toList

class ItemListViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    /**
     * LiveData containing the current value of the filter.
     * The item list derives off this to load the query.
     */
    private val filterSource = MutableLiveData<String>()

    val itemData = Transformations.map(filterSource) { filter ->
        dataManager.queryBasicItems(filter?.trim() ?: "").toList { it.item }
    }

    init {
        setFilter("") // sets filter to blank to trigger initial load
    }

    /**
     * Sets the filter value for the item list view model,
     */
    fun setFilter(filter: String) {
        filterSource.value = filter.trim()
    }
}