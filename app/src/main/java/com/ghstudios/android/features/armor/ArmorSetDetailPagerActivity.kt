package com.ghstudios.android.features.armor

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.classes.meta.ArmorMetadata
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.loggedThread
import com.ghstudios.android.mhgendatabase.R

class ArmorSetDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    private var familyId = -1L
    private var armorId = -1L
    lateinit var metadata: List<ArmorMetadata>

    var armors = MutableLiveData<List<Armor>>()
    var skills = MutableLiveData<HashMap<Long,List<ItemToSkillTree>>>()

    fun initByFamily(familyId: Long): List<ArmorMetadata> {
        if (this.familyId == familyId) {
            return metadata
        }

        metadata = dataManager.getArmorSetMetadataByFamily(familyId)
        loadArmorData()
        return metadata
    }

    fun initByArmor(armorId: Long): List<ArmorMetadata> {
        if (this.armorId == armorId) {
            return metadata
        }

        metadata = dataManager.getArmorSetMetadataByArmor(armorId)
        loadArmorData()
        return metadata
    }

    private fun loadArmorData(){
        loggedThread("ArmorFamily Data") {
            //Get Armors
            armors.postValue(dataManager.getArmorByFamily(metadata.first().family.toLong()))
            //Get Skills
            skills.postValue(dataManager.queryItemToSkillTreeArrayByArmorFamily(metadata.first().family.toLong()))
        }
    }
}

class ArmorSetDetailPagerActivity : BasePagerActivity() {
    companion object {
        const val EXTRA_ARMOR_ID = "com.daviancorp.android.android.ui.detail.armor_id"
        const val EXTRA_FAMILY_ID = "com.daviancorp.android.android.ui.detail.family_id"
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ArmorSetDetailViewModel::class.java)
    }

    override fun getSelectedSection() = MenuSection.ARMOR

    override fun onAddTabs(tabs: TabAdder) {
        val armorId = intent.getLongExtra(EXTRA_ARMOR_ID, -1)
        val familyId = intent.getLongExtra(EXTRA_FAMILY_ID, -1)

        val metadata = when (familyId > -1) {
            true -> viewModel.initByFamily(familyId)
            false -> viewModel.initByArmor(armorId)
        }

        //TODO: Add summary if > 1
        if(metadata.size > 1){
            tabs.addTab(getString(R.string.summary)){ ArmorSetSummaryFragment() }
        }

        for ((idx, armorData) in metadata.withIndex()) {
            tabs.addTab(armorData.slot) {
                ArmorDetailFragment.newInstance(armorData.id)
            }

            // If this is the armor we came here for, set it as the default tab
            if (armorData.id == armorId) {
                tabs.setDefaultItem(idx + if(metadata.size>1) 1 else 0)
            }
        }
    }
}