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

    public static class WeaponBowViewHolder extends WeaponElementViewHolder {
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


        public WeaponBowViewHolder(View weaponView) {
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

        @Override
        public void bindView(Context context, WeaponListEntry entry) {
            super.bindView(context, entry);

            Weapon weapon = entry.weapon;

            String arc = weapon.getRecoil();

            arctv.setText(arc);
            chargetv.setText(weapon.getChargeString());

            // Clear images
            powerv.setImageDrawable(null);
            crangev.setImageDrawable(null);
            poisonv.setImageDrawable(null);
            parav.setImageDrawable(null);
            sleepv.setImageDrawable(null);
            exhaustv.setImageDrawable(null);
            slimev.setImageDrawable(null);
            paintv.setImageDrawable(null);

            powerv.setVisibility(View.GONE);
            crangev.setVisibility(View.GONE);
            poisonv.setVisibility(View.GONE);
            parav.setVisibility(View.GONE);
            sleepv.setVisibility(View.GONE);
            exhaustv.setVisibility(View.GONE);
            slimev.setVisibility(View.GONE);
            paintv.setVisibility(View.GONE);

            //TODO:make the actual field in the db an int.
            int coatings = Integer.parseInt(weapon.getCoatings());

            //Power 1 and 2 are bits 11,10
            boolean power = ((coatings & 0x0400)>0) || ((coatings & 0x0200)>0);

            if (power) {
                powerv.setImageResource(R.drawable.icon_bottle);
                powerv.setColorFilter(ContextCompat.getColor(context,R.color.item_red), PorterDuff.Mode.MULTIPLY);
                powerv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x20)>0) {
                poisonv.setImageResource(R.drawable.icon_bottle);
                poisonv.setColorFilter(ContextCompat.getColor(context,R.color.item_purple), PorterDuff.Mode.MULTIPLY);
                poisonv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x10)>0) {
                parav.setImageResource(R.drawable.icon_bottle);
                parav.setColorFilter(ContextCompat.getColor(context,R.color.item_yellow), PorterDuff.Mode.MULTIPLY);
                parav.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x08)>0) {
                sleepv.setImageResource(R.drawable.icon_bottle);
                sleepv.setColorFilter(ContextCompat.getColor(context,R.color.item_cyan), PorterDuff.Mode.MULTIPLY);
                sleepv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x40)>0) {
                crangev.setImageResource(R.drawable.icon_bottle);
                crangev.setColorFilter(ContextCompat.getColor(context,R.color.item_white), PorterDuff.Mode.MULTIPLY);
                crangev.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x04)>0) {
                exhaustv.setImageResource(R.drawable.icon_bottle);
                exhaustv.setColorFilter(ContextCompat.getColor(context,R.color.item_blue), PorterDuff.Mode.MULTIPLY);
                exhaustv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x02)>0) {
                slimev.setImageResource(R.drawable.icon_bottle);
                slimev.setColorFilter(ContextCompat.getColor(context,R.color.item_orange), PorterDuff.Mode.MULTIPLY);
                slimev.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        WeaponBowViewHolder holder = (WeaponBowViewHolder) viewHolder;
        holder.bindView(mContext,(WeaponListEntry) getItemAt(position));
    }
}
