package com.ghstudios.android.ui.list;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghstudios.android.mhgendatabase.R;

/**
 * Created by Joseph on 7/9/2016.
 */
public class PalicoArmorListFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        return inflater
                .inflate(R.layout.fragment_generic_list, parent, false);
    }
}
