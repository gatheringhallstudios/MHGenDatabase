package com.ghstudios.android.features.quests;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.ui.general.BasePagerActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class QuestDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a quest ID as a long
     */
    public static final String EXTRA_QUEST_ID =
            "com.daviancorp.android.android.ui.detail.monster_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long questId = getIntent().getLongExtra(EXTRA_QUEST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getQuest(questId).getName());

        tabs.addTab("Detail", () ->
                QuestDetailFragment.newInstance(questId)
        );
        tabs.addTab("Rewards", () ->
                QuestRewardFragment.newInstance(questId)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }
}
