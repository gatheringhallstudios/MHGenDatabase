package com.ghstudios.android.ui.detail;

import java.io.IOException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabaseold.R;

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

		//mWeaponLabelTextView = (TextView) view
		//		.findViewById(R.id.detail_weapon_name);
		mWeaponTypeTextView = (TextView) view
				.findViewById(R.id.detail_title_bar_text);
		mWeaponDescription = (TextView)view.findViewById(R.id.detail_weapon_description);
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
		mWeaponDefenseTextTextView=(TextView) view
				.findViewById(R.id.detail_weapon_defense_text);
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
		//mWeaponSpecialTypeTextView = (TextView) view
		//		.findViewById(R.id.detail_weapon_bowgun_special);
		
//		mSpecial1 = (TextView) view.findViewById(R.id.special1);
//		mSpecial2 = (TextView) view.findViewById(R.id.special2);
//		mSpecial3 = (TextView) view.findViewById(R.id.special3);
//		mSpecial4 = (TextView) view.findViewById(R.id.special4);
//		mSpecial5 = (TextView) view.findViewById(R.id.special5);
//
//		mValue1 = (TextView) view.findViewById(R.id.value1);
//		mValue2 = (TextView) view.findViewById(R.id.value2);
//		mValue3 = (TextView) view.findViewById(R.id.value3);
//		mValue4 = (TextView) view.findViewById(R.id.value4);
//		mValue5 = (TextView) view.findViewById(R.id.value5);
		
		return view;
	}

	@Override
	protected void updateUI() throws IOException {
		super.updateUI();

		mWeaponReloadTextView.setText(mWeapon.getReloadSpeed());
		mWeaponRecoilTextView.setText(mWeapon.getRecoil());
		mWeaponSteadinessTextView.setText(mWeapon.getDeviation());

//		if (mWeapon.getWtype().equals("Light Bowgun")) {
//			mWeaponSpecialTypeTextView.setText("Rapid Fire:");
//		}
//		else if (mWeapon.getWtype().equals("Heavy Bowgun")) {
//			mWeaponSpecialTypeTextView.setText("Crouching Fire:");
//		}

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

}
