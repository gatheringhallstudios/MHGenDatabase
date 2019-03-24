package com.ghstudios.android.features.quests

import com.ghstudios.android.AssetLoader
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.data.classes.QuestHub


class QuestListPagerActivity : BasePagerActivity() {

    override fun onAddTabs(tabs: BasePagerActivity.TabAdder) {
        setTitle(R.string.title_quests)

        tabs.addTab(AssetLoader.localizeHub(QuestHub.VILLAGE)) {
            QuestExpandableListFragment.newInstance(QuestHub.VILLAGE)
        }

        tabs.addTab(AssetLoader.localizeHub(QuestHub.GUILD)) {
            QuestExpandableListFragment.newInstance(QuestHub.GUILD)
        }

        tabs.addTab(AssetLoader.localizeHub(QuestHub.EVENT)) {
            QuestExpandableListFragment.newInstance(QuestHub.EVENT)
        }

        tabs.addTab(AssetLoader.localizeHub(QuestHub.PERMIT)) {
            QuestExpandableListFragment.newInstance(QuestHub.PERMIT)
        }

        super.setAsTopLevel()
    }

    override fun getSelectedSection(): Int {
        return MenuSection.QUESTS
    }
}
