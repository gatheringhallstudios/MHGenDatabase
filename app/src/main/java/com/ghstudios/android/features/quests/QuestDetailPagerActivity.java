package com.ghstudios.android.features.quests;

import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import android.arch.lifecycle.ViewModelProviders;
import com.ghstudios.android.data.classes.Quest;

public class QuestDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a quest ID as a long
     */
    public static final String EXTRA_QUEST_ID =
            "com.daviancorp.android.android.ui.detail.monster_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long questId = getIntent().getLongExtra(EXTRA_QUEST_ID, -1);

        QuestDetailViewModel viewModel = ViewModelProviders.of(this).get(QuestDetailViewModel.class);
        Quest q = viewModel.setQuest(questId);

        setTitle(q.getName());

        tabs.addTab("Detail", () ->
                QuestDetailFragment.newInstance(questId)
        );

        if(q.HasGathingItem())
            tabs.addTab("Items", QuestItemFragment::new);

        tabs.addTab("Rewards", () ->
                QuestRewardFragment.newInstance(questId)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }
}
