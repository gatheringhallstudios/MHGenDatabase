package com.ghstudios.android.features.wishlist.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.util.UndoableOperation
import com.ghstudios.android.util.loggedThread

/**
 * Viewmodel storing data for the wishlists.
 */
class WishlistListViewModel: ViewModel() {
    val wishlistManager = DataManager.get().wishlistManager

    private var previousDelete: UndoableOperation? = null
    val wishlistData = MutableLiveData<List<Wishlist>>()

    init {
        reload()
    }

    /**
     * Reloads the list and completes any pending delete operations
     */
    fun reload() {
        previousDelete?.complete()
        loggedThread("Reload Wishlists") {
            wishlistData.postValue(wishlistManager.getWishlists())
        }
    }

    fun startDeleteWishlist(wishlistId: Long): UndoableOperation {
        val previousData = wishlistData.value ?: emptyList()
        wishlistData.value = previousData.filter { it.id != wishlistId }

        val operation = UndoableOperation(
                onComplete = { deleteWishlist(wishlistId) },
                onUndo = { wishlistData.value = previousData }
        )
        previousDelete = operation
        return operation
    }

    /**
     * Deletes a wishlist and reloads the list.
     */
    fun deleteWishlist(wishlistId: Long) {
        wishlistManager.deleteWishlist(wishlistId)
    }
}