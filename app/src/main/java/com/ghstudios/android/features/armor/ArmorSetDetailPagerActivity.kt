package com.ghstudios.android.features.armor

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.data.classes.meta.ArmorMetadata
import com.ghstudios.android.data.database.DataManager

class ArmorSetDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val dataManager = DataManager.get(app.applicationContext)

    private var familyId = -1L
    private var armorId = -1L
    lateinit var metadata: List<ArmorMetadata>

    fun initByFamily(familyId: Long): List<ArmorMetadata> {
        if (this.familyId == familyId) {
            return metadata
        }

        metadata = dataManager.getArmorSetMetadataByFamily(familyId)
        return metadata
    }

    fun initByArmor(armorId: Long): List<ArmorMetadata> {
        if (this.armorId == armorId) {
            return metadata
        }

        metadata = dataManager.getArmorSetMetadataByArmor(armorId)
        return metadata
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

        for ((idx, armorData) in metadata.withIndex()) {
            tabs.addTab(armorData.slot) {
                ArmorDetailFragment.newInstance(armorData.id)
            }

            // If this is the armor we came here for, set it as the default tab
            // todo: if adding a set detail fragment, add +1
            if (armorData.id == armorId) {
                tabs.setDefaultItem(idx)
            }
        }
    }
}