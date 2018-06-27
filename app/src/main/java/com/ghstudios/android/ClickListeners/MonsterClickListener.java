package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.monsters.detail.MonsterDetailPagerActivity;

/**
 * Created by Mark on 2/24/2015.
 */
public class MonsterClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public MonsterClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, MonsterDetailPagerActivity.class);
        i.putExtra(MonsterDetailPagerActivity.EXTRA_MONSTER_ID, id);
        c.startActivity(i);
    }
}