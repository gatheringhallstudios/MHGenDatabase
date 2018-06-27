package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.weapons.detail.WeaponDetailPagerActivity;

/**
 * Created by Mark on 2/24/2015.
 */
public class WeaponClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public WeaponClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, WeaponDetailPagerActivity.class);
        i.putExtra(WeaponDetailPagerActivity.EXTRA_WEAPON_ID, id);
        c.startActivity(i);
    }
}
