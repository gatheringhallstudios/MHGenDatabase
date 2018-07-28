package com.ghstudios.android.features.monsters.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View

import com.ghstudios.android.data.classes.MonsterClass
import com.ghstudios.android.RecyclerViewFragment


/**
 * A fragment used to display a monster list of a category type
 */
class MonsterListFragment : RecyclerViewFragment() {
    companion object {
        private val ARG_TAB = "MONSTER_TAB"

        @JvmStatic fun newInstance(tab: MonsterClass?): MonsterListFragment {
            val args = Bundle()
            args.putSerializable(ARG_TAB, tab)
            val f = MonsterListFragment()
            f.arguments = args
            return f
        }
    }

    private val adapter = MonsterListAdapter()

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MonsterListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val monsterClass = arguments?.getSerializable(ARG_TAB) as MonsterClass?
        viewModel.loadMonsters(monsterClass)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter(adapter)
        enableDivider()

        viewModel.monsterData.observe(this, Observer {
            if (it != null && adapter.itemCount == 0) {
                adapter.setItems(it)
            }
        })
    }
}
