package com.ghstudios.android.features.wishlist.external

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.toList

// sealed classes used to return a result on a wishlist add operation
sealed class WishlistResult
data class WishlistSuccessResult(val wishlistName: String): WishlistResult()
data class WishlistErrorResult(val message: String): WishlistResult()

/**
 * ViewModel for the Wishlist dialog.
 * Unlike normal viewmodels, this one loads data synchronously
 */
class WishlistAddItemViewModel(private val app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()
    private val wishlistManager = dataManager.wishlistManager

    val allWishlists = MutableLiveData<List<Wishlist>>()

    var itemType: WishlistItemType = WishlistItemType.ITEM
        private set

    var itemId: Long = -1
        private set

    val itemPaths = MutableLiveData<List<String>>()

    fun loadWishlists() {
        allWishlists.value = wishlistManager.getWishlists()
    }

    fun setItem(type: WishlistItemType, itemId: Long) {
        // validation, show error in console
        if (itemId < 0) {
            Log.e(javaClass.simpleName, "Item added to wishlist is -1, this means there was an error")
            // todo: show a different error dialog message instead?
            return
        }

        this.itemType = type
        this.itemId = itemId

        // todo: localize paths
        itemPaths.value = when (itemType) {
            WishlistItemType.ITEM -> dataManager.queryComponentCreateImprove(itemId)
            WishlistItemType.ARMORSET -> listOf("Create")
        }
    }

    fun addToWishlist(newWishlistName: String?, quantity: Int?, path: String): WishlistResult {
        val errorMessage = validateQuantityAndPath(quantity, path)
        if (errorMessage != null) {
            return WishlistErrorResult(errorMessage)
        }

        if (newWishlistName.isNullOrEmpty()) {
            return WishlistErrorResult(app.getString(R.string.wishlist_error_name_required))
        }

        val newWishlistId = wishlistManager.addWishlist(newWishlistName.trim())
        return addToWishlistRaw(newWishlistId, quantity!!, path)
    }

    fun addToWishlist(wishlistIdx: Int, quantity: Int?, path: String): WishlistResult {
        val errorMessage = validateQuantityAndPath(quantity, path)
        if (errorMessage != null) {
            return WishlistErrorResult(errorMessage)
        }

        val wishlist = allWishlists.value?.get(wishlistIdx)
            ?: return WishlistErrorResult("INVALID WISHLIST: Unexpected Error")

        return addToWishlistRaw(wishlist.id, quantity!!, path)
    }

    // adds to wishlist, zero validation
    private fun addToWishlistRaw(wishlistId: Long, quantity: Int, path: String): WishlistResult {
        // Add to wishlist
        when (itemType) {
            WishlistItemType.ITEM ->
                wishlistManager.addWishlistItem(wishlistId, itemId, quantity, path)
            WishlistItemType.ARMORSET ->
                wishlistManager.addWishlistArmorFamily(wishlistId, itemId, quantity)
        }

        // load the wishlist (it may have just been created for all we know...)
        val wishlist = wishlistManager.getWishlist(wishlistId)
        return WishlistSuccessResult(wishlist?.name ?: "")
    }

    /**
     * Performs validation on a potential result set.
     * Returns a string on failure, null on success
     */
    private fun validateQuantityAndPath(quantity: Int?, path: String?): String? {
        var error: String? = null

        if (quantity == null) {
            error = app.getString(R.string.wishlist_error_quantity_required)
        } else if (quantity < 0 || quantity > 99) {
            error = app.getString(R.string.wishlist_error_quantity_invalid)
        } else if (path == null || itemPaths.value?.contains(path) != true) {
            // the below should NEVER HAPPEN,
            // if it does the requirements changed or its a bug
            error = "Path is required (Unexpected Error)"
        }

        return error
    }
}