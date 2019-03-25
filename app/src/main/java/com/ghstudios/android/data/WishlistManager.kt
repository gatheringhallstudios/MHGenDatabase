package com.ghstudios.android.data

import android.content.Context
import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.data.classes.WishlistData
import com.ghstudios.android.data.database.MonsterHunterDatabaseHelper
import com.ghstudios.android.util.firstOrNull
import com.ghstudios.android.util.toList

// todo: find some way to control recipe types in a standard way
private const val CREATE = "Create"

/**
 * Class used to manipulate wishlists. Grab an instance from the DataManager.
 */
class WishlistManager internal constructor(
    private val mAppContext: Context,
    private val mBaseManager: DataManager,
    private val mHelper: MonsterHunterDatabaseHelper
) {
    val TAG = "WishlistManager"


    /********************************* WISHLIST QUERIES  */
    /* Get a Cursor that has a list of all Wishlists */
    fun getWishlists(): List<Wishlist> {
        return mHelper.queryWishlists().toList { it.wishlist }
    }

    /* Get a specific Wishlist */
    fun getWishlist(id: Long): Wishlist? {
        return mHelper.queryWishlist(id).firstOrNull { it.wishlist }
    }

    /**
     * Get a list of all wishlist items associated with the wishlist
     */
    fun getWishlistItems(id: Long): List<WishlistData> {
        return mHelper.queryWishlistData(id).toList { it.wishlistData }
    }

    /* Get a a list of WishlistComponent based on Wishlist */
    fun getWishlistComponents(id: Long): List<WishlistComponent> {
        return mHelper.queryWishlistComponents(id).toList { it.wishlistComponent }
    }

    /** Add a new Wishlist with a given name and returns the id.  */
    fun addWishlist(name: String): Long {
        return mHelper.queryAddWishlist(name)
    }

    /* Update a specific Wishlist with a new name */
    fun updateWishlistName(id: Long, name: String) {
        mHelper.queryUpdateWishlist(id, name)
    }


    /* Delete a specific Wishlist */
    fun deleteWishlist(id: Long) {
        mHelper.queryDeleteWishlist(id)
    }

    /* Copy a specific Wishlist into a new wishlist, including its entries */
    fun copyWishlist(id: Long, name: String) {
        // Create the new wishlist, get the id
        val newId = mHelper.queryAddWishlist(name)

        // Copy data entries to the new wishlist
        val data = mHelper.queryWishlistData(id).toList { it.wishlistData }
        for (dataEntry in data) {
            mHelper.queryAddWishlistDataAll(newId, dataEntry!!.item.id,
                    dataEntry.quantity, dataEntry.satisfied, dataEntry.path)
        }

        // Copy wishlist components from the old wishlist to the new one
        val components = this.getWishlistComponents(id)
        for (componentEntry in components) {
            mHelper.queryAddWishlistComponentAll(newId, componentEntry.item.id,
                    componentEntry.quantity, componentEntry.notes)
        }
    }

    /********************************* WISHLIST DATA QUERIES  */

    /**
     * Add an entry to a specific wishlist with the given item and quantity
     * Updates the existing entry if it already exists.
     */
    fun addWishlistItem(wishlistId: Long, itemId: Long, quantity: Int, craftMethod: String = CREATE) {
        addWishlistItemBasic(wishlistId, itemId, quantity, craftMethod)
        helperQueryAddWishlistComponents(wishlistId, itemId, quantity, craftMethod)
        refreshWishlistItemsSatisfied(wishlistId)
    }


    /**
     * Adds all armor pieces in an armor family to a wishlist.
     * Updates any existing entries to update quantity.
     */
    fun addWishlistArmorFamily(wishlistId: Long, familyId: Long, quantity: Int) {
        val pieces = mBaseManager.getArmorByFamily(familyId)
        for (armor in pieces) {
            addWishlistItemBasic(wishlistId, armor.id, quantity, CREATE)
            helperQueryAddWishlistComponents(wishlistId, armor.id, quantity, CREATE)
        }

        refreshWishlistItemsSatisfied(wishlistId)
    }

    /**
     * Internal helper to add or update a wishlist item (without any of the usual cascades)
     */
    private fun addWishlistItemBasic(wishlistId: Long, itemId: Long, quantity: Int, path: String) {
        val data = mHelper.queryWishlistData(wishlistId, itemId, path).firstOrNull { it.wishlistData }
        if (data == null) {
            // Add new entry to wishlist_data
            mHelper.queryAddWishlistData(wishlistId, itemId, quantity, path)
        } else {
            // Update existing entry
            val id = data.id
            val total = data.quantity + quantity
            mHelper.queryUpdateWishlistDataQuantity(id, total)
        }
    }

    /**
     * Adds the components of an item to the wishlist components.
     * These insert or update existing wishlist component rows.
     */
    private fun helperQueryAddWishlistComponents(wishlist_id: Long, item_id: Long, quantity: Int, path: String) {
        // Get the components for the entry
        val itemComponents = mHelper.queryComponentCreatedType(item_id, path).toList {
            it.component
        }

        // Add each component to the wishlist component list
        // We first iterate over the components in a recipe
        for (component in itemComponents) {
            val component_id = component.component.id
            val c_amt = component.quantity * quantity

            // now add or update the existing entry in the wishlist components table
            val entry = mHelper.queryWishlistComponent(wishlist_id, component_id)
                    .firstOrNull { it.wishlistComponent }

            if (entry == null) {
                // Add component entry to wishlist_component
                mHelper.queryAddWishlistComponent(wishlist_id, component_id, c_amt)
            } else {
                // Update component entry to wishlist_component
                val newQuantity = entry.quantity + c_amt
                mHelper.queryUpdateWishlistComponentQuantity(entry.id, newQuantity)
            }
        }
    }

    /* Update an entry to the given quantity */
//    fun queryUpdateWishlistData(id: Long, quantity: Int) {
//
//        // Get the existing entry from WishlistData
//        val data = mHelper.queryWishlistDataId(id).firstOrNull { it.wishlistData }
//
//        val wishlist_id = wd!!.wishlistId
//        val item_id = wd.item.id
//        val wd_old_quantity = wd.quantity
//        val path = wd.path
//
//        // Find the different between new and old quantities
//        val diff_quantity = quantity - wd_old_quantity
//
//        // Get the components for the WishlistData entry
//        val cc = mHelper.queryComponentCreatedType(item_id, path)
//        cc.moveToFirst()
//
//        // Update those components in WishlistComponent
//        while (!cc.isAfterLast) {
//            val component_id = cc.component!!.component.id
//            val c_amt = cc.component!!.quantity * diff_quantity
//
//            val wc = mHelper.queryWishlistComponent(wishlist_id, component_id)
//            wc.moveToFirst()
//
//            // Update component entry to wishlist_component
//            val wc_id = wc.wishlistComponent!!.id
//            val old_amt = wc.wishlistComponent!!.quantity
//
//            mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt + c_amt)
//
//            wc.close()
//            cc.moveToNext()
//        }
//        cc.close()
//
//        mHelper.queryUpdateWishlistDataQuantity(id, quantity)
//
//        // Check for any changes if any WishlistData is satisfied (can be build)
//        refreshWishlistItemsSatisfied(wishlist_id)
//    }

    /**
     * Delete an entry from WishlistData
     * */
    fun queryDeleteWishlistData(id: Long) {
        // Get the existing entry from WishlistData
        val wd = mHelper.queryWishlistDataId(id)
                .firstOrNull { it.wishlistData }
                ?: return // Exit if nothing to delete

        val wishlist_id = wd.wishlistId
        val item_id = wd.item.id
        val wd_old_quantity = wd.quantity
        val path = wd.path

        // Get the components for the WishlistData entry
        val components = mHelper.queryComponentCreatedType(item_id, path).toList { it.component }

        // Update those components in WishlistComponent
        for (wishlistComponent in components) {
            val component_id = wishlistComponent.component.id
            val c_amt = wishlistComponent.quantity * wd_old_quantity

            val component = mHelper.queryWishlistComponent(wishlist_id, component_id)
                    .firstOrNull { it.wishlistComponent }
                    ?: continue

            // Update component entry to wishlist_component
            val wc_id = component.id
            val old_amt = component.quantity

            val new_amt = old_amt - c_amt

            if (new_amt > 0) {
                // Update wishlist_component if component is still needed
                mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt - c_amt)
            } else {
                // If component no longer needed, delete it from wishlist_component
                mHelper.queryDeleteWishlistComponent(wc_id)
            }
        }

        mHelper.queryDeleteWishlistData(id)
    }

    fun calculateWishlistPrice(data: List<WishlistData>): Int {
        var total = 0        // total cost

        for (wd in data) {
            val i = wd.item
            val type = wd.path

            // Check path if the entry is a Weapon
            val buy: Int = if (i.type === ItemType.WEAPON) {
                val weapon = mHelper.queryWeapon(i.id)
                        .firstOrNull { it.weapon }

                // Get the cost from the desired creation type
                // or 0 if the weapon is null
                if (type == "Create")
                    weapon?.creationCost
                else {
                    weapon?.upgradeCost
                } ?: 0
            } else {
                wd.item.buy
            }

            // Add the entry cost to total cost
            total += (buy * wd.quantity)
        }
        return total
    }

    /********************************* WISHLIST COMPONENT QUERIES  */


    /**
     * Update the specified WishlistComponent to the given quantity.
     * Does not update whether or not the wishlist items have been satisfied
     * */
    fun updateComponentQuantity(id: Long, qty: Int) {
        mHelper.queryUpdateWishlistComponentNotes(id, qty)
    }

    /**
     * From a specified wishlist id, check if any WishlistData can be built
     * Updates the "satisfied" status of all wishlists.
     * TODO: Perhaps find a way such that "satisfied" isn't required
     */
    fun refreshWishlistItemsSatisfied(wishlistId: Long) {
        val wishlistData = mHelper.queryWishlistData(wishlistId).toList {
            it.wishlistData
        }

        // For every WishlistData
        for (wd in wishlistData) {
            // Starts off as satisfied. May update to unsatisfied depending on materials
            var satisfied = true

            val created_id = wd.item.id
            val path = wd.path

            val components = mHelper.queryComponentCreatedType(created_id, path).toList {
                it.component
            }

            // For every component of the current WishlistData entry
            for (c in components) {
                val component_id = c!!.component.id

                val component = mHelper.queryWishlistComponent(wishlistId, component_id).firstOrNull { it.wishlistComponent }

                // Get the amounts
                val required_amt = c.quantity
                val have_amt = component?.notes ?: 0

                // Check if user does not have enough materials
                if (have_amt < required_amt) {
                    satisfied = false
                    break
                }
            }

            // Update the WishlistData entry
            mHelper.queryUpdateWishlistDataSatisfied(wd.id, satisfied)
        }
    }
}