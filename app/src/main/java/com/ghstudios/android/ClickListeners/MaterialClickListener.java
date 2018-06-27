package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.items.detail.MaterialDetailActivity;

/**
 * Created by Joseph on 7/7/2016.
 */
public class MaterialClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public MaterialClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, MaterialDetailActivity.class);
        i.putExtra(MaterialDetailActivity.EXTRA_MATERIAL_ITEM_ID, id);
        c.startActivity(i);
    }
}
