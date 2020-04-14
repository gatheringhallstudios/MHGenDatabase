package com.ghstudios.android.features.items.detail;

import androidx.lifecycle.ViewModelProvider;

import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import com.ghstudios.android.data.classes.meta.ItemMetadata;
import com.ghstudios.android.mhgendatabase.R;

public class ItemDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a item ID as a long
     */
    public static final String EXTRA_ITEM_ID =
            "com.daviancorp.android.android.ui.detail.item_id";

    @Override
    public void onAddTabs(TabAdder tabs) {
        hideTabsIfSingular();

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);

        ItemDetailViewModel viewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);
        ItemMetadata meta = viewModel.setItem(itemId);

        viewModel.getItemData().observe(this, (item) -> {
            setTitle(item.getName());
        });

        tabs.addTab(R.string.item_detail_tab_detail, () ->
                ItemDetailFragment.newInstance(itemId)
        );

        if (meta.getUsedInCombining() || meta.getUsedInCrafting()) {
            tabs.addTab(R.string.item_detail_tab_usage, () ->
                    // List of combinations, armor, decoration, and weapons
                    ItemUsageFragment.newInstance(itemId)
            );
        }

        if (meta.isMonsterReward()) {
            tabs.addTab(R.string.type_monster, () ->
                    // Monster drops
                    ItemMonsterFragment.newInstance(itemId)
            );
        }

        if (meta.isQuestReward()) {
            tabs.addTab(R.string.type_quest, () ->
                    ItemQuestFragment.newInstance(itemId)
            );
        }

        if (meta.isGatherable()) {
            tabs.addTab(R.string.type_location, ItemLocationFragment::new);
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
