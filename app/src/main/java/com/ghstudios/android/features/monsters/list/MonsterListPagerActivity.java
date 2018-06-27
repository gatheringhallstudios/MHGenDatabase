package com.ghstudios.android.features.monsters.list;

import com.ghstudios.android.features.monsters.list.MonsterListFragment;
import com.ghstudios.android.loader.MonsterListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class MonsterListPagerActivity extends BasePagerActivity {

    @Override
    public void onAddTabs(TabAdder tabs) {
        setTitle(R.string.monsters);
        super.setAsTopLevel();

        tabs.addTab(MonsterListCursorLoader.TAB_LARGE, () ->
                MonsterListFragment.newInstance("Large")
        );

        tabs.addTab(MonsterListCursorLoader.TAB_SMALL, () ->
                MonsterListFragment.newInstance("Small")
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.MONSTERS;
    }
}
