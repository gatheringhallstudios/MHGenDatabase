package com.ghstudios.android.loader;

import android.content.Context;
import com.ghstudios.android.data.classes.ASBSession;
import com.ghstudios.android.data.DataManager;

public class ASBSessionLoader extends DataLoader<ASBSession> {

    private long id;

    public ASBSessionLoader(Context context, long id) {
        super(context);
        this.id = id;
    }

    @Override
    public ASBSession loadInBackground() {
        return DataManager.get().getAsbManager().getASBSession(id);
    }
}
