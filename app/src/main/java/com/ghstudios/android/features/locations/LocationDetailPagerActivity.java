package com.ghstudios.android.features.locations;

import com.ghstudios.android.data.DataManager;
import com.ghstudios.android.loader.GatheringListCursorLoader;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;
import com.ghstudios.android.mhgendatabase.R;

public class LocationDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a monster ID as a long
     */
    public static final String EXTRA_LOCATION_ID =
            "com.daviancorp.android.android.ui.detail.location_id";
    @Override
    public void onAddTabs(TabAdder tabs) {
        long locationId = getIntent().getLongExtra(EXTRA_LOCATION_ID, -1);
        setTitle(DataManager.get().getLocation(locationId).getName());

        tabs.addTab(R.string.location_detail_tab_map, () ->
                LocationDetailFragment.newInstance(locationId)
        );

        tabs.addTab(R.string.title_monsters, () ->
                LocationHabitatFragment.newInstance(locationId)
        );

        tabs.addTab(R.string.rank_lr, () ->
                LocationRankFragment.newInstance(locationId, GatheringListCursorLoader.RANK_LR)
        );

        tabs.addTab(R.string.rank_hr, () ->
                LocationRankFragment.newInstance(locationId, GatheringListCursorLoader.RANK_HR)
        );

        tabs.addTab(R.string.rank_g, () ->
                LocationRankFragment.newInstance(locationId, GatheringListCursorLoader.RANK_G)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.LOCATIONS;
    }
}
