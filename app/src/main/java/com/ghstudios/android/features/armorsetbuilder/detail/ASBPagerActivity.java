package com.ghstudios.android.features.armorsetbuilder.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ghstudios.android.data.classes.ASBSession;
import com.ghstudios.android.features.armorsetbuilder.list.ASBSetListFragment;
import com.ghstudios.android.loader.ASBSessionLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

public class ASBPagerActivity extends BasePagerActivity {

    public static final String EXTRA_FROM_SET_BUILDER = "com.daviancorp.android.ui.detail.from_set_builder";
    public static final String EXTRA_FROM_TALISMAN_EDITOR = "com.daviancorp.android.ui.detail.from_talisman_editor";
    public static final String EXTRA_TALISMAN_SKILL_INDEX = "com.daviancorp.android.ui.detail.talisman_skill_number";
    public static final String EXTRA_PIECE_INDEX = "com.daviancorp.android.ui.detail.piece_index";
    public static final String EXTRA_DECORATION_INDEX = "com.daviancorp.android.ui.detail.decoration_index";
    public static final String EXTRA_DECORATION_MAX_SLOTS = "com.daviancorp.android.ui.detail.decoration_max_slots";
    public static final String EXTRA_SET_RANK = "com.daviancorp.android.ui.detail.set_rank";
    public static final String EXTRA_SET_HUNTER_TYPE = "com.daviancorp.android.ui.detail.hunter_type";

    public static final String EXTRA_TALISMAN_SKILL_TREE_1 = "com.daviancorp.android.ui.detail.skill_tree_1";
    public static final String EXTRA_TALISMAN_SKILL_POINTS_1 = "com.daviancorp.android.ui.detail.skill_points_1";
    public static final String EXTRA_TALISMAN_SKILL_TREE_2 = "com.daviancorp.android.ui.detail.skill_tree_2";
    public static final String EXTRA_TALISMAN_SKILL_POINTS_2 = "com.daviancorp.android.ui.detail.skill_points_2";
    public static final String EXTRA_TALISMAN_TYPE_INDEX = "com.daviancorp.android.ui.detail.talisman_type_index";
    public static final String EXTRA_TALISMAN_SLOTS = "com.daviancorp.android.ui.detail.talisman_slots";

    public static final int REQUEST_CODE_ADD_PIECE = 537;
    public static final int REQUEST_CODE_ADD_DECORATION = 538;
    public static final int REQUEST_CODE_CREATE_TALISMAN = 539;
    public static final int REQUEST_CODE_REMOVE_PIECE = 540;
    public static final int REQUEST_CODE_REMOVE_DECORATION = 541;
    public static final int REQUEST_CODE_SET_WEAPON_SLOTS = 542;


    @Override
    public void onAddTabs(TabAdder tabs) {
        setTitle(getIntent().getStringExtra(ASBSetListFragment.EXTRA_ASB_SET_NAME));
        long asbId = getIntent().getLongExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, -1);

        try {
            ASBDetailViewModel viewModel = ViewModelProviders.of(this).get(ASBDetailViewModel.class);
            viewModel.loadSession(asbId);

            tabs.addTab(getString(R.string.asb_tab_equipment), ASBFragment::new);
            tabs.addTab(getString(R.string.asb_tab_skills), ASBSkillsListFragment::new);

        } catch (Exception ex) {
            showFatalError();
            Log.e(getClass().getSimpleName(), "Fatal error loading ASB", ex);
        }
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR_SET_BUILDER;
    }
}
