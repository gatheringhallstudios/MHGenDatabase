package com.ghstudios.android.ui.ClickListeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.armorsetbuilder.ASBPagerActivity;
import com.ghstudios.android.features.decorations.DecorationDetailPagerActivity;

/**
 * Created by Mark on 2/24/2015.
 */
public class DecorationClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    private boolean fromAsb;
    private Activity activity;

    public DecorationClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    public DecorationClickListener(Context context, Long id, boolean fromAsb, Activity activity) {
        this(context, id);
        this.fromAsb = fromAsb;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, DecorationDetailPagerActivity.class);
        i.putExtra(DecorationDetailPagerActivity.EXTRA_DECORATION_ID, id);

        if (fromAsb) {
            i.putExtras(activity.getIntent());
            activity.startActivityForResult(i, ASBPagerActivity.REQUEST_CODE_ADD_DECORATION);
        }
        else {
            c.startActivity(i);
        }
    }
}
