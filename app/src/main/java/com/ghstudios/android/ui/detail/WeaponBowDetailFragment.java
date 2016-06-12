package com.ghstudios.android.ui.detail;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;

public class WeaponBowDetailFragment extends WeaponDetailFragment {

	private TextView mWeaponArcTextView, mWeaponCharge1TextView,
			mWeaponCharge2TextView, mWeaponCharge3TextView,
			mWeaponCharge4TextView, mWeaponElementTextView;

	private ImageView mWeaponCoating1ImageView, mWeaponCoating2ImageView,
			mWeaponCoating3ImageView, mWeaponCoating4ImageView,
			mWeaponCoating5ImageView, mWeaponCoating6ImageView,
			mWeaponCoating7ImageView, mWeaponCoating8ImageView;

	private TextView mPower1TextView, mPower2TextView,
			mElemental1TextView, mElemental2TextView,
			mCRangeTextView, mPoisonTextView,
			mParaTextView, mSleepTextView,
			mExhaustTextView,mBlastTextView,mPaintTextView;

	TextView[] mCoatingTextViews;

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

		mWeaponLabelTextView = (TextView) view
				.findViewById(R.id.detail_weapon_name);
		mWeaponTypeTextView = (TextView) view
				.findViewById(R.id.detail_weapon_type);
		mWeaponAttackTextView = (TextView) view
				.findViewById(R.id.detail_weapon_attack);
		mWeaponElementTextView = (TextView) view
				.findViewById(R.id.detail_weapon_element);
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

		mCoatingTextViews = new TextView[]{
				(TextView)view.findViewById(R.id.power_1_text),
				(TextView)view.findViewById(R.id.power_2_text),
				(TextView)view.findViewById(R.id.element_1_text),
				(TextView)view.findViewById(R.id.element_2_text),
				(TextView)view.findViewById(R.id.crange_text),
				(TextView)view.findViewById(R.id.poison_text),
				(TextView)view.findViewById(R.id.para_text),
				(TextView)view.findViewById(R.id.sleep_text),
				(TextView)view.findViewById(R.id.exhaust_text),
				(TextView)view.findViewById(R.id.blast_text),
				(TextView)view.findViewById(R.id.paint_text)
		};


		return view;
	}

	@Override
	protected void updateUI() throws IOException {
		super.updateUI();

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

		// Read a Bitmap from Assets
		AssetManager manager = getActivity().getAssets();
		InputStream open = null;
		Bitmap bitmap = null;

		/* Coatings */
		int coatings = Integer.parseInt(mWeapon.getCoatings());

		for(int i=10;i>=0;i--){
			boolean show = (coatings & (1 << i))>0;
			if(show){
				mCoatingTextViews[10-i].setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_focused));
				mCoatingTextViews[10-i].setTypeface(null,Typeface.BOLD);
			}
		}

        /* Element */
        String element = "";
        if (!mWeapon.getElement().equals(""))
        {
            element = mWeapon.getElement() + " " + mWeapon.getElementAttack();
        }
        else if (!mWeapon.getAwaken().equals(""))
        {
            element = mWeapon.getAwaken() + " " + mWeapon.getAwakenAttack();
        }
        else
        {
            element = "None";
        }

        if (!"".equals(mWeapon.getElement2())) {
            element = element + ", " + mWeapon.getElement2() + " " + mWeapon.getElement2Attack();
        }

        if (!mWeapon.getAwaken().equals(""))
        {
            element = "(" + element + ")";
        }

        mWeaponElementTextView.setText(element);
	}
}
