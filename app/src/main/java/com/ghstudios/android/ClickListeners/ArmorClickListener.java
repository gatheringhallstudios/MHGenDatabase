package com.ghstudios.android.ClickListeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.armor.ArmorDetailPagerActivity;

public class ArmorClickListener implements View.OnClickListener {

    private Context c;
    private Long id;

    private Activity activity;
    private int requestCode;

    public ArmorClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    public ArmorClickListener(Context context, Long id, Activity activity, int requestCode) {
        this(context, id);
        this.activity = activity;
        this.requestCode = requestCode;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, ArmorDetailPagerActivity.class);
        i.putExtra(ArmorDetailPagerActivity.EXTRA_ARMOR_ID, id);

        // If we are being called by something else
        if (activity != null) {
            i.putExtras(activity.getIntent().getExtras());
            activity.startActivityForResult(i, requestCode);
        }
        else {
            c.startActivity(i);
        }
    }
}
