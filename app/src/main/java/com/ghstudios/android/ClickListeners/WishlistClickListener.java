package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ghstudios.android.features.wishlist.detail.WishlistDetailPagerActivity;

public class WishlistClickListener implements View.OnClickListener {
    private Context c;
    private Long id;

    public WishlistClickListener(Context context, Long id) {
        super();
        this.id = id;
        this.c = context;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(c, WishlistDetailPagerActivity.class);
        i.putExtra(WishlistDetailPagerActivity.EXTRA_WISHLIST_ID, id);
        c.startActivity(i);
    }
}