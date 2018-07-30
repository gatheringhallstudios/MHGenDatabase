package com.ghstudios.android.features.armor.list;

import android.content.Intent;

import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.features.armor.list.ArmorExpandableListFragment;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.armorsetbuilder.ASBPagerActivity;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class ArmorListPagerActivity extends BasePagerActivity {

    @Override
    public void onAddTabs(TabAdder tabs) {
        setTitle(R.string.armor);

        tabs.addTab("Blade", () ->
                ArmorExpandableListFragment.newInstance(Armor.ARMOR_TYPE_BLADEMASTER)
        );

        tabs.addTab("Gunner", () ->
                ArmorExpandableListFragment.newInstance(Armor.ARMOR_TYPE_GUNNER)
        );

        // Enable back button if we're coming from the set builder
        if (getIntent().getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)) {
            super.disableDrawerIndicator();
            if (getIntent().getIntExtra(ASBPagerActivity.EXTRA_SET_HUNTER_TYPE, -1) == 1) {
                // We change to the gunner page if its a gunner set
                tabs.setDefaultItem(1);
            }
        }
        else {
            // Tag as top level activity
            super.setAsTopLevel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ASBPagerActivity.REQUEST_CODE_ADD_PIECE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ARMOR;
    }
}
