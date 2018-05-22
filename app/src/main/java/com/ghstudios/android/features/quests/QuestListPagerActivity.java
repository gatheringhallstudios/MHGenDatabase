package com.ghstudios.android.features.quests;

import com.ghstudios.android.loader.QuestListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class QuestListPagerActivity extends BasePagerActivity {

    @Override
    public void onAddTabs(TabAdder tabs) {
        setTitle(R.string.quests);

        tabs.addTab(QuestListCursorLoader.HUB_CARAVAN, () ->
                QuestExpandableListFragment.newInstance("Village")
        );

        tabs.addTab(QuestListCursorLoader.HUB_GUILD, () ->
                QuestExpandableListFragment.newInstance("Guild")
        );

        tabs.addTab(QuestListCursorLoader.HUB_EVENT, () ->
                QuestExpandableListFragment.newInstance("Event")
        );

        tabs.addTab(QuestListCursorLoader.HUB_PERMIT, () ->
                QuestExpandableListFragment.newInstance("Permit")
        );

        super.setAsTopLevel();
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }
}
