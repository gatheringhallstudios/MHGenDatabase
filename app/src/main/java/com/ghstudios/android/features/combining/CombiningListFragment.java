package com.ghstudios.android.features.combining;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.ghstudios.android.RecyclerViewFragment;
import com.ghstudios.android.adapter.ItemCombinationAdapterDelegate;
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter;
import com.ghstudios.android.data.classes.Combining;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;

public class CombiningListFragment extends RecyclerViewFragment {
    private BasicListDelegationAdapter<Combining> adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        AdapterDelegate delegate = new ItemCombinationAdapterDelegate();
        adapter = new BasicListDelegationAdapter<Combining>(delegate);
        setAdapter(adapter);
        enableDivider();

        CombiningListViewModel viewModel = ViewModelProviders.of(this).get(CombiningListViewModel.class);
        viewModel.getCombinationData().observe(this, (data) -> {
            adapter.setItems(data);
            adapter.notifyDataSetChanged();
        });
    }
}
