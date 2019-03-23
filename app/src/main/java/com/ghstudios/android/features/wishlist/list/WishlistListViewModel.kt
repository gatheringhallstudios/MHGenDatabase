package com.ghstudios.android.features.wishlist.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.util.loggedThread

/**
 * Viewmodel storing data for the wishlists.
 */
class WishlistListViewModel: ViewModel() {
    val wishlistManager = DataManager.get().wishlistManager
    val wishlistData = MutableLiveData<List<Wishlist>>()

    init { reload() }

    fun reload() {
        loggedThread("Reload Wishlists") {
            wishlistData.postValue(wishlistManager.getWishlists())
        }
    }
}