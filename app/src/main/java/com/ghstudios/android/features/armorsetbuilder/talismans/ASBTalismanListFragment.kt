package com.ghstudios.android.features.armorsetbuilder.talismans

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.adapter.common.SwipeReorderTouchHelper
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.applyArguments
import com.ghstudios.android.util.createSnackbarWithUndo


/**
 * Fragment used to display a list of talismans
 */
class ASBTalismanListFragment: RecyclerViewFragment() {
    companion object {
        private const val REQUEST_CODE_TALISMAN = 500
        private const val MODE_SELECTION = "SELECTION_MODE"

        @JvmStatic
        fun newInstance(select: Boolean = true): ASBTalismanListFragment {
            return ASBTalismanListFragment().applyArguments {
                putBoolean(MODE_SELECTION, select)
            }
        }
    }

    val viewModel by lazy {
        ViewModelProvider(this).get(ASBTalismanListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isSelecting = arguments?.getBoolean(MODE_SELECTION) == true

        enableDivider()
        enableFab {
            showAddTalismanDialog()
        }

        val adapter = TalismanAdapter(
                onSelect = {
                    if (isSelecting) {
                        val metadata = TalismanMetadata(
                                id = it.id,
                                typeIndex = it.typeIndex,
                                numSlots = it.numSlots,
                                skills = it.skills.map {sp ->
                                    Pair(sp.skillTree.id, sp.points)
                                }
                        )

                        val intent = activity!!.intent
                        intent.putExtra(TalismanSelectActivity.EXTRA_TALISMAN, metadata)
                        activity?.setResult(Activity.RESULT_OK, intent)
                        activity?.finish()
                    } else {
                        showAddTalismanDialog(it)
                    }
                }
        )

        val handler = ItemTouchHelper(SwipeReorderTouchHelper(
                afterSwiped = {
                    val talismanId = it.itemView.tag as Long
                    val message = getString(R.string.asb_result_talisman_deleted)
                    val operation = viewModel.startRemoveTalisman(talismanId)
                    val containerView = view.findViewById<ViewGroup>(R.id.recyclerview_container_main)

                    containerView.createSnackbarWithUndo(message, operation)
                }
        ))

        setAdapter(adapter)
        handler.attachToRecyclerView(recyclerView)

        viewModel.talismanData.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            adapter.setItems(it)
            showEmptyView(show = it.isEmpty())
        })
    }

    fun showAddTalismanDialog(talisman: ASBTalisman? = null) {
        val dialog = ASBTalismanDialogFragment.newInstance(talisman)
        dialog.setTargetFragment(this, REQUEST_CODE_TALISMAN)
        dialog.show(this.parentFragmentManager, "TALISMAN")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_CODE_TALISMAN -> {
                if (data == null) return
                val metadata = data.getSerializableExtra(ASBTalismanDialogFragment.EXTRA_TALISMAN)
                viewModel.saveTalisman(metadata as TalismanMetadata)
            }
        }
    }
}