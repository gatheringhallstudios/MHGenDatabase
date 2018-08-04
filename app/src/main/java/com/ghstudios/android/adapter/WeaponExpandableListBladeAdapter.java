package com.ghstudios.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.MHUtils;
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

    private static class WeaponBladeViewHolder extends WeaponElementViewHolder {
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
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        WeaponBladeViewHolder holder = (WeaponBladeViewHolder) viewHolder;
        Weapon weapon = ((WeaponListEntry) getItemAt(position)).weapon;

        //
        // Set special text fields
        //
        String type = weapon.getWtype();
        if (type.equals("Hunting Horn")) {
            String special = weapon.getHornNotes();

            holder.note1v.setTag(weapon.getId());
            holder.note2v.setTag(weapon.getId());
            holder.note3v.setTag(weapon.getId());

            holder.note1v.setVisibility(View.VISIBLE);
            holder.note2v.setVisibility(View.VISIBLE);
            holder.note3v.setVisibility(View.VISIBLE);
            holder.specialView.setVisibility(View.VISIBLE);
            holder.specialView.setText("NOTES: ");

            holder.note1v.setImageResource(R.drawable.icon_music_note);
            holder.note1v.setColorFilter(ContextCompat.getColor(mContext, MHUtils.getNoteColor(special.charAt(0))), PorterDuff.Mode.MULTIPLY);

            holder.note2v.setImageResource(R.drawable.icon_music_note);
            holder.note2v.setColorFilter(ContextCompat.getColor(mContext, MHUtils.getNoteColor(special.charAt(1))), PorterDuff.Mode.MULTIPLY);

            holder.note3v.setImageResource(R.drawable.icon_music_note);
            holder.note3v.setColorFilter(ContextCompat.getColor(mContext, MHUtils.getNoteColor(special.charAt(2))), PorterDuff.Mode.MULTIPLY);
        }
        else if (type.equals("Gunlance")) {
            holder.specialView.setVisibility(View.VISIBLE);
            String special = weapon.getShellingType();
            holder.specialView.setText(special);
        }
        else if (type.equals("Switch Axe") || type.equals("Charge Blade")) {
            holder.specialView.setVisibility(View.VISIBLE);
            String special = weapon.getPhial();
            holder.specialView.setText(special);
        }

        // Set sharpness
        holder.sharpnessDrawable.init(weapon.getSharpness1(), weapon.getSharpness2(),weapon.getSharpness3());
        holder.sharpnessDrawable.invalidate();
    }

}
