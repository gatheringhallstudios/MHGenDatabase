package com.ghstudios.android.ui.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.ui.detail.MonsterDetailActivity;

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
        Intent i = new Intent(c, MonsterDetailActivity.class);
        i.putExtra(MonsterDetailActivity.EXTRA_MONSTER_ID, id);
        c.startActivity(i);
    }
}