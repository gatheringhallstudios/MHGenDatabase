package com.ghstudios.android.features.items.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.MHUtils
import com.ghstudios.android.data.classes.Combining
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.loggedThread
import com.ghstudios.android.toList

data class ItemUsage(
        val combinations: List<Combining>,
        val crafting: List<Component>
)

class ItemDetailViewModel(app: Application): AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    val itemData = MutableLiveData<Item>()
    val craftData = MutableLiveData<List<Combining>>()
    val usageData = MutableLiveData<ItemUsage>()

    // live data used to create cursors
    // this will most likely go away and is here because of incremental refactor
    val monsterRewardsData
        get() = MHUtils.createLiveData { dataManager.queryHuntingRewardItem(itemId) }

    val questRewardsData
        get() = MHUtils.createLiveData { dataManager.queryQuestRewardItem(itemId) }

    val gatherData
        get() = MHUtils.createLiveData { dataManager.queryGatheringItem(itemId) }

    var itemId = -1L
        private set

    /**
     * If the item id is unique, loads item data.
     */
    fun setItem(itemId: Long) {
        if (this.itemId == itemId) return

        this.itemId = itemId

        loggedThread(name="Item Loading") {
            itemData.postValue(dataManager.getItem(itemId))

            // query crafting usage
            val craftUsage = dataManager.queryComponentComponent(itemId).toList {
                it.component
            }

            // query combination usage
            val combiningResults = dataManager.queryCombiningOnItemID(itemId).toList {
                it.combining
            }

            // Combinations that result in this item are added to the craft list
            craftData.postValue(combiningResults.filter { it.createdItem.id == itemId })

            usageData.postValue(ItemUsage(
                    combinations = combiningResults.filter { it.createdItem.id != itemId },
                    crafting = craftUsage
            ))

        }
    }
}