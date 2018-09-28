package com.ghstudios.android.features.armor.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.view.*

import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R

import com.ghstudios.android.data.classes.Armor.Companion.ARMOR_TYPE_BLADEMASTER

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

    private var hunterType: Int = 0

    private var adapter: ArmorFamilyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hunterType = arguments!!.getInt(ARG_TYPE, ARMOR_TYPE_BLADEMASTER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_generic_expandable_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // todo: use a viewmodel. This was refactored but not thoroughly.
        val families = DataManager.get().queryArmorFamilies(hunterType)
        val groups = families
                .groupBy { it.rarity }
                .map { ArmorFamilyGroup(it.key, it.value) }
                .sortedBy { it.rarity }

        adapter = ArmorFamilyListAdapter(groups)

        val elv = view.findViewById<ExpandableListView>(R.id.expandableListView)
        elv.setAdapter(adapter)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_armor_list, menu)
    }
}