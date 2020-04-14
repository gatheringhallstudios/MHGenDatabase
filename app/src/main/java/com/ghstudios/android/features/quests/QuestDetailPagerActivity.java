package com.ghstudios.android.features.quests;

import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import androidx.lifecycle.ViewModelProviders;
import com.ghstudios.android.data.classes.Quest;
import com.ghstudios.android.mhgendatabase.R;

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

        if (q == null) {
            showFatalError();
            return;
        }

        setTitle(q.getName());

        tabs.addTab(R.string.quest_detail_tab_detail, () ->
                QuestDetailFragment.newInstance(questId)
        );

        if (q.getHasGatheringItem() || q.getHasHuntingRewardItem()) {
            tabs.addTab(R.string.title_items, QuestItemFragment::new);
        }

        tabs.addTab(R.string.quest_detail_tab_rewards, () ->
                QuestRewardFragment.newInstance(questId)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }
}
