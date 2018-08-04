package com.ghstudios.android.features.weapons.detail;

import java.util.List;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;

/**
 * The superclass of all weapon detail fragments.
 * TODO: Fragments do not lend themselves well to being inherited for unique views.
 * Find an alternative way to handle it like custom view groups, or move more functionality
 * to the superclass
 */
public class WeaponDetailFragment extends Fragment {
    protected static final String ARG_WEAPON_ID = "WEAPON_ID";

    protected WeaponDetailViewModel viewModel;

    private TitleBarCell titleBar;
    private ColumnLabelTextCell rarityCell;
    private ColumnLabelTextCell attackCell;
    private ColumnLabelTextCell element1Cell;
    private ColumnLabelTextCell element2Cell;
    private ColumnLabelTextCell affinityCell;
    private ColumnLabelTextCell slotsCell;

    protected TextView mWeaponDescription, mWeaponDefenseTextView,
            mWeaponDefenseTextTextView,
            mWeaponCreationTextView, mWeaponUpgradeTextView;

    public static WeaponDetailFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(ARG_WEAPON_ID, weaponId);
        WeaponDetailFragment f = new WeaponDetailFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(WeaponDetailViewModel.class);

        // Check for a Weapon ID as an argument, and find the weapon
        Bundle args = getArguments();
        if (args != null) {
            long weaponId = args.getLong(ARG_WEAPON_ID, -1);
            viewModel.loadWeapon(weaponId);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind general view elements
        // subclasses implement onCreateView, so we have to do it here instead
        titleBar = view.findViewById(R.id.titlebar);
        rarityCell = view.findViewById(R.id.rare);
        attackCell = view.findViewById(R.id.attack);
        element1Cell = view.findViewById(R.id.element1);
        element2Cell = view.findViewById(R.id.element2);
        affinityCell = view.findViewById(R.id.affinity);
        slotsCell = view.findViewById(R.id.slots);

        viewModel.getWeaponData().observe(this, this::populateWeapon);
        viewModel.getWeaponElementData().observe(this, this::populateElementData);
    }

    protected void populateWeapon(Weapon weapon) {
        titleBar.setIcon(weapon);
        titleBar.setTitleText(weapon.getName());
        titleBar.setAltTitleText(weapon.getJpnName());
        titleBar.setAltTitleEnabled(AppSettings.isJapaneseEnabled());

        rarityCell.setValueText(weapon.getRarityString());
        attackCell.setValueText("" + weapon.getAttack());
        affinityCell.setValueText(weapon.getAffinity()+"%");
        slotsCell.setValueText("" + weapon.getSlotString());

        /*
         * Items below are from old code
         */

        mWeaponDescription.setText(weapon.getDescription());

        if(weapon.getDefense()==0)
        {
            mWeaponDefenseTextTextView.setVisibility(View.GONE);
            mWeaponDefenseTextView.setVisibility(View.GONE);
        }
        else
            mWeaponDefenseTextView.setText("" + weapon.getDefense());


        
        String createCost = "" + weapon.getCreationCost() + "z";
        String upgradeCost = "" + weapon.getUpgradeCost() + "z";
        
        if (createCost.equals("0z")) {
            createCost = "-";
        }
        if (upgradeCost.equals("0z")) {
            upgradeCost = "-";
        }
        
        mWeaponCreationTextView.setText(createCost);
        mWeaponUpgradeTextView.setText(upgradeCost);
    }

    private void populateElementData(List<WeaponElementData> items) {
        element1Cell.setVisibility(View.GONE);
        element2Cell.setVisibility(View.GONE);

        if (items == null) {
            return;
        }

        if (items.size() >= 1) {
            WeaponElementData data = items.get(0);
            element1Cell.setLabelText(data.getElement());
            element1Cell.setValueText(String.valueOf(data.getValue()));
            element1Cell.setVisibility(View.VISIBLE);
        }

        if (items.size() >= 2) {
            WeaponElementData data = items.get(1);
            element2Cell.setLabelText(data.getElement());
            element2Cell.setValueText(String.valueOf(data.getValue()));
            element2Cell.setVisibility(View.VISIBLE);
        }
    }
}
