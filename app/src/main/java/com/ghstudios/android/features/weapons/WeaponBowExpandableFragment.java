package com.ghstudios.android.features.weapons;

import android.os.Bundle;
import android.view.View;

import com.ghstudios.android.adapter.WeaponExpandableListBowAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListGeneralAdapter;

/**
 * Created by Mark on 3/5/2015.
 */
public class WeaponBowExpandableFragment extends WeaponListFragment {

    public static WeaponBowExpandableFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(WeaponListFragment.ARG_TYPE, type);
        WeaponBowExpandableFragment f = new WeaponBowExpandableFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    protected WeaponExpandableListGeneralAdapter createWeaponAdapter() {
        return new WeaponExpandableListBowAdapter(getActivity(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = mRecyclerView.getChildPosition(v);
                mAdapter.toggleGroup(position);

                return true;
            }
        });
    }
}
