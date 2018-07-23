package com.ghstudios.android.features.locations;

import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.loader.GatheringListCursorLoader;
import com.ghstudios.android.BasePagerActivity;
import com.ghstudios.android.MenuSection;

public class LocationDetailPagerActivity extends BasePagerActivity {
    /**
     * A key for passing a monster ID as a long
     */
    public static final String EXTRA_LOCATION_ID =
            "com.daviancorp.android.android.ui.detail.location_id";
    @Override
    public void onAddTabs(TabAdder tabs) {
        long locationId = getIntent().getLongExtra(EXTRA_LOCATION_ID, -1);
        setTitle(DataManager.get(getApplicationContext()).getLocation(locationId).getName());

        tabs.addTab("Map", () ->
                LocationDetailFragment.newInstance(locationId)
        );

        tabs.addTab("Monsters", () ->
                LocationHabitatFragment.newInstance(locationId)
        );

        tabs.addTab("Low Rank", () ->
                LocationRankFragment.newInstance(locationId, GatheringListCursorLoader.RANK_LR)
        );

        tabs.addTab("High Rank", () ->
                LocationRankFragment.newInstance(locationId, GatheringListCursorLoader.RANK_HR)
        );

        tabs.addTab("G Rank", () ->
                LocationRankFragment.newInstance(locationId, GatheringListCursorLoader.RANK_G)
        );
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.LOCATIONS;
    }
}
