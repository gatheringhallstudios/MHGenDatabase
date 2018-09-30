package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ArmorSet
import com.ghstudios.android.data.classes.Rank
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

class ArmorSelectViewModel(private val app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    val allArmorData = MutableLiveData<List<ArmorGroup>>()

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
        }
    }
}