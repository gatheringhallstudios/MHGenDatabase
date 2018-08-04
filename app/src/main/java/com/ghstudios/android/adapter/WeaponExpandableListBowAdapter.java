package com.ghstudios.android.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.components.FixedImageView;
import com.ghstudios.android.components.WeaponListEntry;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;

/**
 * Created by Mark on 3/5/2015.
 */
public class WeaponExpandableListBowAdapter extends WeaponExpandableListElementAdapter {

    public WeaponExpandableListBowAdapter(Context context, View.OnLongClickListener listener) {
        super(context, listener);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder viewHolder;

        int resource = R.layout.fragment_weapon_tree_item_bow;
        v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        viewHolder = new WeaponBowViewHolder(v);

        v.setOnLongClickListener(mListener);

        return viewHolder;
    }

    private static class WeaponBowViewHolder extends WeaponElementViewHolder {
        // Bow
        FixedImageView powerv;
        FixedImageView crangev;
        FixedImageView poisonv;
        FixedImageView parav;
        FixedImageView sleepv;
        FixedImageView exhaustv;
        FixedImageView slimev;
        FixedImageView paintv;

        TextView arctv;
        TextView chargetv;


        WeaponBowViewHolder(View weaponView) {
            super(weaponView);

            //
            // BOW VIEWS
            arctv = weaponView.findViewById(R.id.arc_shot_text);
            chargetv = weaponView.findViewById(R.id.charge_text);

            // Coatings
            powerv = weaponView.findViewById(R.id.power);
            crangev = weaponView.findViewById(R.id.crange);
            poisonv = weaponView.findViewById(R.id.poison);
            parav = weaponView.findViewById(R.id.para);
            sleepv = weaponView.findViewById(R.id.sleep);
            exhaustv = weaponView.findViewById(R.id.exhaust);
            slimev = weaponView.findViewById(R.id.blast);
            paintv = weaponView.findViewById(R.id.paint);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        WeaponBowViewHolder holder = (WeaponBowViewHolder) viewHolder;
        Weapon weapon = ((WeaponListEntry) getItemAt(position)).weapon;

        String arc = weapon.getRecoil();

        holder.arctv.setText(arc);
        holder.chargetv.setText(weapon.getChargeString());

        // Clear images
        holder.powerv.setImageDrawable(null);
        holder.crangev.setImageDrawable(null);
        holder.poisonv.setImageDrawable(null);
        holder.parav.setImageDrawable(null);
        holder.sleepv.setImageDrawable(null);
        holder.exhaustv.setImageDrawable(null);
        holder.slimev.setImageDrawable(null);
        holder.paintv.setImageDrawable(null);

        holder.powerv.setVisibility(View.GONE);
        holder.crangev.setVisibility(View.GONE);
        holder.poisonv.setVisibility(View.GONE);
        holder.parav.setVisibility(View.GONE);
        holder.sleepv.setVisibility(View.GONE);
        holder.exhaustv.setVisibility(View.GONE);
        holder.slimev.setVisibility(View.GONE);
        holder.paintv.setVisibility(View.GONE);

        //TODO:make the actual field in the db an int.
        int coatings = Integer.parseInt(weapon.getCoatings());

        //Power 1 and 2 are bits 11,10
        boolean power = ((coatings & 0x0400)>0) || ((coatings & 0x0200)>0);

        if (power) {
            holder.powerv.setImageResource(R.drawable.icon_bottle);
            holder.powerv.setColorFilter(ContextCompat.getColor(mContext,R.color.item_red), PorterDuff.Mode.MULTIPLY);
            holder.powerv.setVisibility(View.VISIBLE);
        }
        if ((coatings & 0x20)>0) {
            holder.poisonv.setImageResource(R.drawable.icon_bottle);
            holder.poisonv.setColorFilter(ContextCompat.getColor(mContext,R.color.item_purple), PorterDuff.Mode.MULTIPLY);
            holder.poisonv.setVisibility(View.VISIBLE);
        }
        if ((coatings & 0x10)>0) {
            holder.parav.setImageResource(R.drawable.icon_bottle);
            holder.parav.setColorFilter(ContextCompat.getColor(mContext,R.color.item_yellow), PorterDuff.Mode.MULTIPLY);
            holder.parav.setVisibility(View.VISIBLE);
        }
        if ((coatings & 0x08)>0) {
            holder.sleepv.setImageResource(R.drawable.icon_bottle);
            holder.sleepv.setColorFilter(ContextCompat.getColor(mContext,R.color.item_cyan), PorterDuff.Mode.MULTIPLY);
            holder.sleepv.setVisibility(View.VISIBLE);
        }
        if ((coatings & 0x40)>0) {
            holder.crangev.setImageResource(R.drawable.icon_bottle);
            holder.crangev.setColorFilter(ContextCompat.getColor(mContext,R.color.item_white), PorterDuff.Mode.MULTIPLY);
            holder.crangev.setVisibility(View.VISIBLE);
        }
        if ((coatings & 0x04)>0) {
            holder.exhaustv.setImageResource(R.drawable.icon_bottle);
            holder.exhaustv.setColorFilter(ContextCompat.getColor(mContext,R.color.item_blue), PorterDuff.Mode.MULTIPLY);
            holder.exhaustv.setVisibility(View.VISIBLE);
        }
        if ((coatings & 0x02)>0) {
            holder.slimev.setImageResource(R.drawable.icon_bottle);
            holder.slimev.setColorFilter(ContextCompat.getColor(mContext,R.color.item_orange), PorterDuff.Mode.MULTIPLY);
            holder.slimev.setVisibility(View.VISIBLE);
        }

    }
}
