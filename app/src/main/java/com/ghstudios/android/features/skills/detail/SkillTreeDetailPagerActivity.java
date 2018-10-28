package com.ghstudios.android.features.skills.detail;

import android.arch.lifecycle.ViewModelProviders;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.data.classes.Armor;
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

        SkillDetailViewModel viewModel = ViewModelProviders.of(this).get(SkillDetailViewModel.class);
        viewModel.setSkillTreeId(skillTreeId);

        viewModel.getSkillTreeData().observe(this, (data) -> {
            if (data == null) return;
            setTitle(data.getName());
        });

        tabs.addTab(R.string.skill_tab_detail, () ->
                SkillTreeDetailFragment.newInstance(skillTreeId)
        );

        tabs.addTab(R.string.skill_tab_decorations, () ->
                SkillTreeDecorationFragment.newInstance(skillTreeId,
                        ItemToSkillTreeListCursorLoader.TYPE_DECORATION)
        );

        tabs.addTab(R.string.skill_tab_head, () ->
                SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_HEAD)
        );

        tabs.addTab(R.string.skill_tab_body, () ->
                SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_BODY)
        );

        tabs.addTab(R.string.skill_tab_arms, () ->
                SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_ARMS)
        );

        tabs.addTab(R.string.skill_tab_waist, () ->
                SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_WAIST)
        );

        tabs.addTab(R.string.skill_tab_legs, () ->
                SkillTreeArmorFragment.newInstance(skillTreeId, Armor.ARMOR_SLOT_LEGS)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.SKILL_TREES;
    }
}
