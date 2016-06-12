package com.ghstudios.android.ui.detail;

import java.io.IOException;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

public class WeaponBowgunDetailFragment extends WeaponDetailFragment {

	private TextView mWeaponReloadTextView, mWeaponRecoilTextView,
			mWeaponSteadinessTextView, mWeaponSpecialTypeTextView;

	private TextView mSpecial1, mSpecial2, mSpecial3, mSpecial4, mSpecial5,
			mValue1, mValue2, mValue3, mValue4, mValue5;

	TextView[] mAmmoTextViews;

	public static WeaponBowgunDetailFragment newInstance(long weaponId) {
		Bundle args = new Bundle();
		args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId);
		WeaponBowgunDetailFragment f = new WeaponBowgunDetailFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_weapon_bowgun_detail,
				container, false);

		mWeaponLabelTextView = (TextView) view
				.findViewById(R.id.detail_weapon_name);
		mWeaponTypeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_type);
		mWeaponAttackTextView = (TextView) view
				.findViewById(R.id.detail_weapon_attack);
		mWeaponRarityTextView = (TextView) view
				.findViewById(R.id.detail_weapon_rarity);
		mWeaponSlotTextView = (TextView) view
				.findViewById(R.id.detail_weapon_slot);
		mWeaponAffinityTextView = (TextView) view
				.findViewById(R.id.detail_weapon_affinity);
		mWeaponDefenseTextView = (TextView) view
				.findViewById(R.id.detail_weapon_defense);
		mWeaponCreationTextView = (TextView) view
				.findViewById(R.id.detail_weapon_creation);
		mWeaponUpgradeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_upgrade);

		mWeaponReloadTextView = (TextView) view
				.findViewById(R.id.detail_weapon_bowgun_reload);
		mWeaponRecoilTextView = (TextView) view
				.findViewById(R.id.detail_weapon_bowgun_recoil);
		mWeaponSteadinessTextView = (TextView) view
				.findViewById(R.id.detail_weapon_bowgun_steadiness);
		mWeaponSpecialTypeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_bowgun_special);

		mAmmoTextViews = new TextView[]{
				(TextView) view.findViewById(R.id.normal1),
				(TextView) view.findViewById(R.id.normal2),
				(TextView) view.findViewById(R.id.normal3),
				(TextView) view.findViewById(R.id.pierce1),
				(TextView) view.findViewById(R.id.pierce2),
				(TextView) view.findViewById(R.id.pierce3),
				(TextView) view.findViewById(R.id.pellet1),
				(TextView) view.findViewById(R.id.pellet2),
				(TextView) view.findViewById(R.id.pellet3),
				(TextView) view.findViewById(R.id.crag1),
				(TextView) view.findViewById(R.id.crag2),
				(TextView) view.findViewById(R.id.crag3),
				(TextView) view.findViewById(R.id.clust1),
				(TextView) view.findViewById(R.id.clust2),
				(TextView) view.findViewById(R.id.clust3),
				(TextView) view.findViewById(R.id.flaming),
				(TextView) view.findViewById(R.id.water),
				(TextView) view.findViewById(R.id.thunder),
				(TextView) view.findViewById(R.id.freeze),
				(TextView) view.findViewById(R.id.dragon),
				(TextView) view.findViewById(R.id.poison1),
				(TextView) view.findViewById(R.id.poison2),
				(TextView) view.findViewById(R.id.para1),
				(TextView) view.findViewById(R.id.para2),
				(TextView) view.findViewById(R.id.sleep1),
				(TextView) view.findViewById(R.id.sleep2),
				(TextView) view.findViewById(R.id.exhaust1),
				(TextView) view.findViewById(R.id.exhaust2)
		};
		
		mSpecial1 = (TextView) view.findViewById(R.id.special1);
		mSpecial2 = (TextView) view.findViewById(R.id.special2);
		mSpecial3 = (TextView) view.findViewById(R.id.special3);
		mSpecial4 = (TextView) view.findViewById(R.id.special4);
		mSpecial5 = (TextView) view.findViewById(R.id.special5);
		
		mValue1 = (TextView) view.findViewById(R.id.value1);
		mValue2 = (TextView) view.findViewById(R.id.value2);
		mValue3 = (TextView) view.findViewById(R.id.value3);
		mValue4 = (TextView) view.findViewById(R.id.value4);
		mValue5 = (TextView) view.findViewById(R.id.value5);
		
		return view;
	}

	@Override
	protected void updateUI() throws IOException {
		super.updateUI();

		mWeaponReloadTextView.setText(mWeapon.getReloadSpeed());
		mWeaponRecoilTextView.setText(mWeapon.getRecoil());
		mWeaponSteadinessTextView.setText(mWeapon.getDeviation());
		
		String[] ammos = mWeapon.getAmmo().split("\\|");

        TextView ammoView;
		
		for(int i=0;i<mAmmoTextViews.length;i++) {
            ammoView = mAmmoTextViews[i];
			setAmmoText(ammos[i], ammoView);
		}

		if (mWeapon.getWtype().equals("Light Bowgun")) {
			mWeaponSpecialTypeTextView.setText("Rapid Fire:");
		}
		else if (mWeapon.getWtype().equals("Heavy Bowgun")) {
			mWeaponSpecialTypeTextView.setText("Crouching Fire:");
		}

//		if (!mWeapon.getSpecialAmmo().isEmpty()) {
//			String[] specials = mWeapon.getSpecialAmmo().split("\\|");
//			int numSpecial = specials.length;
//
//			if (numSpecial >= 1) {
//				String[] tempSpecial = specials[0].split(" ");
//				mSpecial1.setText(tempSpecial[0]);
//				mValue1.setText(tempSpecial[1]);
//			}
//			if (numSpecial >= 2) {
//				String[] tempSpecial = specials[1].split(" ");
//				mSpecial2.setText(tempSpecial[0]);
//				mValue2.setText(tempSpecial[1]);
//			}
//			if (numSpecial >= 3) {
//				String[] tempSpecial = specials[2].split(" ");
//				mSpecial3.setText(tempSpecial[0]);
//				mValue3.setText(tempSpecial[1]);
//			}
//			if (numSpecial >= 4) {
//				String[] tempSpecial = specials[3].split(" ");
//				mSpecial4.setText(tempSpecial[0]);
//				mValue4.setText(tempSpecial[1]);
//			}
//			if (numSpecial == 5) {
//				String[] tempSpecial = specials[4].split(" ");
//				mSpecial5.setText(tempSpecial[0]);
//				mValue5.setText(tempSpecial[1]);
//			}
//		}
//		else
//		{
//			mWeaponSpecialTypeTextView.setText("");
//		}
	}
	
	private void setAmmoText(String a, TextView ammoView) {
        Boolean loadUp = false;

        if (a.contains("*"))
        {
            loadUp = true;
            a = a.substring(0,a.length()-1);
        }

        ammoView.setText(a);

        if (loadUp) {
            ammoView.setTypeface(null, Typeface.BOLD);
			ammoView.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_focused));
        }
	}
}
