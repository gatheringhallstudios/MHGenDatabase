package com.ghstudios.android.features.weapons.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R

class WeaponBowgunDetailFragment : WeaponDetailFragment() {

    companion object {
        @JvmStatic fun newInstance(weaponId: Long): WeaponBowgunDetailFragment {
            val args = Bundle()
            args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId)
            val f = WeaponBowgunDetailFragment()
            f.arguments = args
            return f
        }
    }

    private var mWeaponReloadTextView: TextView? = null
    private var mWeaponRecoilTextView: TextView? = null
    private var mWeaponSteadinessTextView: TextView? = null
    private val mWeaponSpecialTypeTextView: TextView? = null

    private val mSpecial1: TextView? = null
    private val mSpecial2: TextView? = null
    private val mSpecial3: TextView? = null
    private val mSpecial4: TextView? = null
    private val mSpecial5: TextView? = null
    private val mValue1: TextView? = null
    private val mValue2: TextView? = null
    private val mValue3: TextView? = null
    private val mValue4: TextView? = null
    private val mValue5: TextView? = null

    internal var mAmmoTextViews: Array<TextView>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weapon_bowgun_detail,
                container, false)

        //mWeaponLabelTextView = (TextView) view
        //        .findViewById(R.id.detail_weapon_name);
        mWeaponDescription = view.findViewById<View>(R.id.detail_weapon_description) as TextView
        mWeaponDefenseTextView = view
                .findViewById<View>(R.id.detail_weapon_defense) as TextView
        mWeaponDefenseTextTextView = view
                .findViewById<View>(R.id.detail_weapon_defense_text) as TextView
        mWeaponCreationTextView = view
                .findViewById<View>(R.id.detail_weapon_creation) as TextView
        mWeaponUpgradeTextView = view
                .findViewById<View>(R.id.detail_weapon_upgrade) as TextView

        mWeaponReloadTextView = view
                .findViewById<View>(R.id.detail_weapon_bowgun_reload) as TextView
        mWeaponRecoilTextView = view
                .findViewById<View>(R.id.detail_weapon_bowgun_recoil) as TextView
        mWeaponSteadinessTextView = view
                .findViewById<View>(R.id.detail_weapon_bowgun_steadiness) as TextView
        //mWeaponSpecialTypeTextView = (TextView) view
        //        .findViewById(R.id.detail_weapon_bowgun_special);

        //        mSpecial1 = (TextView) view.findViewById(R.id.special1);
        //        mSpecial2 = (TextView) view.findViewById(R.id.special2);
        //        mSpecial3 = (TextView) view.findViewById(R.id.special3);
        //        mSpecial4 = (TextView) view.findViewById(R.id.special4);
        //        mSpecial5 = (TextView) view.findViewById(R.id.special5);
        //
        //        mValue1 = (TextView) view.findViewById(R.id.value1);
        //        mValue2 = (TextView) view.findViewById(R.id.value2);
        //        mValue3 = (TextView) view.findViewById(R.id.value3);
        //        mValue4 = (TextView) view.findViewById(R.id.value4);
        //        mValue5 = (TextView) view.findViewById(R.id.value5);

        return view
    }

    override fun populateWeapon(weapon: Weapon?) {
        super.populateWeapon(weapon)

        mWeaponReloadTextView!!.text = weapon!!.reloadSpeed
        mWeaponRecoilTextView!!.text = weapon.recoil
        mWeaponSteadinessTextView!!.text = weapon.deviation

        //        if (mWeapon.getWtype().equals("Light Bowgun")) {
        //            mWeaponSpecialTypeTextView.setText("Rapid Fire:");
        //        }
        //        else if (mWeapon.getWtype().equals("Heavy Bowgun")) {
        //            mWeaponSpecialTypeTextView.setText("Crouching Fire:");
        //        }

        //        if (!mWeapon.getSpecialAmmo().isEmpty()) {
        //            String[] specials = mWeapon.getSpecialAmmo().split("\\|");
        //            int numSpecial = specials.length;
        //
        //            if (numSpecial >= 1) {
        //                String[] tempSpecial = specials[0].split(" ");
        //                mSpecial1.setText(tempSpecial[0]);
        //                mValue1.setText(tempSpecial[1]);
        //            }
        //            if (numSpecial >= 2) {
        //                String[] tempSpecial = specials[1].split(" ");
        //                mSpecial2.setText(tempSpecial[0]);
        //                mValue2.setText(tempSpecial[1]);
        //            }
        //            if (numSpecial >= 3) {
        //                String[] tempSpecial = specials[2].split(" ");
        //                mSpecial3.setText(tempSpecial[0]);
        //                mValue3.setText(tempSpecial[1]);
        //            }
        //            if (numSpecial >= 4) {
        //                String[] tempSpecial = specials[3].split(" ");
        //                mSpecial4.setText(tempSpecial[0]);
        //                mValue4.setText(tempSpecial[1]);
        //            }
        //            if (numSpecial == 5) {
        //                String[] tempSpecial = specials[4].split(" ");
        //                mSpecial5.setText(tempSpecial[0]);
        //                mValue5.setText(tempSpecial[1]);
        //            }
        //        }
        //        else
        //        {
        //            mWeaponSpecialTypeTextView.setText("");
        //        }
    }

}
