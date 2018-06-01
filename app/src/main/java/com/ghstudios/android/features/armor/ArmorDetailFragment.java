package com.ghstudios.android.features.armor;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.armorsetbuilder.ASBPagerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArmorDetailFragment extends Fragment {
    private static final String ARG_ARMOR_ID = "ARMOR_ID";

    private ArmorViewModel viewModel;

    @BindView(R.id.titlebar)
    TitleBarCell titleBar;

    @BindView(R.id.rare) ColumnLabelTextCell rareView;
    @BindView(R.id.slots) ColumnLabelTextCell slotsReqView;
    @BindView(R.id.defense) ColumnLabelTextCell defenseView;
    @BindView(R.id.part) ColumnLabelTextCell partView;
    
    private TextView fireResTextView;
    private TextView waterResTextView;
    private TextView iceResTextView;
    private TextView thunderResTextView;
    private TextView dragonResTextView;

    public static ArmorDetailFragment newInstance(long armorId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ARMOR_ID, armorId);
        ArmorDetailFragment f = new ArmorDetailFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        viewModel = ViewModelProviders.of(this).get(ArmorViewModel.class);

        // Check for a Item ID as an argument, and find the item
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        long armorId = args.getLong(ARG_ARMOR_ID, -1);
        viewModel.loadArmor(armorId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_armor_detail,
                container, false);

        ButterKnife.bind(this, view);

        fireResTextView = view.findViewById(R.id.fire_res);
        waterResTextView = view.findViewById(R.id.water_res);
        iceResTextView = view.findViewById(R.id.ice_res);
        thunderResTextView = view.findViewById(R.id.thunder_res);
        dragonResTextView = view.findViewById(R.id.dragon_res);

        // If the originator of this fragment's activity was the Armor Set Builder...
        if (getActivity().getIntent().getBooleanExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, false)) {
            Button selectButton = view.findViewById(R.id.select_button);
            selectButton.setVisibility(View.VISIBLE);
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long armorId = getArguments().getLong(ARG_ARMOR_ID);
                    Intent intent = getActivity().getIntent();
                    intent.putExtra(ArmorDetailPagerActivity.EXTRA_ARMOR_ID, armorId);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
            });
        }
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel.getArmorData().observe(this, this::updateUI);
    }

    private void updateUI(Armor armor) {
        Drawable itemImage = MHUtils.loadAssetDrawable(getContext(), armor.getItemImage());

        titleBar.setTitleText(armor.getName());
        titleBar.setIconDrawable(itemImage);
        titleBar.setAltTitleText(armor.getJpnName());
        titleBar.setAltTitleEnabled(AppSettings.isJapaneseEnabled());

        String cellPart = "" + armor.getSlot();
        String cellDefense = "" + armor.getDefense() + "~" + armor.getMaxDefense();
        String cellSlot = armor.getSlotString();
        String cellRare = "" + armor.getRarity();

        rareView.setValueText(cellRare);
        slotsReqView.setValueText(cellSlot);
        partView.setValueText(cellPart);
        defenseView.setValueText(cellDefense);
        
        fireResTextView.setText(String.valueOf(armor.getFireRes()));
        waterResTextView.setText(String.valueOf(armor.getWaterRes()));
        iceResTextView.setText(String.valueOf(armor.getIceRes()));
        thunderResTextView.setText(String.valueOf(armor.getThunderRes()));
        dragonResTextView.setText(String.valueOf(armor.getDragonRes()));
    }
}
