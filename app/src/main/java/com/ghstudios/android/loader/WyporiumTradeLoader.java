package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.classes.WyporiumTrade;
import com.ghstudios.android.data.DataManager;

public class WyporiumTradeLoader extends DataLoader<WyporiumTrade> {
    private long mTradeId;

    public WyporiumTradeLoader(Context context, long tradeId) {
        super(context);
        mTradeId = tradeId;
    }

    @Override
    public WyporiumTrade loadInBackground() {
        // Query the specific wyporium trade
        return DataManager.get().getWyporiumTrade(mTradeId);
    }
}
