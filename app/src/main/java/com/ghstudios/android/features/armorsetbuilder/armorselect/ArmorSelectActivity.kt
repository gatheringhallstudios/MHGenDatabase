package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.GenericActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.data.classes.ArmorSet
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.features.armorsetbuilder.detail.ASBDetailPagerActivity
import com.ghstudios.android.mhgendatabase.R

class ArmorSelectActivity : BasePagerActivity() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ArmorSelectViewModel::class.java)
    }

    override fun onAddTabs(tabs: TabAdder) {
        val asbPieceIndex = intent.getIntExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, -1)
        val rankValue = intent.getSerializableExtra(ASBDetailPagerActivity.EXTRA_SET_RANK) as Rank?
        val hunterType = intent.getIntExtra(ASBDetailPagerActivity.EXTRA_SET_HUNTER_TYPE, -1)

        setTitle(when (asbPieceIndex) {
            ArmorSet.HEAD -> R.string.asb_title_select_head
            ArmorSet.BODY -> R.string.asb_title_select_body
            ArmorSet.ARMS -> R.string.asb_title_select_arms
            ArmorSet.WAIST -> R.string.asb_title_select_waist
            ArmorSet.LEGS -> R.string.asb_title_select_legs
            else -> R.string.asb_title_select_armor
        })

        viewModel.initialize(asbPieceIndex, rankValue ?: Rank.ANY, hunterType)

        tabs.addTab(R.string.asb_title_armor_all) { ArmorSelectAllFragment() }
        tabs.addTab(R.string.wishlist) { ArmorSelectWishlistFragment() }
    }

    override fun getSelectedSection() = MenuSection.ARMOR_SET_BUILDER

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // don't inflate menu - remove search icon
        return true
    }
}