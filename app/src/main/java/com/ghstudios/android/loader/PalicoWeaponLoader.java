package com.ghstudios.android.loader;

import android.content.Context;

import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.DataManager;

/**
 * Created by Joseph on 7/11/2016.
 */
public class PalicoWeaponLoader extends DataLoader<PalicoWeapon> {

    long id;

    public PalicoWeaponLoader(Context context, long id) {
        super(context);
        this.id = id;
    }

    @Override
    public PalicoWeapon loadInBackground() {
        return DataManager.get().getPalicoWeapon(id);
    }
}
