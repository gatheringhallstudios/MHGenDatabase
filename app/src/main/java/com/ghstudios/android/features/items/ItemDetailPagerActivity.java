package com.ghstudios.android.features.items;

import android.arch.lifecycle.ViewModelProviders;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.features.combining.CombiningListFragment;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import com.ghstudios.android.features.items.basicdetail.ItemComponentFragment;
import com.ghstudios.android.features.items.basicdetail.ItemDetailFragment;
import com.ghstudios.android.features.items.basicdetail.ItemLocationFragment;
import com.ghstudios.android.features.items.basicdetail.ItemMonsterFragment;
import com.ghstudios.android.features.items.basicdetail.ItemQuestFragment;

public class ItemDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a item ID as a long
     */
    public static final String EXTRA_ITEM_ID =
            "com.daviancorp.android.android.ui.detail.item_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);

        ItemDetailViewModel viewModel = ViewModelProviders.of(this).get(ItemDetailViewModel.class);
        viewModel.setItem(itemId);

        viewModel.getItemData().observe(this, (item) -> {
            setTitle(item.getName());
        });

        tabs.addTab("Detail", () ->
                ItemDetailFragment.newInstance(itemId)
        );
        tabs.addTab("Usage", () ->
                // List of combinations, armor, aecoration, and weapons
                ItemComponentFragment.newInstance(itemId)
        );
        tabs.addTab("Monster", () ->
                // Monster drops
                ItemMonsterFragment.newInstance(itemId)
        );
        tabs.addTab("Quest", () ->
                ItemQuestFragment.newInstance(itemId)
        );
        tabs.addTab("Location", () ->
                ItemLocationFragment.newInstance(itemId)
        );

        //JOE: No wyporium in MHGen
        //    return ItemTradeFragment.newInstance(itemId);
		// ArenaQuest rewards
        //TODO reenable when arena quests are complete.
		// return ItemArenaFragment.newInstance(itemId);
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.ITEMS;
    }
}
