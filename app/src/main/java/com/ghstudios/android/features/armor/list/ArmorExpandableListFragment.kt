package com.ghstudios.android.features.armor.list

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.view.*

import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.mhgendatabase.R

import com.ghstudios.android.data.classes.ArmorFamily
import com.ghstudios.android.util.loggedThread

/**
 * ViewModel used to background load armor families and store them.
 */
class ArmorFamilyListViewModel: ViewModel() {
    val dataManager = DataManager.get()
    val armorFamilyListData = MutableLiveData<List<ArmorFamily>>()

    // loaded family type to prevent double-loading
    private var hunterType: Int? = null

    fun initialize(hunterType: Int) {
        if (hunterType == this.hunterType) {
            return
        }

        this.hunterType = hunterType
        loggedThread("ArmorFamily List Loading") {
            armorFamilyListData.postValue(dataManager.queryArmorFamilies(hunterType))
        }
    }
}

/**
 * Fragment used to display the list of armor families.
 */
class ArmorExpandableListFragment : Fragment() {
    companion object {
        private val ARG_TYPE = "ARMOR_TYPE"

        const val KEY_FILTER_RANK = "FILTER_RANK"
        const val KEY_FILTER_SLOTS = "FILTER_SLOTS"
        const val KEY_FILTER_SLOTS_SPECIFICATION = "FILTER_SLOTS_SPEC"

        @JvmStatic fun newInstance(type: Int): ArmorExpandableListFragment {
            val args = Bundle()
            args.putInt(ARG_TYPE, type)
            val f = ArmorExpandableListFragment()
            f.arguments = args
            return f
        }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ArmorFamilyListViewModel::class.java)
    }

    private var adapter: ArmorFamilyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hunterType = arguments?.getInt(ARG_TYPE, Armor.ARMOR_TYPE_BOTH) ?: Armor.ARMOR_TYPE_BOTH
        viewModel.initialize(hunterType)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_generic_expandable_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.armorFamilyListData.observe(this, Observer { families ->
            if (families == null) return@Observer

            val groups = families
                    .groupBy { it.rarity }
                    .map { ArmorFamilyGroup(it.key, it.value) }
                    .sortedBy { it.rarity }

            adapter = ArmorFamilyListAdapter(groups)

            val elv = view.findViewById<ExpandableListView>(R.id.expandableListView)
            elv.setAdapter(adapter)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_armor_list, menu)
    }
}