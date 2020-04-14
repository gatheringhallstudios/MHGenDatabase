package com.ghstudios.android.features.monsters.list

import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.data.classes.MonsterClass
import com.ghstudios.android.RecyclerViewFragment
import com.ghstudios.android.util.applyArguments


/**
 * A fragment used to display a monster list of a category type
 */
class MonsterListFragment : RecyclerViewFragment() {
    companion object {
        private val ARG_TAB = "MONSTER_TAB"

        @JvmStatic fun newInstance(tab: MonsterClass?): MonsterListFragment {
            return MonsterListFragment().applyArguments {
                putSerializable(ARG_TAB, tab)
            }
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(MonsterListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val monsterClass = arguments?.getSerializable(ARG_TAB) as MonsterClass?
        viewModel.loadMonsters(monsterClass)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MonsterListAdapter()

        setAdapter(adapter)
        enableDivider()

        viewModel.monsterData.observe(this, Observer {
            if (it != null && adapter.itemCount == 0) {
                adapter.setItems(it)
            }
        })
    }
}
