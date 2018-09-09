package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.items.detail.ItemDetailPagerActivity;

/**
 * Created by Mark on 2/24/2015.
 */
public class BasicItemClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public BasicItemClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, ItemDetailPagerActivity.class);
        i.putExtra(ItemDetailPagerActivity.EXTRA_ITEM_ID, id);
        c.startActivity(i);
    }
}
