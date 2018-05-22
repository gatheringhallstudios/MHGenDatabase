package com.ghstudios.android.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.general.FixedImageView;
import com.ghstudios.android.ui.general.WeaponListEntry;

/**
 * Created by Mark on 3/5/2015.
 */
public class WeaponExpandableListBowAdapter extends WeaponExpandableListElementAdapter {

    public WeaponExpandableListBowAdapter(Context context, View.OnLongClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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


        public WeaponBowViewHolder(View weaponView) {
            super(weaponView);

            //
            // BOW VIEWS
            arctv = (TextView) weaponView.findViewById(R.id.arc_shot_text);
            chargetv = (TextView) weaponView.findViewById(R.id.charge_text);

            // Coatings
            powerv = (FixedImageView) weaponView.findViewById(R.id.power);
            crangev = (FixedImageView) weaponView.findViewById(R.id.crange);
            poisonv = (FixedImageView) weaponView.findViewById(R.id.poison);
            parav = (FixedImageView) weaponView.findViewById(R.id.para);
            sleepv = (FixedImageView) weaponView.findViewById(R.id.sleep);
            exhaustv = (FixedImageView) weaponView.findViewById(R.id.exhaust);
            slimev = (FixedImageView) weaponView.findViewById(R.id.blast);
            paintv = (FixedImageView) weaponView.findViewById(R.id.paint);

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

        Bitmap bitmap = null;

        //Power 1 and 2 are bits 11,10
        boolean power = ((coatings & 0x0400)>0) || ((coatings & 0x0200)>0);
        //Elemental 1 and 2 are bits 9,8
        boolean elemental = ((coatings & 0x0100)>0) || ((coatings & 0x0080)>0);

        try {
            if (power) {
                holder.powerv.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-Red.png");
                if (bitmap != null) {
                    holder.powerv.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.powerv, "icons_items/Bottle-Red.png").execute();
                }
                holder.powerv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x20)>0) {
                holder.poisonv.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-Purple.png");
                if (bitmap != null) {
                    holder.poisonv.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.poisonv, "icons_items/Bottle-Purple.png").execute();
                }
                holder.poisonv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x10)>0) {
                holder.parav.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-Yellow.png");
                if (bitmap != null) {
                    holder.parav.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.parav, "icons_items/Bottle-Yellow.png").execute();
                }
                holder.parav.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x08)>0) {
                holder.sleepv.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-Cyan.png");
                if (bitmap != null) {
                    holder.sleepv.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.sleepv, "icons_items/Bottle-Cyan.png").execute();
                }
                holder.sleepv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x40)>0) {
                holder.crangev.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-White.png");
                if (bitmap != null) {
                    holder.crangev.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.crangev, "icons_items/Bottle-White.png").execute();
                }
                holder.crangev.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x04)>0) {
                holder.exhaustv.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-Blue.png");
                if (bitmap != null) {
                    holder.exhaustv.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.exhaustv, "icons_items/Bottle-Blue.png").execute();
                }
                holder.exhaustv.setVisibility(View.VISIBLE);
            }
            if ((coatings & 0x02)>0) {
                holder.slimev.setTag(weapon.getId());
                bitmap = getBitmapFromMemCache("icons_items/Bottle-Orange.png");
                if (bitmap != null) {
                    holder.slimev.setImageBitmap(bitmap);
                } else {
                    new LoadImage(holder.slimev, "icons_items/Bottle-Orange.png").execute();
                }
                holder.slimev.setVisibility(View.VISIBLE);
            }
        }
        catch(Exception e){

        }

    }
}
