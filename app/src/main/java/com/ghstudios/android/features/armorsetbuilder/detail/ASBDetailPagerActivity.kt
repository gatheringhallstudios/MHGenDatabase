package com.ghstudios.android.features.armorsetbuilder.detail

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.features.armorsetbuilder.list.ASBSetListFragment
import com.ghstudios.android.loader.ASBSessionLoader
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection

import java.util.ArrayList

class ASBDetailPagerActivity : BasePagerActivity() {
    companion object {

        const val EXTRA_FROM_SET_BUILDER = "com.daviancorp.android.ui.detail.from_set_builder"
        const val EXTRA_FROM_TALISMAN_EDITOR = "com.daviancorp.android.ui.detail.from_talisman_editor"
        const val EXTRA_TALISMAN_SKILL_INDEX = "com.daviancorp.android.ui.detail.talisman_skill_number"
        const val EXTRA_PIECE_INDEX = "com.daviancorp.android.ui.detail.piece_index"
        const val EXTRA_DECORATION_INDEX = "com.daviancorp.android.ui.detail.decoration_index"
        const val EXTRA_DECORATION_MAX_SLOTS = "com.daviancorp.android.ui.detail.decoration_max_slots"
        const val EXTRA_SET_RANK = "com.daviancorp.android.ui.detail.set_rank"
        const val EXTRA_SET_HUNTER_TYPE = "com.daviancorp.android.ui.detail.hunter_type"

        const val EXTRA_TALISMAN_SKILL_TREE_1 = "com.daviancorp.android.ui.detail.skill_tree_1"
        const val EXTRA_TALISMAN_SKILL_POINTS_1 = "com.daviancorp.android.ui.detail.skill_points_1"
        const val EXTRA_TALISMAN_SKILL_TREE_2 = "com.daviancorp.android.ui.detail.skill_tree_2"
        const val EXTRA_TALISMAN_SKILL_POINTS_2 = "com.daviancorp.android.ui.detail.skill_points_2"
        const val EXTRA_TALISMAN_TYPE_INDEX = "com.daviancorp.android.ui.detail.talisman_type_index"
        const val EXTRA_TALISMAN_SLOTS = "com.daviancorp.android.ui.detail.talisman_slots"

        const val REQUEST_CODE_ADD_PIECE = 537
        const val REQUEST_CODE_ADD_DECORATION = 538
        const val REQUEST_CODE_CREATE_TALISMAN = 539
        const val REQUEST_CODE_REMOVE_PIECE = 540
        const val REQUEST_CODE_REMOVE_DECORATION = 541
        const val REQUEST_CODE_SET_WEAPON_SLOTS = 542
    }

    val viewModel by lazy {
        ViewModelProviders.of(this).get(ASBDetailViewModel::class.java)
    }

    override fun onAddTabs(tabs: BasePagerActivity.TabAdder) {
        title = intent.getStringExtra(ASBSetListFragment.EXTRA_ASB_SET_NAME)
        val asbId = intent.getLongExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, -1)

        try {
            viewModel.loadSession(asbId)

            tabs.addTab(R.string.asb_tab_equipment) { ASBFragment() }
            tabs.addTab(R.string.skills) { ASBSkillsListFragment() }

        } catch (ex: Exception) {
            showFatalError()
            Log.e(javaClass.simpleName, "Fatal error loading ASB", ex)
        }
    }

    override fun getSelectedSection(): Int {
        return MenuSection.ARMOR_SET_BUILDER
    }
}
