package com.ghstudios.android.features.skills;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.loader.ItemToSkillTreeListCursorLoader;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class SkillTreeDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a monster ID as a long
     */
    public static final String EXTRA_SKILLTREE_ID =
            "com.daviancorp.android.android.ui.detail.skill_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long skillTreeId = getIntent().getLongExtra(EXTRA_SKILLTREE_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getSkillTree(skillTreeId).getName());

        tabs.addTab("Detail", () ->
                SkillTreeDetailFragment.newInstance(skillTreeId)
        );

        tabs.addTab("Head", () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_HEAD)
        );

        tabs.addTab("Body", () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_BODY)
        );

        tabs.addTab("Arm", () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_ARMS)
        );

        tabs.addTab("Waist", () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_WAIST)
        );

        tabs.addTab("Leg", () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_LEGS)
        );

        tabs.addTab("Jewels", () ->
                SkillTreeDecorationFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_DECORATION)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.SKILL_TREES;
    }
}
