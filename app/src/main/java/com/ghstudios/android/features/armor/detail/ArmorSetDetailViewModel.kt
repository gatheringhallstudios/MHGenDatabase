package com.ghstudios.android.features.armor.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.classes.meta.ArmorMetadata
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.util.loggedThread
import com.ghstudios.android.util.toList

/**
 * A viewmodel binded to the activity containing all armor pieces,
 * as well as the armor set summary itself.
 *
 * The armor piece tabs each have their own ArmorDetailViewModel independent of this one.
 * TODO: They shouldn't, they should all use the same viewmodel to save data. Consider refactor.
 */
class ArmorSetDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get()

    var familyId = -1L
        private set

    val familyName get() = metadata.firstOrNull()?.familyName ?: ""

    private var armorId = -1L
    lateinit var metadata: List<ArmorMetadata>

    /**
     * A livedata containing all armor in this set paired with their piece skills
     */
    var armors = MutableLiveData<List<ArmorSkillPoints>>()

    /**
     * A livedata containing all skills used by this set
     */
    var setSkills = MutableLiveData<List<SkillTreePoints>>()

    /**
     * A livedata containing all setComponents required to craft this set
     */
    var setComponents = MutableLiveData<List<Component>>()

    /**
     * Initialize this viewmodel using an armor set's family.
     * Used when navigating to an armor set.
     */
    fun initByFamily(familyId: Long): List<ArmorMetadata> {
        if (this.familyId == familyId) {
            return metadata
        }

        this.metadata = dataManager.getArmorSetMetadataByFamily(familyId)
        this.familyId = familyId

        loadArmorData()
        return metadata
    }

    /**
     * Initialize this viewmodel using an armor id, using that armor to retrieve the family.
     * Used when navigating to a piece of armor.
     */
    fun initByArmor(armorId: Long): List<ArmorMetadata> {
        if (this.armorId == armorId) {
            return metadata
        }

        this.metadata = dataManager.getArmorSetMetadataByArmor(armorId)
        this.familyId = metadata.firstOrNull()?.family ?: -1L

        loadArmorData()
        return metadata
    }

    /**
     * Internal helper that starts the data loading.
     * Exists because there are multiple ways to load an armor set
     */
    private fun loadArmorData(){
        loggedThread("ArmorFamily Data") {
            val family = metadata.first().family

            // load prerequisite armor/skill data
            val armorPieces = dataManager.getArmorByFamily(family)
            val skillsByArmor = dataManager.queryItemToSkillTreeArrayByArmorFamily(family)

            // Populate armor/skill pairs
            val armorSkillPairs = armorPieces.map {
                ArmorSkillPoints(armor = it, skills = skillsByArmor[it.id] ?: emptyList())
            }

            // Send the armor pieces so that the fragment gets it
            armors.postValue(armorSkillPairs)

            // Calculating skill totals to make a flat list of skill totals
            val allSkillsUnmerged = skillsByArmor.values.flatten()
            val mergedSkills = allSkillsUnmerged.groupBy { it.skillTree.id }.map {
                val skillsToMerge = it.value
                val points = skillsToMerge.sumBy { it.points }
                SkillTreePoints(skillsToMerge.first().skillTree, points)
            }

            // Populate set skill total results so the fragment gets it
            setSkills.postValue(mergedSkills)

            // Load components and send to the fragment
            setComponents.postValue(dataManager.queryComponentCreateByArmorFamily(family).toList { it.component })

        }
    }
}