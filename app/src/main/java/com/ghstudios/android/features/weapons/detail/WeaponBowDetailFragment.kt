package com.ghstudios.android.features.weapons.detail

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R

class WeaponBowDetailFragment : WeaponDetailFragment() {

    companion object {
        @JvmStatic fun newInstance(weaponId: Long): WeaponBowDetailFragment {
            val args = Bundle()
            args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId)
            val f = WeaponBowDetailFragment()
            f.arguments = args
            return f
        }
    }

    private var mWeaponArcTextView: TextView? = null
    private var mWeaponCharge1TextView: TextView? = null
    private var mWeaponCharge2TextView: TextView? = null
    private var mWeaponCharge3TextView: TextView? = null
    private var mWeaponCharge4TextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weapon_bow_detail,
                container, false)

        mWeaponDefenseTextView = view
                .findViewById<View>(R.id.detail_weapon_defense) as TextView
        mWeaponDefenseTextTextView = view
                .findViewById<View>(R.id.detail_weapon_defense_text) as TextView
        mWeaponCreationTextView = view
                .findViewById<View>(R.id.detail_weapon_creation) as TextView
        mWeaponUpgradeTextView = view
                .findViewById<View>(R.id.detail_weapon_upgrade) as TextView

        mWeaponArcTextView = view
                .findViewById<View>(R.id.detail_weapon_bow_arc) as TextView
        mWeaponCharge1TextView = view
                .findViewById<View>(R.id.detail_weapon_bow_charge1) as TextView
        mWeaponCharge2TextView = view
                .findViewById<View>(R.id.detail_weapon_bow_charge2) as TextView
        mWeaponCharge3TextView = view
                .findViewById<View>(R.id.detail_weapon_bow_charge3) as TextView
        mWeaponCharge4TextView = view
                .findViewById<View>(R.id.detail_weapon_bow_charge4) as TextView
        mWeaponDescription = view.findViewById<View>(R.id.detail_weapon_description) as TextView


        return view
    }

    override fun populateWeapon(mWeapon: Weapon?) {
        super.populateWeapon(mWeapon)

        mWeaponArcTextView!!.text = mWeapon!!.recoil

        // Charges
        val charges = mWeapon.charges.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        /* charges[0] maps to mWeaponCharge1TextView
         * charges[1] maps to mWeaponCharge2TextView
         * etc. */
        mWeaponCharge1TextView!!.text = charges[0]
        mWeaponCharge2TextView!!.text = charges[1]

        if (charges.size >= 3) {
            var thirdCharge = charges[2]
            if (thirdCharge.contains("*")) {
                thirdCharge = thirdCharge.replace("*", "")
                mWeaponCharge3TextView!!.setTypeface(null, Typeface.BOLD)
            }
            mWeaponCharge3TextView!!.text = thirdCharge
        } else {
            mWeaponCharge3TextView!!.text = "None"
        }

        if (charges.size == 4) {
            var fourthCharge = charges[3]
            if (fourthCharge.contains("*")) {
                fourthCharge = fourthCharge.replace("*", "")
                mWeaponCharge4TextView!!.setTypeface(null, Typeface.BOLD)
            }
            mWeaponCharge4TextView!!.text = fourthCharge
        } else {
            mWeaponCharge4TextView!!.text = "None"
        }
    }
}
