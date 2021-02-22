package com.ghstudios.android.features.wishlist.detail

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.features.wishlist.list.WishlistListActivity
import com.ghstudios.android.features.wishlist.list.WishlistListFragment

class WishlistDetailPagerActivity: BasePagerActivity() {

    companion object {
        /**
         * A key for passing a wishlist ID as a long
         */
        const val EXTRA_WISHLIST_ID = "com.daviancorp.android.android.ui.detail.wishlist_id"

        private val REQUEST_REFRESH = 0


        val EXTRA_DETAIL_REFRESH = "com.daviancorp.android.ui.general.wishlist_detail_refresh"

        private val ARG_ID = "ID"

        const val DIALOG_WISHLIST_DATA_EDIT = "wishlist_data_edit"
        const val DIALOG_WISHLIST_DATA_DELETE = "wishlist_data_delete"
        const val REQUEST_EDIT = 1
        const val REQUEST_DELETE = 2
        const val REQUEST_WISHLIST_DATA_DELETE = 10

        private val TAG = "WishlistDataFragment"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WishlistDetailViewModel::class.java)
    }

    override fun onAddTabs(tabs: BasePagerActivity.TabAdder) {
        // Load data into the model
        val id = intent.getLongExtra(EXTRA_WISHLIST_ID, -1)
        viewModel.loadData(id)

        // Listen for title changes
        viewModel.wishlist.observe(this, Observer {
            title = it?.name ?: ""
        })

        // Set tabs
        tabs.addTab(R.string.wishlist_tab_wishlist) { WishlistDataDetailFragment() }
        tabs.addTab(R.string.wishlist_tab_materials) { WishlistDataComponentFragment() }
    }

    override fun getSelectedSection(): Int {
        return MenuSection.WISH_LISTS
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_wishlist_details, menu)
        return true
    }

    private fun updateUI() {
        viewModel.reload()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val fm = supportFragmentManager

        val wishlistId = viewModel.wishlistId
        val name = viewModel.wishlist.value?.name

        when (item.itemId) {
            // Launch Rename Wishlist dialog
            R.id.wishlist_rename -> {
                val dialog = WishlistRenameDialogFragment.newInstance(wishlistId, name)
                dialog.setTargetFragment(null, WishlistListFragment.REQUEST_RENAME)
                dialog.show(fm, WishlistListFragment.DIALOG_WISHLIST_RENAME)
                return true
            }

            // Launch Delete Wishlist dialog
            R.id.wishlist_delete -> {
                val dialog = WishlistDeleteDialogFragment.newInstance(wishlistId, name)
                dialog.setTargetFragment(null, WishlistListFragment.REQUEST_DELETE)
                dialog.show(fm, WishlistListFragment.DIALOG_WISHLIST_DELETE)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Return nothing if result is failed
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            // After wishlist is renamed
            WishlistListFragment.REQUEST_RENAME -> {
                if (data!!.getBooleanExtra(WishlistRenameDialogFragment.EXTRA_RENAME, false)) {
                    updateUI()
                }
                return
            }

            // After wishlist is deleted
            WishlistListFragment.REQUEST_DELETE -> {
                if (data!!.getBooleanExtra(WishlistDeleteDialogFragment.EXTRA_DELETE, false)) {
                    // Exit current activity
                    val intent = Intent(this, WishlistListActivity::class.java)
                    startActivity(intent)
                    this.finish()
                }
                return
            }

            REQUEST_WISHLIST_DATA_DELETE -> updateUI()
        }
    }
}
