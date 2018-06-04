package com.ghstudios.android.features.items

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.database.DataManager

class ItemDetailViewModel(app: Application): AndroidViewModel(app) {
    val dataManager = DataManager.get(app.applicationContext)

    val itemData = MutableLiveData<Item>()


    var itemId = -1L
        private set

    /**
     * If the item id is unique, loads item data.
     */
    fun setItem(itemId: Long) {
        if (this.itemId == itemId) return

        this.itemId = itemId

        Thread {
            itemData.postValue(dataManager.getItem(itemId))
        }.start()
    }
}