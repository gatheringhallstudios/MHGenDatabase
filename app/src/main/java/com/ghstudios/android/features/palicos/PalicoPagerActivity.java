package com.ghstudios.android.features.palicos;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

/**
 * Created by Joseph on 7/9/2016.
 */
public class PalicoPagerActivity extends BasePagerActivity {

    @Override
    public void onAddTabs(TabAdder tabs) {
        setTitle(R.string.palicos);

        tabs.addTab("Weapons", () ->
                new PalicoWeaponListFragment()
        );

        tabs.addTab("Armor", () ->
                new PalicoArmorListFragment()
        );

        // Tag as top level activity
        super.setAsTopLevel();
    }


    @Override
    protected int getSelectedSection() {
        return MenuSection.PALICOS;
    }
}
