package com.ghstudios.android.features.skills;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.loader.ItemToSkillTreeListCursorLoader;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import com.ghstudios.android.mhgendatabase.R;

public class SkillTreeDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a monster ID as a long
     */
    public static final String EXTRA_SKILLTREE_ID =
            "com.daviancorp.android.android.ui.detail.skill_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long skillTreeId = getIntent().getLongExtra(EXTRA_SKILLTREE_ID, -1);
        setTitle(DataManager.get().getSkillTree(skillTreeId).getName());

        tabs.addTab(getString(R.string.skill_tab_detail), () ->
                SkillTreeDetailFragment.newInstance(skillTreeId)
        );

        tabs.addTab(getString(R.string.skill_tab_decorations), () ->
                SkillTreeDecorationFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_DECORATION)
        );

        tabs.addTab(getString(R.string.skill_tab_head), () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_HEAD)
        );

        tabs.addTab(getString(R.string.skill_tab_body), () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_BODY)
        );

        tabs.addTab(getString(R.string.skill_tab_arms), () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_ARMS)
        );

        tabs.addTab(getString(R.string.skill_tab_waist), () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_WAIST)
        );

        tabs.addTab(getString(R.string.skill_tab_legs), () ->
                SkillTreeArmorFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_LEGS)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.SKILL_TREES;
    }
}
