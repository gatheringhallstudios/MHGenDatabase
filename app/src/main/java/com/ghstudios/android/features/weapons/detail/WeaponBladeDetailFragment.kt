package com.ghstudios.android.features.weapons.detail

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.components.DrawSharpness
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R

class WeaponBladeDetailFragment : WeaponDetailFragment() {
    companion object {
        @JvmStatic fun newInstance(weaponId: Long): WeaponBladeDetailFragment {
            val args = Bundle()
            args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId)
            val f = WeaponBladeDetailFragment()
            f.arguments = args
            return f
        }
    }

    private var mWeaponSpecialTypeTextView: TextView? = null
    private var mWeaponSpecialTextView: TextView? = null
    private var mWeaponNoteText: TextView? = null
    private var mWeaponNote1ImageView: ImageView? = null
    private var mWeaponNote2ImageView: ImageView? = null
    private var mWeaponNote3ImageView: ImageView? = null
    private var mWeaponSharpnessDrawnView: DrawSharpness? = null
    private lateinit var NoteContainer: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weapon_blade_detail,
                container, false)

        mWeaponDescription = view
                .findViewById(R.id.detail_weapon_description)
        mWeaponSharpnessDrawnView = view
                .findViewById(R.id.detail_weapon_blade_sharpness)
        mWeaponDefenseTextView = view
                .findViewById(R.id.detail_weapon_defense)
        mWeaponDefenseTextTextView = view
                .findViewById(R.id.detail_weapon_defense_text)
        mWeaponCreationTextView = view
                .findViewById(R.id.detail_weapon_creation)
        mWeaponUpgradeTextView = view
                .findViewById(R.id.detail_weapon_upgrade)
        mWeaponSpecialTypeTextView = view
                .findViewById(R.id.detail_weapon_blade_special)
        mWeaponSpecialTextView = view
                .findViewById(R.id.detail_weapon_blade_special_value)
        mWeaponNoteText = view.findViewById(R.id.detail_weapon_blade_note_text)
        NoteContainer = view.findViewById(R.id.detail_weapon_note_container)
        mWeaponNote1ImageView = view
                .findViewById(R.id.detail_weapon_blade_note1)
        mWeaponNote2ImageView = view
                .findViewById(R.id.detail_weapon_blade_note2)
        mWeaponNote3ImageView = view
                .findViewById(R.id.detail_weapon_blade_note3)

        return view
    }

    override fun populateWeapon(mWeapon: Weapon?) {
        super.populateWeapon(mWeapon)

        /* Sharpness */
        mWeaponSharpnessDrawnView!!.init(mWeapon!!.sharpness1, mWeapon.sharpness2, mWeapon.sharpness3)
        // Redraw sharpness after data is loaded
        mWeaponSharpnessDrawnView!!.invalidate()

        /* Hunting Horn notes */
        if (mWeapon.wtype == "Hunting Horn") {
            val notes = mWeapon.hornNotes

            mWeaponNote1ImageView!!.setImageResource(R.drawable.icon_music_note)
            mWeaponNote1ImageView!!.setColorFilter(ContextCompat.getColor(context!!, MHUtils.getNoteColor(notes!![0])), PorterDuff.Mode.MULTIPLY)

            mWeaponNote2ImageView!!.setImageResource(R.drawable.icon_music_note)
            mWeaponNote2ImageView!!.setColorFilter(ContextCompat.getColor(context!!, MHUtils.getNoteColor(notes[1])), PorterDuff.Mode.MULTIPLY)

            mWeaponNote3ImageView!!.setImageResource(R.drawable.icon_music_note)
            mWeaponNote3ImageView!!.setColorFilter(ContextCompat.getColor(context!!, MHUtils.getNoteColor(notes[2])), PorterDuff.Mode.MULTIPLY)
        } else {
            mWeaponNoteText!!.visibility = View.GONE
            NoteContainer.visibility = View.GONE
        }

        /* Gunlance */
        if (mWeapon.wtype == "Gunlance") {
            mWeaponSpecialTypeTextView!!.text = "Shelling"
            mWeaponSpecialTextView!!.text = mWeapon.shellingType
        } else if (mWeapon.wtype == "Switch Axe") {
            mWeaponSpecialTypeTextView!!.text = "Phial"
            mWeaponSpecialTextView!!.text = mWeapon.phial
        } else if (mWeapon.wtype == "Charge Blade") {
            mWeaponSpecialTypeTextView!!.text = "Phial"
            mWeaponSpecialTextView!!.text = mWeapon.phial
        } else {
            mWeaponSpecialTextView!!.visibility = View.GONE
            mWeaponSpecialTypeTextView!!.visibility = View.GONE
        }/* Switch Axe */
    }
}
