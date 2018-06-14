package com.ghstudios.android.features.weapons.detail;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;

public class WeaponBowDetailFragment extends WeaponDetailFragment {

    private TextView mWeaponArcTextView, mWeaponCharge1TextView,
            mWeaponCharge2TextView, mWeaponCharge3TextView,
            mWeaponCharge4TextView;

    public static WeaponBowDetailFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId);
        WeaponBowDetailFragment f = new WeaponBowDetailFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weapon_bow_detail,
                container, false);

        mWeaponRarityTextView = (TextView) view
                .findViewById(R.id.detail_weapon_rarity);
        mWeaponDefenseTextView = (TextView) view
                .findViewById(R.id.detail_weapon_defense);
        mWeaponDefenseTextTextView = (TextView) view
                .findViewById(R.id.detail_weapon_defense_text);
        mWeaponCreationTextView = (TextView) view
                .findViewById(R.id.detail_weapon_creation);
        mWeaponUpgradeTextView = (TextView) view
                .findViewById(R.id.detail_weapon_upgrade);

        mWeaponArcTextView = (TextView) view
                .findViewById(R.id.detail_weapon_bow_arc);
        mWeaponCharge1TextView = (TextView) view
                .findViewById(R.id.detail_weapon_bow_charge1);
        mWeaponCharge2TextView = (TextView) view
                .findViewById(R.id.detail_weapon_bow_charge2);
        mWeaponCharge3TextView = (TextView) view
                .findViewById(R.id.detail_weapon_bow_charge3);
        mWeaponCharge4TextView = (TextView) view
                .findViewById(R.id.detail_weapon_bow_charge4);
        mWeaponDescription = (TextView)view.findViewById(R.id.detail_weapon_description);


        return view;
    }

    @Override
    protected void populateWeapon(Weapon mWeapon) {
        super.populateWeapon(mWeapon);

        mWeaponArcTextView.setText(mWeapon.getRecoil());

        // Charges
        String[] charges = mWeapon.getCharges().split("\\|");

        /* charges[0] maps to mWeaponCharge1TextView
         * charges[1] maps to mWeaponCharge2TextView
         * etc. */
        mWeaponCharge1TextView.setText(charges[0]);
        mWeaponCharge2TextView.setText(charges[1]);

        if (charges.length >= 3) {
            String thirdCharge = charges[2];
            if (thirdCharge.contains("*")){
                thirdCharge = thirdCharge.replace("*", "");
                mWeaponCharge3TextView.setTypeface(null, Typeface.BOLD);
            }
            mWeaponCharge3TextView.setText(thirdCharge);
        }
        else {
            mWeaponCharge3TextView.setText("None");
        }

        if (charges.length == 4) {
            String fourthCharge = charges[3];
            if (fourthCharge.contains("*")) {
                fourthCharge = fourthCharge.replace("*", "");
                mWeaponCharge4TextView.setTypeface(null, Typeface.BOLD);
            }
            mWeaponCharge4TextView.setText(fourthCharge);
        }
        else {
            mWeaponCharge4TextView.setText("None");
        }
    }
}
