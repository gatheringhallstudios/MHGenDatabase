package com.ghstudios.android.ClickListeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.data.classes.ArmorFamilyBase;
import com.ghstudios.android.features.armor.detail.ArmorSetDetailPagerActivity;

public class ArmorClickListener implements View.OnClickListener {

    private Context c;
    private Long id;
    private boolean isFamily;

    private Activity activity;
    private int requestCode;

    public ArmorClickListener(Context context, Long id, boolean isFamily) {
        super();
        this.id = id;
        this.c = context;
        this.isFamily = isFamily;
    }

    public ArmorClickListener(Context context, Long id, boolean isFamily, Activity activity, int requestCode) {
        this(context, id, isFamily);
        this.activity = activity;
        this.requestCode = requestCode;
    }

    public ArmorClickListener(Context context, ArmorFamilyBase family) {
        this(context, family.getId(), true);
    }

    public ArmorClickListener(Context context, Armor armor) {
        this(context, armor.getId(), false);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, ArmorSetDetailPagerActivity.class);
        if(isFamily)
            i.putExtra(ArmorSetDetailPagerActivity.EXTRA_FAMILY_ID, id);
        else
            i.putExtra(ArmorSetDetailPagerActivity.EXTRA_ARMOR_ID, id);

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
