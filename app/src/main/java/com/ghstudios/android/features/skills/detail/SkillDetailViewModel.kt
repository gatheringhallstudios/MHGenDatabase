package com.ghstudios.android.features.skills.detail

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.SkillTree
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

private fun filterArmorSkillPoints(data: LiveData<List<ItemToSkillTree>>, slot: String): LiveData<List<ItemToSkillTree>> {
    return Transformations.map(data) { entries ->
        entries.filter { (it.item as Armor).slot == slot}
    }
}

/**
 * View Model that contains
 */
class SkillDetailViewModel: ViewModel() {
    private val dataManager = DataManager.get()
    private var skillTreeId = -1L

    /**
     * Contains the base information for armor skill points.
     * Copied into the armorSkillPoints based on existing filters.
     */
    private val armorSkillPointsBase = mutableListOf<ItemToSkillTree>()
    private val decorationSkillPointsBase = mutableListOf<ItemToSkillTree>()

    val skillTreeData = MutableLiveData<SkillTree>()
    val decorationSkillPoints = MutableLiveData<List<ItemToSkillTree>>()
    val armorSkillPoints = MutableLiveData<List<ItemToSkillTree>>()

    private val armorSkillPointsByPart = mapOf(
            Armor.ARMOR_SLOT_HEAD to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_HEAD),
            Armor.ARMOR_SLOT_BODY to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_BODY),
            Armor.ARMOR_SLOT_ARMS to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_ARMS),
            Armor.ARMOR_SLOT_WAIST to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_WAIST),
            Armor.ARMOR_SLOT_LEGS to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_LEGS)
    )

    fun setSkillTreeId(skillTreeId: Long, showPenalties: Boolean) {
        if (this.skillTreeId == skillTreeId) {
            return
        }

        this.skillTreeId = skillTreeId

        loggedThread("Skill Detail Loading") {
            skillTreeData.postValue(dataManager.getSkillTree(skillTreeId))

            // load decorations. Positive entries before negative ones
            val loadedDecorations = dataManager.queryItemToSkillTreeSkillTree(skillTreeId, ItemType.DECORATION).toList { it.itemToSkillTree }
            decorationSkillPointsBase.addAll(loadedDecorations.filter { it.points >= 0 })
            decorationSkillPointsBase.addAll(loadedDecorations.filter { it.points < 0 })

            // load armors
            armorSkillPointsBase.addAll(dataManager.queryArmorSkillTreePointsBySkillTree(skillTreeId))

            // Note: Calling this method populates the live data
            setShowPenalties(showPenalties)
        }
    }

    /**
     * Sets whether items with penalties should be listed, and updates the mutable live data with correct values
     */
    fun setShowPenalties(showPenalties: Boolean) {
        decorationSkillPoints.postValue(when (showPenalties) {
            true -> decorationSkillPointsBase
            false -> decorationSkillPointsBase.filter { it.points > 0 }
        })

        armorSkillPoints.postValue(when (showPenalties) {
            true -> armorSkillPointsBase
            false -> armorSkillPointsBase.filter { it.points > 0 }
        })
    }

    fun observeArmorsWithSkill(owner: LifecycleOwner, armorSlot: String, observer: Observer<List<ItemToSkillTree>>) {
        if (armorSlot !in armorSkillPointsByPart) {
            Log.e(this.javaClass.name, "$armorSlot isn't a valid armor slot name")
            return
        }

        armorSkillPointsByPart[armorSlot]!!.observe(owner, observer)
    }
}