package com.ghstudios.android.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.util.MHUtils;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.components.DrawSharpness;
import com.ghstudios.android.components.WeaponListEntry;

/**
 * Created by Mark on 3/3/2015.
 */
public class WeaponExpandableListBladeAdapter extends WeaponExpandableListElementAdapter {

    public WeaponExpandableListBladeAdapter(Context context, View.OnLongClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder viewHolder;

        int resource = R.layout.fragment_weapon_tree_item_blademaster;
        v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        viewHolder = new WeaponBladeViewHolder(v);

        v.setOnLongClickListener(mListener);

        return viewHolder;
    }

    public static class WeaponBladeViewHolder extends WeaponElementViewHolder {
        // Blade

        TextView specialView;
        DrawSharpness sharpnessDrawable;
        ImageView note1v;
        ImageView note2v;
        ImageView note3v;

        public WeaponBladeViewHolder(View weaponView) {
            super(weaponView);

            //
            // BLADE VIEWS
            specialView = weaponView.findViewById(R.id.special_text);
            sharpnessDrawable = weaponView.findViewById(R.id.sharpness);

            note1v = weaponView.findViewById(R.id.note_image_1);
            note2v = weaponView.findViewById(R.id.note_image_2);
            note3v = weaponView.findViewById(R.id.note_image_3);
        }

        @Override
        public void bindView(Context context, WeaponListEntry entry){
            super.bindView(context,entry);

            Weapon weapon = entry.weapon;

            String type = weapon.getWtype();
            if (type.equals(Weapon.HUNTING_HORN)) {
                String special = weapon.getHornNotes();

                note1v.setTag(weapon.getId());
                note2v.setTag(weapon.getId());
                note3v.setTag(weapon.getId());

                note1v.setVisibility(View.VISIBLE);
                note2v.setVisibility(View.VISIBLE);
                note3v.setVisibility(View.VISIBLE);

                note1v.setImageResource(R.drawable.icon_music_note);
                note1v.setColorFilter(ContextCompat.getColor(context, MHUtils.getNoteColor(special.charAt(0))), PorterDuff.Mode.MULTIPLY);

                note2v.setImageResource(R.drawable.icon_music_note);
                note2v.setColorFilter(ContextCompat.getColor(context, MHUtils.getNoteColor(special.charAt(1))), PorterDuff.Mode.MULTIPLY);

                note3v.setImageResource(R.drawable.icon_music_note);
                note3v.setColorFilter(ContextCompat.getColor(context, MHUtils.getNoteColor(special.charAt(2))), PorterDuff.Mode.MULTIPLY);
            }
            else if (type.equals(Weapon.GUNLANCE)) {
                specialView.setVisibility(View.VISIBLE);
                String special = AssetLoader.localizeWeaponShelling(weapon.getShellingType());
                specialView.setText(special);
            }
            else if (type.equals(Weapon.SWITCH_AXE) || type.equals(Weapon.CHARGE_BLADE)) {
                specialView.setVisibility(View.VISIBLE);
                String special = AssetLoader.localizeWeaponPhialType(weapon.getPhial());
                specialView.setText(special);
            }

            // Set sharpness
            sharpnessDrawable.init(weapon.getSharpness1(), weapon.getSharpness2(),weapon.getSharpness3());
            sharpnessDrawable.invalidate();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        WeaponBladeViewHolder holder = (WeaponBladeViewHolder) viewHolder;
        holder.bindView(mContext,(WeaponListEntry) getItemAt(position));
    }

}
