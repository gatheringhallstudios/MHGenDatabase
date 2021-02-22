package com.ghstudios.android.features.items.detail;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghstudios.android.adapter.ItemCombinationAdapterDelegate;
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.FragmentItemDetailBinding;


public class ItemDetailFragment extends Fragment {
    private static final String ARG_ITEM_ID = "ITEM_ID";

    private FragmentItemDetailBinding binding;

    public static ItemDetailFragment newInstance(long itemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        ItemDetailFragment f = new ItemDetailFragment();
        f.setArguments(args);
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // add divider for the combination list
        //combinationList.addItemDecoration(new RecyclerViewDivider(combinationList));

        // this uses the pager's view Model
        ItemDetailViewModel viewModel = new ViewModelProvider(getActivity()).get(ItemDetailViewModel.class);
        viewModel.getItemData().observe(getViewLifecycleOwner(), this::populateItem);

        viewModel.getCraftData().observe(getViewLifecycleOwner(), (items) -> {
            if (items == null || items.isEmpty()) {
                return;
            }

            binding.combinationSection.setVisibility(View.VISIBLE);

            // DO NOT PUT ADAPTER AS AN INSTANCE VARIABLE OF THE FRAGMENT (or it'll leak)
            ItemCombinationAdapterDelegate delegate = new ItemCombinationAdapterDelegate();
            delegate.setResultItemNavigationEnabled(false);
            BasicListDelegationAdapter<Object> adapter = new BasicListDelegationAdapter<>(delegate);
            adapter.setItems(items);

            binding.craftCombinations.setAdapter(adapter);
        });
    }

    private void populateItem(Item mItem) {
        // Set title icon and image
        binding.itemTitle.setIcon(mItem);
        binding.itemTitle.setTitleText(mItem.getName());
        binding.itemTitle.setAltTitleText(getString(R.string.value_rare, mItem.getRarityString()));

        binding.description.setText(mItem.getDescription());

        String cellSell = "" + mItem.getSell() + "z";
        String cellBuy = "" + mItem.getBuy() + "z";
        
        if (cellBuy.equals("0z")) {
            cellBuy = "-";
        }
        if (cellSell.equals("0z")) {
            cellSell = "-";
        }

        binding.carry.setValueText(String.valueOf(mItem.getCarryCapacity()));
        binding.buy.setValueText(cellBuy);
        binding.sell.setValueText(cellSell);
    }
}
