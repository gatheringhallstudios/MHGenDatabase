package com.ghstudios.android.features.arena;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

public class ArenaQuestListActivity extends GenericActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.arena_quests);
	}

    @Override
    protected int getSelectedSection() {
        return MenuSection.QUESTS;
    }

    @Override
	protected Fragment createFragment() {
		super.detail = new ArenaQuestListFragment();
		return super.detail;
	}

}
