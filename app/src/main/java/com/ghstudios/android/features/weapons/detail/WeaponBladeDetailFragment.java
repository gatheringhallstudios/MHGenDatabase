package com.ghstudios.android.features.weapons.detail;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.DrawSharpness;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;

public class WeaponBladeDetailFragment extends WeaponDetailFragment {

    private TextView mWeaponSpecialTypeTextView, mWeaponSpecialTextView,
            mWeaponNoteText;
    private ImageView mWeaponNote1ImageView,
            mWeaponNote2ImageView, mWeaponNote3ImageView;
    private DrawSharpness mWeaponSharpnessDrawnView;
    View NoteContainer;

    public static WeaponBladeDetailFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(WeaponDetailFragment.ARG_WEAPON_ID, weaponId);
        WeaponBladeDetailFragment f = new WeaponBladeDetailFragment();
        f.setArguments(args);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weapon_blade_detail,
                container, false);

        mWeaponDescription = view
                .findViewById(R.id.detail_weapon_description);
        mWeaponSharpnessDrawnView = view
                .findViewById(R.id.detail_weapon_blade_sharpness);
        mWeaponDefenseTextView = view
                .findViewById(R.id.detail_weapon_defense);
        mWeaponDefenseTextTextView= view
                .findViewById(R.id.detail_weapon_defense_text);
        mWeaponCreationTextView = view
                .findViewById(R.id.detail_weapon_creation);
        mWeaponUpgradeTextView = view
                .findViewById(R.id.detail_weapon_upgrade);
        mWeaponSpecialTypeTextView = view
                .findViewById(R.id.detail_weapon_blade_special);
        mWeaponSpecialTextView = view
                .findViewById(R.id.detail_weapon_blade_special_value);
        mWeaponNoteText = view.findViewById(R.id.detail_weapon_blade_note_text);
        NoteContainer = view.findViewById(R.id.detail_weapon_note_container);
        mWeaponNote1ImageView = view
                .findViewById(R.id.detail_weapon_blade_note1);
        mWeaponNote2ImageView = view
                .findViewById(R.id.detail_weapon_blade_note2);
        mWeaponNote3ImageView = view
                .findViewById(R.id.detail_weapon_blade_note3);

        return view;
    }

    @Override
    protected void populateWeapon(Weapon mWeapon) {
        super.populateWeapon(mWeapon);

        /* Sharpness */
        mWeaponSharpnessDrawnView.init(mWeapon.getSharpness1(),mWeapon.getSharpness2(),mWeapon.getSharpness3());
        // Redraw sharpness after data is loaded
        mWeaponSharpnessDrawnView.invalidate();

        /* Hunting Horn notes */
        if (mWeapon.getWtype().equals("Hunting Horn")) {
            String notes = mWeapon.getHornNotes();

            mWeaponNote1ImageView.setImageResource(R.drawable.icon_music_note);
            mWeaponNote1ImageView.setColorFilter(ContextCompat.getColor(getContext(), MHUtils.getNoteColor(notes.charAt(0))), PorterDuff.Mode.MULTIPLY);

            mWeaponNote2ImageView.setImageResource(R.drawable.icon_music_note);
            mWeaponNote2ImageView.setColorFilter(ContextCompat.getColor(getContext(),MHUtils.getNoteColor(notes.charAt(1))), PorterDuff.Mode.MULTIPLY);

            mWeaponNote3ImageView.setImageResource(R.drawable.icon_music_note);
            mWeaponNote3ImageView.setColorFilter(ContextCompat.getColor(getContext(),MHUtils.getNoteColor(notes.charAt(2))), PorterDuff.Mode.MULTIPLY);
        }
        else
        {
            mWeaponNoteText.setVisibility(View.GONE);
            NoteContainer.setVisibility(View.GONE);
        }
        
        /* Gunlance */
        if (mWeapon.getWtype().equals("Gunlance")) {
            mWeaponSpecialTypeTextView.setText("Shelling");
            mWeaponSpecialTextView.setText(mWeapon.getShellingType());
        }

        /* Switch Axe */
        else if (mWeapon.getWtype().equals("Switch Axe")) {
            mWeaponSpecialTypeTextView.setText("Phial");
            mWeaponSpecialTextView.setText(mWeapon.getPhial());
        }
        else if(mWeapon.getWtype().equals("Charge Blade")){
            mWeaponSpecialTypeTextView.setText("Phial");
            mWeaponSpecialTextView.setText(mWeapon.getPhial());
        }
        else{
            mWeaponSpecialTextView.setVisibility(View.GONE);
            mWeaponSpecialTypeTextView.setVisibility(View.GONE);
        }
    }

}
