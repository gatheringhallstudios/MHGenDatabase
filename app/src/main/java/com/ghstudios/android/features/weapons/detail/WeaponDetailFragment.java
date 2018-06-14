package com.ghstudios.android.features.weapons.detail;

import java.io.IOException;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.ghstudios.android.AppSettings;
import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.ColumnLabelTextCell;
import com.ghstudios.android.components.TitleBarCell;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.features.weapons.WeaponDetailViewModel;
import com.ghstudios.android.loader.WeaponLoader;
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
    private ColumnLabelTextCell attackCell;
    private ColumnLabelTextCell element1Cell;
    private ColumnLabelTextCell element2Cell;
    private ColumnLabelTextCell affinityCell;
    private ColumnLabelTextCell slotsCell;

    protected TextView mWeaponDescription,
            mWeaponRarityTextView, mWeaponDefenseTextView,
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
        attackCell = view.findViewById(R.id.attack);
        element1Cell = view.findViewById(R.id.element1);
        element2Cell = view.findViewById(R.id.element2);
        affinityCell = view.findViewById(R.id.affinity);
        slotsCell = view.findViewById(R.id.slots);

        viewModel.getWeaponData().observe(this, this::populateWeapon);
    }

    protected void populateWeapon(Weapon weapon) {
        Drawable weaponIcon = MHUtils.loadAssetDrawable(getContext(), weapon.getItemImage());
        titleBar.setIconDrawable(weaponIcon);
        titleBar.setTitleText(weapon.getName());
        titleBar.setAltTitleText(weapon.getJpnName());
        titleBar.setAltTitleEnabled(AppSettings.isJapaneseEnabled());

        attackCell.setValueText("" + weapon.getAttack());
        affinityCell.setValueText(weapon.getAffinity()+"%");
        slotsCell.setValueText("" + weapon.getSlotString());

//
//        /* Element */
//        if (!mWeapon.getElement().equals(""))
//        {
//            mWeaponElementTextView.setText(Long.toString(mWeapon.getElementAttack()));
//            mWeaponElementTypeTextView.setText(mWeapon.getElement());
//        }
//        else
//        {
//            mWeaponElementTextView.setText("0");
//            mWeaponElementTypeTextView.setText("None");
//        }
//
//        /* Element 2 */
//        if (!"".equals(mWeapon.getElement2())) {
//            mWeaponElement2TypeTextView.setText(mWeapon.getElement2());
//            mWeaponElement2TextView.setText(Long.toString(mWeapon.getElement2Attack()));
//        }else{
//            mWeaponElement2Layout.setVisibility(View.GONE);
//        }


        /*
         * Items below are from old code
         */

        mWeaponRarityTextView.setText("" + weapon.getRarity());
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
}
