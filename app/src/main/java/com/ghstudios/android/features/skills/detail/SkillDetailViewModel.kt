package com.ghstudios.android.features.skills.detail

import android.arch.lifecycle.*
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.classes.SkillTree
import com.ghstudios.android.util.loggedThread

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
    val skillTreeData = MutableLiveData<SkillTree>()
    val armorSkillPoints = MutableLiveData<List<ItemToSkillTree>>()

    private val armorSkillPointsByPart: Map<String, LiveData<List<ItemToSkillTree>>>

    init {
        armorSkillPointsByPart = mapOf(
                Armor.ARMOR_SLOT_HEAD to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_HEAD),
                Armor.ARMOR_SLOT_BODY to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_BODY),
                Armor.ARMOR_SLOT_ARMS to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_ARMS),
                Armor.ARMOR_SLOT_WAIST to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_WAIST),
                Armor.ARMOR_SLOT_LEGS to filterArmorSkillPoints(armorSkillPoints, Armor.ARMOR_SLOT_LEGS)
        )
    }


    private var skillTreeId = -1L

    fun setSkillTreeId(skillTreeId: Long) {
        if (this.skillTreeId == skillTreeId) {
            return
        }

        this.skillTreeId = skillTreeId

        loggedThread("Skill Detail Loading") {
            skillTreeData.postValue(dataManager.getSkillTree(skillTreeId))
            armorSkillPoints.postValue(dataManager.queryArmorSkillTreePointsBySkillTree(skillTreeId))
        }
    }

    fun observeArmorsWithSkill(owner: LifecycleOwner, armorSlot: String, observer: Observer<List<ItemToSkillTree>>) {
        if (armorSlot !in armorSkillPointsByPart) {
            Log.e(this.javaClass.name, "$armorSlot isn't a valid armor slot name")
            return
        }

        armorSkillPointsByPart[armorSlot]!!.observe(owner, observer)
    }
}