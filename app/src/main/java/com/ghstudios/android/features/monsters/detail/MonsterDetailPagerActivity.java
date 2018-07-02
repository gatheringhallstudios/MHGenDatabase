package com.ghstudios.android.features.monsters.detail;

import android.arch.lifecycle.ViewModelProviders;

import com.ghstudios.android.data.classes.meta.MonsterMetadata;
import com.ghstudios.android.loader.HuntingRewardListCursorLoader;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class MonsterDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a monster ID as a long
     */
    public static final String EXTRA_MONSTER_ID =
            "com.daviancorp.android.android.ui.detail.monster_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long monsterId = getIntent().getLongExtra(EXTRA_MONSTER_ID, -1);

        MonsterDetailViewModel viewModel = ViewModelProviders.of(this).get(MonsterDetailViewModel.class);
        viewModel.setMonster(monsterId);

        viewModel.getMonsterMetadata().observe(this, meta -> {
            if (meta == null) return; // todo: throw?

            setTitle(meta.getName());
            resetTabs((adder) -> initTabs(adder, meta));
        });
    }

    protected void initTabs(TabAdder tabs, MonsterMetadata meta) {
        Long monsterId = meta.getId();

        tabs.addTab("Summary", () ->
                MonsterSummaryFragment.newInstance(monsterId)
        );

        if (meta.getHasDamageData() || meta.getHasStatusData()) {
            // only include Damage tab if there is data
            tabs.addTab("Damage", () ->
                    MonsterDamageFragment.newInstance(monsterId)
            );
        }

        tabs.addTab("Low Rank", () ->
                MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_LR)
        );

        tabs.addTab("High Rank", () ->
                MonsterRewardFragment.newInstance(monsterId, HuntingRewardListCursorLoader.RANK_HR)
        );

        // add G soon

        tabs.addTab("Quest", () ->
                MonsterQuestFragment.newInstance(monsterId)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.MONSTERS;
    }
}
