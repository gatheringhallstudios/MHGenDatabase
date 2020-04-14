package com.ghstudios.android.features.wishlist.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.data.classes.WishlistData
import com.ghstudios.android.util.loggedThread

/**
 * Viewmodel used to load and calculate information for a wishlist detail.
 */
class WishlistDetailViewModel: ViewModel() {
    private val dataManager = DataManager.get()
    private val wishlistManager = dataManager.wishlistManager

    var wishlistId: Long = -1
        private set

    val wishlist = MutableLiveData<Wishlist>()
    val wishlistItems = MutableLiveData<List<WishlistData>>()
    val wishlistComponents = MutableLiveData<List<WishlistComponent>>()

    /**
     * Stores the total price required to craft all items in this wishlist
     */
    val priceData = MutableLiveData<Int>()

    fun loadData(wishlistId: Long) {
        if (this.wishlistId == wishlistId) {
            return
        }

        this.wishlistId = wishlistId
        reload()
    }

    fun reload() {
        if (wishlistId < 0) {
            return
        }

        loggedThread(name="Load Wishlist Detail") {
            wishlist.postValue(wishlistManager.getWishlist(wishlistId))

            val items = wishlistManager.getWishlistItems(wishlistId)
            wishlistItems.postValue(items)
            wishlistComponents.postValue(wishlistManager.getWishlistComponents(wishlistId))
            priceData.postValue(wishlistManager.calculateWishlistPrice(items))
        }
    }

    fun updateComponentQuantity(componentId: Long, quantity: Int) {
        wishlistManager.updateComponentQuantity(componentId, quantity)
        wishlistManager.refreshWishlistItemsSatisfied(wishlistId)

        loggedThread(name="Load Wishlist Detail (Partial)") {
            val items = wishlistManager.getWishlistItems(wishlistId)
            wishlistItems.postValue(items)
            wishlistComponents.postValue(wishlistManager.getWishlistComponents(wishlistId))
        }
    }
}