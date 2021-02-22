package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.app.AlertDialog
import androidx.lifecycle.Observer
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.features.armorsetbuilder.list.ASBSetListFragment
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.features.armorsetbuilder.list.ASBSetAddDialogFragment
import com.ghstudios.android.features.armorsetbuilder.list.ASBSetListPagerActivity

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
        const val REQUEST_CODE_ADD_TO_WISHLIST = 543

        const val REQUEST_CODE_SET_EDIT = 550
    }

    val viewModel by lazy {
        ViewModelProvider(this).get(ASBDetailViewModel::class.java)
    }

    override fun onAddTabs(tabs: BasePagerActivity.TabAdder) {
        val asbId = intent.getLongExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, -1)

        try {
            viewModel.loadSession(asbId)
            viewModel.sessionData.observe(this, Observer {
                title = viewModel.session.name
            })

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_asb, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_to_wishlist -> {
                val fm = supportFragmentManager
                val dialog = ASBAddToWishlistDialog()
                dialog.setTargetFragment(null, REQUEST_CODE_ADD_TO_WISHLIST)
                dialog.show(fm, "create_wishlist")
                return true
            }
            R.id.asb_edit -> {
                val set = viewModel.session
                val dialog = ASBSetAddDialogFragment.newInstance(set)
                dialog.setTargetFragment(null, REQUEST_CODE_SET_EDIT)
                dialog.show(supportFragmentManager, ASBSetListFragment.DIALOG_ADD_ASB_SET)

                return true
            }
            R.id.asb_delete -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.asb_dialog_title_delete_set)
                        .setMessage(getString(R.string.dialog_message_delete, viewModel.session.name))
                        .setPositiveButton(R.string.delete) { _, _ ->
                            viewModel.deleteSet()

                            val intent = Intent(this, ASBSetListPagerActivity::class.java)
                            startActivity(intent)
                            this.finish()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null)
            return

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD_TO_WISHLIST -> {
                    val name = data.getStringExtra(ASBAddToWishlistDialog.EXTRA_NAME)
                    if (!name.isNullOrEmpty()) {
                        viewModel.addToNewWishlist(name) {
                            val message = getString(R.string.wishlist_created, name)
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Executed after the edit dialog completes
                REQUEST_CODE_SET_EDIT -> {
                    val name = data.getStringExtra(ASBSetListFragment.EXTRA_ASB_SET_NAME) ?: ""
                    val rank = data.getIntExtra(ASBSetListFragment.EXTRA_ASB_SET_RANK, -1)
                    val hunterType = data.getIntExtra(ASBSetListFragment.EXTRA_ASB_SET_HUNTER_TYPE, -1)
                    viewModel.updateSet(name, Rank.from(rank), hunterType)
                }
            }
        }
    }
}
