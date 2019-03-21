package com.ghstudios.android.features.armorsetbuilder.talismans

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.util.applyArguments
import kotlinx.android.synthetic.main.fragment_recyclerview_main.*



class ASBTalismanViewModel: ViewModel() {
    val dataManager = DataManager.get()
    val asbManager = dataManager.asbManager

    /** Returns talisman data. */
    val talismanData = MutableLiveData<List<ASBTalisman>>()

    fun reload() {
        talismanData.value = asbManager.getTalismans()
    }

    fun saveTalisman(data: ASBTalisman) {
        talismanData.value = asbManager.saveTalisman(data)
    }

    fun saveTalisman(data: TalismanMetadata) {
        val talisman = ASBTalisman(data.typeIndex)
        talisman.id = data.id
        talisman.numSlots = data.numSlots
        for ((skillId, points) in data.skills) {
            if (skillId == -1L) {
                continue
            }

            val skillTree = dataManager.getSkillTree(skillId)
            if (skillTree != null) {
                talisman.addSkill(skillTree, points)
            }
        }

        saveTalisman(talisman)
    }
}

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
        ViewModelProviders.of(this).get(ASBTalismanViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableDivider()

        val isSelecting = arguments?.getBoolean(MODE_SELECTION) == true

        val fab = this.fab
        fab.visibility = View.VISIBLE
        fab.setOnClickListener {
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
                },
                onLongSelect = { showAddTalismanDialog(it) }
        )
        setAdapter(adapter)

        viewModel.reload()
        viewModel.talismanData.observe(this, Observer {
            if (it == null) return@Observer
            adapter.setItems(it)
        })
    }

    fun showAddTalismanDialog(talisman: ASBTalisman? = null) {
        val dialog = ASBTalismanDialogFragment.newInstance(talisman)
        dialog.setTargetFragment(this, REQUEST_CODE_TALISMAN)
        dialog.show(this.fragmentManager, "TALISMAN")
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