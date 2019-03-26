package com.ghstudios.android.features.monsters.list;

import com.ghstudios.android.data.classes.MonsterClass;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

/**
 * The main activity for the monster list.
 * Contains separate small, large, and deviant tabs
 */
public class MonsterListPagerActivity extends BasePagerActivity {

    @Override
    public void onAddTabs(TabAdder tabs) {
        setTitle(R.string.title_monsters);
        super.setAsTopLevel();

        tabs.addTab(R.string.monster_class_large, () ->
                MonsterListFragment.newInstance(MonsterClass.LARGE)
        );

        tabs.addTab(R.string.monster_class_deviant, () ->
                MonsterListFragment.newInstance(MonsterClass.DEVIANT)
        );

        tabs.addTab(R.string.monster_class_small, () ->
                MonsterListFragment.newInstance(MonsterClass.SMALL)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.MONSTERS;
    }
}
