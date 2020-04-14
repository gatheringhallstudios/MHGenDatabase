package com.ghstudios.android.features.armorsetbuilder.talismans

import androidx.fragment.app.Fragment
import android.view.Menu
import com.ghstudios.android.GenericActivity
import com.ghstudios.android.MenuSection

class TalismanSelectActivity : GenericActivity() {
    override fun createFragment(): androidx.fragment.app.Fragment {
        return ASBTalismanListFragment.newInstance(select=true)
    }

    companion object {
        const val EXTRA_TALISMAN = "extra_talisman"
    }

    override fun getSelectedSection() = MenuSection.ARMOR_SET_BUILDER

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // don't inflate menu - removes search icon
        return true
    }
}