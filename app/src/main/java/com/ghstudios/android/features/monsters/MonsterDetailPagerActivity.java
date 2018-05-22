package com.ghstudios.android.features.monsters;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.features.monsters.detail.MonsterDamageFragment;
import com.ghstudios.android.features.monsters.detail.MonsterHabitatFragment;
import com.ghstudios.android.features.monsters.detail.MonsterQuestFragment;
import com.ghstudios.android.features.monsters.detail.MonsterRewardFragment;
import com.ghstudios.android.features.monsters.detail.MonsterStatusFragment;
import com.ghstudios.android.features.monsters.detail.MonsterSummaryFragment;
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
        setTitle(DataManager.get(getApplicationContext()).getMonster(monsterId).getName());

        tabs.addTab("Summary", () ->
                MonsterSummaryFragment.newInstance(monsterId)
        );

        tabs.addTab("Damage", () ->
                MonsterDamageFragment.newInstance(monsterId)
        );

        tabs.addTab("Status", () ->
                MonsterStatusFragment.newInstance(monsterId)
        );

        tabs.addTab("Habitat", () ->
                MonsterHabitatFragment.newInstance(monsterId)
        );

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
