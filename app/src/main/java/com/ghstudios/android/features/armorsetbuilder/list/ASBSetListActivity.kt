package com.ghstudios.android.features.armorsetbuilder.list

import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.MenuSection
import com.ghstudios.android.features.armorsetbuilder.talismans.ASBTalismanListFragment

/**
 * Main activity for the Armor set list
 */
class ASBSetListActivity : BasePagerActivity() {

    override fun onAddTabs(tabs: TabAdder) {
        setTitle(R.string.activity_asb_sets)

        // Tag as top level activity
        super.setAsTopLevel()

        tabs.addTab(R.string.activity_asb_sets) { ASBSetListFragment() }
        tabs.addTab(R.string.asb_dialog_talisman_title) { ASBTalismanListFragment() }
    }

    override fun getSelectedSection(): Int {
        return MenuSection.ARMOR_SET_BUILDER
    }
}
