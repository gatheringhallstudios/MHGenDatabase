package com.ghstudios.android.features.armorsetbuilder.list

import android.app.*
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*

import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.adapter.common.SwipeReorderTouchHelper
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.createSnackbarWithUndo

/**
 * Fragment used to display and manage a list of armor sets.
 */
class ASBSetListFragment : RecyclerViewFragment() {
    companion object {
        const val EXTRA_ASB_SET_ID = "com.daviancorp.android.ui.general.asb_set_id"
        const val EXTRA_ASB_SET_NAME = "com.daviancorp.android.ui.general.asb_set_name"
        const val EXTRA_ASB_SET_RANK = "com.daviancorp.android.ui.general.asb_set_rank"
        const val EXTRA_ASB_SET_HUNTER_TYPE = "com.daviancorp.android.ui.general.asb_set_hunter_type"

        const val DIALOG_ADD_ASB_SET = "add_asb_set"

        const val REQUEST_ADD_ASB_SET = 0
        const val REQUEST_EDIT_ASB_SET = 1
    }
    
    private val asbManager = DataManager.get().asbManager

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ASBSetListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableDivider()
        enableFab {
            showAddDialog()
        }

        val adapter = ASBSetAdapter()
        setAdapter(adapter)

        val handler = ItemTouchHelper(SwipeReorderTouchHelper(
                afterSwiped = {
                    val setId = it.itemView.tag as Long
                    val message = getString(R.string.asb_result_set_deleted)
                    val operation = viewModel.startDeleteSet(setId)
                    val containerView = view.findViewById<ViewGroup>(R.id.recyclerview_container_main)

                    containerView.createSnackbarWithUndo(message, operation)
                }
        ))
        handler.attachToRecyclerView(recyclerView)

        viewModel.asbData.observe(this, Observer {
            if (it == null) return@Observer

            adapter.setItems(it)
            showEmptyView(show = it.isEmpty())
        })
    }

    fun showAddDialog() {
        val dialog = ASBSetAddDialogFragment.newInstance()
        dialog.setTargetFragment(this, REQUEST_ADD_ASB_SET)
        dialog.show(fragmentManager, DIALOG_ADD_ASB_SET)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_ADD_ASB_SET -> {
                    val name = data!!.getStringExtra(EXTRA_ASB_SET_NAME)
                    val rank = data.getIntExtra(EXTRA_ASB_SET_RANK, -1)
                    val hunterType = data.getIntExtra(EXTRA_ASB_SET_HUNTER_TYPE, -1)
                    viewModel.addSet(name, Rank.from(rank), hunterType)
                }

                REQUEST_EDIT_ASB_SET -> {
                    val id = data!!.getLongExtra(EXTRA_ASB_SET_ID, -1)
                    val name = data.getStringExtra(EXTRA_ASB_SET_NAME)
                    val rank = data.getIntExtra(EXTRA_ASB_SET_RANK, -1)
                    val hunterType = data.getIntExtra(EXTRA_ASB_SET_HUNTER_TYPE, -1)
                    viewModel.updateASBSet(id, name, Rank.from(rank), hunterType)
                }
            }
        }
    }

    // Initiates an edit. Currently unused.
    fun initiateEdit(set: ASBSet) {
        val fm = fragmentManager
        val dialog = ASBSetAddDialogFragment.newInstance(
                set.id,
                set.name,
                set.rank,
                set.hunterType
        )
        dialog.setTargetFragment(this@ASBSetListFragment, REQUEST_EDIT_ASB_SET)
        dialog.show(fm!!, DIALOG_ADD_ASB_SET)
    }

    // Initiates a copy. Currently unused. Needs to use the viewmodel
    fun initiateCopy(set: ASBSet) {
        // Create dialog
        val b = AlertDialog.Builder(activity)
                .setPositiveButton(R.string.copy) { dialog: DialogInterface, which: Int ->
                    asbManager.copyASB(set.id)
                    updateUI()
                }
                .setNegativeButton(android.R.string.cancel, null)

        val firstName = set.name
        b.setMessage(resources.getString(R.string.dialog_message_copy, firstName))
                .setTitle(R.string.asb_dialog_title_copy_set)

        val dialog = b.create()
        dialog.show()
    }

    private fun updateUI() {
        viewModel.reload()
    }
}
