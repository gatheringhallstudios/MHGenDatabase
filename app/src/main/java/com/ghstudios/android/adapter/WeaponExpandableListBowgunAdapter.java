package com.ghstudios.android.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.components.WeaponListEntry;

/**
 * Created by Mark on 3/3/2015.
 */
public class WeaponExpandableListBowgunAdapter extends WeaponExpandableListGeneralAdapter {

    public WeaponExpandableListBowgunAdapter(Context context, View.OnLongClickListener listener) {
        super(context, listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder viewHolder;

        int resource = R.layout.fragment_weapon_tree_item_bowgun;
        v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);

        viewHolder = new WeaponBowgunViewHolder(v);

        v.setOnLongClickListener(mListener);

        return viewHolder;
    }

    public static class WeaponBowgunViewHolder extends WeaponViewHolder {
        // Gunner
        TextView recoiltv;
        TextView steadytv;
        TextView reloadtv;

        public WeaponBowgunViewHolder(View weaponView) {
            super(weaponView);

            //
            // Bowgun views
            //
            reloadtv = (TextView) weaponView.findViewById(R.id.reload_text);
            recoiltv = (TextView) weaponView.findViewById(R.id.recoil_text);
            steadytv = (TextView) weaponView.findViewById(R.id.deviation_text);
        }

        @Override
        public void bindView(Context context, WeaponListEntry entry) {
            super.bindView(context, entry);

            Weapon weapon = entry.weapon;

            String reload = weapon.getReloadSpeed();
            String recoil = weapon.getRecoil();
            String steady = weapon.getDeviation();


            if (steady.startsWith("Left/Right")) {
                String[] tempSteady = steady.split(":");
                steady = "L/R:" + tempSteady[1];
            }

            reloadtv.setText("REL: " + reload);
            recoiltv.setText("REC: " + recoil);
            steadytv.setText("DEV: " + steady);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);

        WeaponBowgunViewHolder holder = (WeaponBowgunViewHolder) viewHolder;
        holder.bindView(mContext,(WeaponListEntry)getItemAt(position));
    }
}
