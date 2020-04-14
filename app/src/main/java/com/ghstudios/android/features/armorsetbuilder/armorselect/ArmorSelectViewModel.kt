package com.ghstudios.android.features.armorsetbuilder.armorselect

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.util.SearchFilter
import com.ghstudios.android.util.loggedThread


// todo: move somewhere else, have the ASB handle the mapping instead
fun getSlotForPieceIndex(pieceIndex: Int) = when (pieceIndex) {
    ArmorSet.HEAD -> Armor.ARMOR_SLOT_HEAD
    ArmorSet.BODY -> Armor.ARMOR_SLOT_BODY
    ArmorSet.ARMS -> Armor.ARMOR_SLOT_ARMS
    ArmorSet.WAIST -> Armor.ARMOR_SLOT_WAIST
    ArmorSet.LEGS -> Armor.ARMOR_SLOT_LEGS
    else -> ""
}

class ArmorSelectViewModel : ViewModel() {
    private val dataManager = DataManager.get()

    private val filterValue = MutableLiveData<String>()

    /**
     * LiveData containing all available armor pieces grouped by rarity
     * for a particular rank and hunter type.
     */
    val allArmorData = MutableLiveData<List<ArmorGroup>>()

    /**
     * LiveData containing all armor pieces that pass the filter.
     */
    val filteredArmor = Transformations.switchMap(filterValue) { filterStr ->
        if (filterStr.isNullOrBlank()) {
            return@switchMap allArmorData
        }

        val filter = SearchFilter(filterStr)
        Transformations.map(allArmorData) { armorGroups ->
            armorGroups.mapNotNull { group ->
                val filteredPieces = group.armor.filter {
                    filter.matches(it.armor.name) || it.skills.any {s -> filter.matches(s.skillTree.name)}
                }

                // Return the armor group...if there are armor pieces
                when {
                    filteredPieces.isEmpty() -> null
                    else -> ArmorGroup(group.rarity, filteredPieces)
                }
            }
        }
    }

    val armorWishlistData = MutableLiveData<List<ArmorSkillPoints>>()

    fun initialize(asbIndex: Int, rankFilter: Rank, hunterType: Int) {
        val armorSlot = getSlotForPieceIndex(asbIndex)
        loggedThread("Armor Select armor loading") {
            // Head pieces should see all head pieces, passthrough if not a head piece
            val type = when (armorSlot) {
                Armor.ARMOR_SLOT_HEAD -> Armor.ARMOR_TYPE_BOTH
                else -> hunterType
            }

            val armorSkillPoints = dataManager.queryArmorSkillPointsByType(armorSlot, type)
            val allArmorItems = armorSkillPoints
                    .groupBy { it.armor.rarity }.toSortedMap().map {
                        val rarity = it.key
                        val armorPieces = it.value
                        ArmorGroup(rarity, armorPieces)
                    }.filter {
                        // filter results based on rankFilter input
                        // deviants armor is always returned.
                        if (rankFilter == Rank.ANY || it.rarity == 11) {
                            return@filter true
                        }

                        val armorRank = Rank.fromArmorRarity(it.rarity)
                        armorRank == rankFilter
                    }

            allArmorData.postValue(allArmorItems)
            filterValue.postValue("")

            // load wishlist data
            val wishlistArmorIds = mutableSetOf<Long>()
            val wishlistManager = dataManager.wishlistManager
            for (wishlist in wishlistManager.getWishlists()) {
                for (data in wishlistManager.getWishlistItems(wishlist.id)) {
                    if (data.item.type == ItemType.ARMOR) {
                        wishlistArmorIds.add(data.item.id)
                    }
                }
            }

            // Now go over the armor pieces loaded above, only taking those existing in wishlists.
            // This creates an interesection of filter armors and wishlist armors
            val wishlistArmors = allArmorItems.asSequence()
                    .flatMap { it.armor.asSequence() }
                    .filter { it.armor.id in wishlistArmorIds }
                    .toList()
            armorWishlistData.postValue(wishlistArmors)
        }
    }

    fun setFilter(filterStr: String) {
        filterValue.value = filterStr
    }
}