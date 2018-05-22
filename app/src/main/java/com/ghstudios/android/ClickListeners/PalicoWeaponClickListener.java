package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.palicos.PalicoWeaponDetailActivity;

/**
 * Created by Joseph on 7/10/2016.
 */
public class PalicoWeaponClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public PalicoWeaponClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, PalicoWeaponDetailActivity.class);
        i.putExtra(PalicoWeaponDetailActivity.EXTRA_WEAPON_ID, id);
        c.startActivity(i);
    }
}
