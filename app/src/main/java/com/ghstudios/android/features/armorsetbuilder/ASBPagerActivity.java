package com.ghstudios.android.features.armorsetbuilder;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

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

    private ASBSession session;

    private List<OnASBSetActivityUpdateListener> onASBSetActivityUpdateListeners;

    @Override
    public void onAddTabs(TabAdder tabs) {
        onASBSetActivityUpdateListeners = new ArrayList<>();

        setTitle(getIntent().getStringExtra(ASBSetListFragment.EXTRA_ASB_SET_NAME));

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(R.id.asb_set_activity, null, new ASBSetLoaderCallbacks());

        // we don't add tabs here. Tabs are reset when the loader complete
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR_SET_BUILDER;
    }

    public ASBSession getASBSession() {
        return session;
    }

    public void addASBSetChangedListener(OnASBSetActivityUpdateListener a) {
        onASBSetActivityUpdateListeners.add(a);
    }

    public void updateASBSetChangedListeners() {
        if (onASBSetActivityUpdateListeners != null) {
            for (OnASBSetActivityUpdateListener a : onASBSetActivityUpdateListeners) {
                a.onASBActivityUpdated(session);
            }
        }
    }

    public interface OnASBSetActivityUpdateListener {
        void onASBActivityUpdated(ASBSession s);
    }

    private class ASBSetLoaderCallbacks implements LoaderManager.LoaderCallbacks<ASBSession> {
        @Override
        public Loader<ASBSession> onCreateLoader(int id, Bundle args) {
            return new ASBSessionLoader(ASBPagerActivity.this, getIntent().getLongExtra(ASBSetListFragment.EXTRA_ASB_SET_ID, -1));
        }

        @Override
        public void onLoadFinished(Loader<ASBSession> loader, ASBSession run) {
            session = run;

            resetTabs((tabs) -> {
                // Load the tabs now that we have a session
                tabs.addTab("Equipment", () ->
                        ASBFragment.newInstance(session.getRank(), session.getHunterType())
                );
                tabs.addTab("Skills", () ->
                        ASBSkillsListFragment.newInstance()
                );

                // kotlin interop nonesense requirement
                return Unit.INSTANCE;
            });

            updateASBSetChangedListeners();
        }

        @Override
        public void onLoaderReset(Loader<ASBSession> loader) {
        }
    }
}
