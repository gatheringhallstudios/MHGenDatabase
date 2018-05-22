package com.ghstudios.android.features.arena;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class ArenaQuestDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a arena quest ID as a long
     */
    public static final String EXTRA_ARENA_QUEST_ID =
            "com.daviancorp.android.android.ui.detail.arena_quest_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long id = getIntent().getLongExtra(EXTRA_ARENA_QUEST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getArenaQuest(id).getName());

        tabs.addTab("Detail", () ->
                ArenaQuestDetailFragment.newInstance(id)
        );

        tabs.addTab("Monsters", () ->
                ArenaQuestMonsterFragment.newInstance(id)
        );

        tabs.addTab("Rewards", () ->
                ArenaQuestRewardFragment.newInstance(id)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }
}
