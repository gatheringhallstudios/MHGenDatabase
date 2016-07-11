package com.ghstudios.android.ui.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by Joseph on 7/10/2016.
 */
public class PalicoWeaponDetailFragment extends Fragment {

    public static String EXTRA_WEAPON_ID="WEAPON_ID";

    public static PalicoWeaponDetailFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_WEAPON_ID, id);
        PalicoWeaponDetailFragment f = new PalicoWeaponDetailFragment();
        f.setArguments(args);
        return f;
    }



}
