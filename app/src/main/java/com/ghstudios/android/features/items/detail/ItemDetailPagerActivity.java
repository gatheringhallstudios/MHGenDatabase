package com.ghstudios.android.features.items.detail;

import android.arch.lifecycle.ViewModelProviders;

import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import com.ghstudios.android.data.classes.meta.ItemMetadata;

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
        ItemMetadata meta = viewModel.setItem(itemId);

        viewModel.getItemData().observe(this, (item) -> {
            setTitle(item.getName());
        });

        tabs.addTab("Detail", () ->
                ItemDetailFragment.newInstance(itemId)
        );

        if (meta.getUsedInCombining() || meta.getUsedInCrafting()) {
            tabs.addTab("Usage", () ->
                    // List of combinations, armor, aecoration, and weapons
                    ItemUsageFragment.newInstance(itemId)
            );
        }

        if (meta.isMonsterReward()) {
            tabs.addTab("Monster", () ->
                    // Monster drops
                    ItemMonsterFragment.newInstance(itemId)
            );
        }

        if (meta.isQuestReward()) {
            tabs.addTab("Quest", () ->
                    ItemQuestFragment.newInstance(itemId)
            );
        }

        if (meta.isGatherable()) {
            tabs.addTab("Location", () ->
                    ItemLocationFragment.newInstance(itemId)
            );
        }

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
