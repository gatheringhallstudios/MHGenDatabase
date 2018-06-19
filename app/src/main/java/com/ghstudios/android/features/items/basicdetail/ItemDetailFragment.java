package com.ghstudios.android.features.items.basicdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.adapter.ItemCombinationAdapterDelegate;
import com.ghstudios.android.adapter.common.BasicListDelegationAdapter;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.features.items.ItemDetailViewModel;
import com.ghstudios.android.mhgendatabase.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemDetailFragment extends Fragment {
    private static final String ARG_ITEM_ID = "ITEM_ID";

    @BindView(R.id.item_title) TitleBarCell titleCell;

    @BindView(R.id.rare) ColumnLabelTextCell rareCell;
    @BindView(R.id.carry) ColumnLabelTextCell carryCell;
    @BindView(R.id.buy) ColumnLabelTextCell buyCell;
    @BindView(R.id.sell) ColumnLabelTextCell sellCell;

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
        ItemDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(ItemDetailViewModel.class);
        viewModel.getItemData().observe(this, this::populateItem);

        viewModel.getCraftData().observe(this, (items) -> {
            if (items == null || items.isEmpty()) {
                return;
            }

            combinationSection.setVisibility(View.VISIBLE);

            // DO NOT PUT ADAPTER AS AN INSTANCE VARIABLE (or it'll leak)
            ItemCombinationAdapterDelegate delegate = new ItemCombinationAdapterDelegate();
            delegate.setShowSideMargins(false);
            delegate.setResultItemNavigationEnabled(false);
            BasicListDelegationAdapter<Object> adapter = new BasicListDelegationAdapter<>(delegate);
            adapter.setItems(items);

            combinationList.setAdapter(adapter);
        });
    }

    private void populateItem(Item mItem) {
        // Set title icon and image
        Drawable itemImage = MHUtils.loadAssetDrawable(getContext(), mItem.getItemImage());
        titleCell.setIconDrawable(itemImage);
        titleCell.setTitleText(mItem.getName());
        titleCell.setAltTitleText(mItem.getJpnName());
        titleCell.setAltTitleEnabled(AppSettings.isJapaneseEnabled());

        String cellSell = "" + mItem.getSell() + "z";
        String cellBuy = "" + mItem.getBuy() + "z";
        
        if (cellBuy.equals("0z")) {
            cellBuy = "-";
        }
        if (cellSell.equals("0z")) {
            cellSell = "-";
        }

        rareCell.setValueText(String.valueOf(mItem.getRarity()));
        carryCell.setValueText(String.valueOf(mItem.getCarryCapacity()));
        buyCell.setValueText(cellBuy);
        sellCell.setValueText(cellSell);

        descriptionTextView.setText(mItem.getDescription());
    }

}
