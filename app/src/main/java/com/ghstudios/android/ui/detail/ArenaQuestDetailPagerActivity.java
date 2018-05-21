package com.ghstudios.android.ui.detail;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.adapter.ArenaQuestDetailPagerAdapter;
import com.ghstudios.android.ui.general.GenericTabActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class ArenaQuestDetailActivity extends GenericTabActivity {
    /**
     * A key for passing a arena quest ID as a long
     */
    public static final String EXTRA_ARENA_QUEST_ID =
            "com.daviancorp.android.android.ui.detail.arena_quest_id";

    private ViewPager viewPager;
    private ArenaQuestDetailPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getIntent().getLongExtra(EXTRA_ARENA_QUEST_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getArenaQuest(id).getName());

        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ArenaQuestDetailPagerAdapter(getSupportFragmentManager(), id);
        viewPager.setAdapter(mAdapter);

        setViewPager(viewPager);

    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
