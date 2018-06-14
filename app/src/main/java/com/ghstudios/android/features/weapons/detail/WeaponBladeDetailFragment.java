package com.ghstudios.android.features.weapons.detail;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.MHUtils;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.components.DrawSharpness;

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

        mWeaponDescription = (TextView) view
                .findViewById(R.id.detail_weapon_description);
        mWeaponSharpnessDrawnView = (DrawSharpness) view
                .findViewById(R.id.detail_weapon_blade_sharpness);
        mWeaponRarityTextView = (TextView) view
                .findViewById(R.id.detail_weapon_rarity);
        mWeaponDefenseTextView = (TextView) view
                .findViewById(R.id.detail_weapon_defense);
        mWeaponDefenseTextTextView=(TextView) view
                .findViewById(R.id.detail_weapon_defense_text);
        mWeaponCreationTextView = (TextView) view
                .findViewById(R.id.detail_weapon_creation);
        mWeaponUpgradeTextView = (TextView) view
                .findViewById(R.id.detail_weapon_upgrade);
        mWeaponSpecialTypeTextView = (TextView) view
                .findViewById(R.id.detail_weapon_blade_special);
        mWeaponSpecialTextView = (TextView) view
                .findViewById(R.id.detail_weapon_blade_special_value);
        mWeaponNoteText = (TextView)view.findViewById(R.id.detail_weapon_blade_note_text);
        NoteContainer = view.findViewById(R.id.detail_weapon_note_container);
        mWeaponNote1ImageView = (ImageView) view
                .findViewById(R.id.detail_weapon_blade_note1);
        mWeaponNote2ImageView = (ImageView) view
                .findViewById(R.id.detail_weapon_blade_note2);
        mWeaponNote3ImageView = (ImageView) view
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

            Drawable[] images = new Drawable[3];
            for (int idx = 0; idx < 3; idx++) {
                String noteAsset = getNoteImage(notes.charAt(idx));
                images[idx] = MHUtils.loadAssetDrawable(getContext(), noteAsset);
            }

            mWeaponNote1ImageView.setImageDrawable(images[0]);
            mWeaponNote2ImageView.setImageDrawable(images[1]);
            mWeaponNote3ImageView.setImageDrawable(images[2]);
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

    public static String getNoteImage(char note) {
        String file = "icons_monster_info/";

        switch (note) {
            case 'B':
                return file + "Note.blue.png";
            case 'C':
                return file + "Note.aqua.png";
            case 'G':
                return file + "Note.green.png";
            case 'O':
                return file + "Note.orange.png";
            case 'P':
                return file + "Note.purple.png";
            case 'R':
                return file + "Note.red.png";
            case 'W':
                return file + "Note.white.png";
            case 'Y':
                return file + "Note.yellow.png";
        }
        return "";
    }

}
