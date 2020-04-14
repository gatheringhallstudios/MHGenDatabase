package com.ghstudios.android.features.skills.detail

import androidx.lifecycle.Observer
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.AppSettings

import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.mhgendatabase.R

class SkillTreeDetailPagerActivity : BasePagerActivity() {

    companion object {
        /**
         * A key for passing a monster ID as a long
         */
        const val EXTRA_SKILLTREE_ID = "com.daviancorp.android.android.ui.detail.skill_id"
    }

    /**
     * Viewmodel for the entirity of this skill detail, including sub fragments
     */
    private val viewModel by lazy {
        ViewModelProvider(this).get(SkillDetailViewModel::class.java)
    }

    override fun onAddTabs(tabs: BasePagerActivity.TabAdder) {
        val skillTreeId = intent.getLongExtra(EXTRA_SKILLTREE_ID, -1)

        viewModel.setSkillTreeId(skillTreeId, showPenalties=AppSettings.showSkillPenalties)

        viewModel.skillTreeData.observe(this, Observer { data ->
            if (data != null) {
                this.title = data.name
            }
        })

        tabs.addTab(R.string.skill_tab_detail) {
            SkillTreeDetailFragment.newInstance(skillTreeId)
        }

        tabs.addTab(R.string.type_decoration) {
            SkillTreeDecorationFragment.newInstance(skillTreeId)
        }

        tabs.addTab(R.string.skill_tab_head) {
            SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_HEAD)
        }

        tabs.addTab(R.string.skill_tab_body) {
            SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_BODY)
        }

        tabs.addTab(R.string.skill_tab_arms) {
            SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_ARMS)
        }

        tabs.addTab(R.string.skill_tab_waist) {
            SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_WAIST)
        }

        tabs.addTab(R.string.skill_tab_legs) {
            SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_LEGS)
        }
    }

    override fun getSelectedSection(): Int {
        return MenuSection.SKILL_TREES
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu) // will inflate global actions like search

        menuInflater.inflate(R.menu.menu_skill_detail, menu)
        menu.findItem(R.id.show_negative).isChecked = AppSettings.showSkillPenalties
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle superclass cases first. If handled, return immediately
        val handled = super.onOptionsItemSelected(item)
        if (handled) {
            return true
        }

        return when (item.itemId) {
            R.id.show_negative -> {
                val newSetting = !AppSettings.showSkillPenalties
                AppSettings.showSkillPenalties = newSetting
                viewModel.setShowPenalties(newSetting)
                item.isChecked = newSetting
                true
            }
            else -> false
        }
    }
}
