package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.locations.LocationDetailPagerActivity;

/**
 * Created by Mark on 2/23/2015.
 */
public class LocationClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public LocationClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, LocationDetailPagerActivity.class);

        if(id>100) id = id-100;

        i.putExtra(LocationDetailPagerActivity.EXTRA_LOCATION_ID, id);
        c.startActivity(i);
    }
}