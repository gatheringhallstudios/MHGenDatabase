package com.ghstudios.android.features.items.detail;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.adapter.ItemCombinationAdapterDelegate;
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter;
import com.ghstudios.android.components.LabelValueComponent;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemDetailFragment extends Fragment {
    private static final String ARG_ITEM_ID = "ITEM_ID";

    @BindView(R.id.item_title) TitleBarCell titleCell;

    @BindView(R.id.carry) LabelValueComponent carryCell;
    @BindView(R.id.buy) LabelValueComponent buyCell;
    @BindView(R.id.sell) LabelValueComponent sellCell;

    @BindView(R.id.description) TextView descriptionTextView;

    @BindView(R.id.combination_section) ViewGroup combinationSection;
    @BindView(R.id.craft_combinations) RecyclerView combinationList;

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
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        ButterKnife.bind(this, view);

        return view;
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

            combinationSection.setVisibility(View.VISIBLE);

            // DO NOT PUT ADAPTER AS AN INSTANCE VARIABLE OF THE FRAGMENT (or it'll leak)
            ItemCombinationAdapterDelegate delegate = new ItemCombinationAdapterDelegate();
            delegate.setResultItemNavigationEnabled(false);
            BasicListDelegationAdapter<Object> adapter = new BasicListDelegationAdapter<>(delegate);
            adapter.setItems(items);

            combinationList.setAdapter(adapter);
        });
    }

    private void populateItem(Item mItem) {
        // Set title icon and image
        titleCell.setIcon(mItem);
        titleCell.setTitleText(mItem.getName());
        titleCell.setAltTitleText(getString(R.string.value_rare, mItem.getRarityString()));

        descriptionTextView.setText(mItem.getDescription());

        String cellSell = "" + mItem.getSell() + "z";
        String cellBuy = "" + mItem.getBuy() + "z";
        
        if (cellBuy.equals("0z")) {
            cellBuy = "-";
        }
        if (cellSell.equals("0z")) {
            cellSell = "-";
        }

        carryCell.setValueText(String.valueOf(mItem.getCarryCapacity()));
        buyCell.setValueText(cellBuy);
        sellCell.setValueText(cellSell);
    }
}
