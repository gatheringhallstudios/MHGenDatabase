package com.ghstudios.android.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.WeaponClickListener;
import com.ghstudios.android.components.WeaponListEntry;
import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;
import com.oissela.software.multilevelexpindlistview.Utils;

/**
 * Created by Mark on 3/3/2015.
 */
public abstract class WeaponExpandableListGeneralAdapter extends MultiLevelExpIndListAdapter {

    /**
     * This is called when the user click on an item or group.
     */
    protected final View.OnLongClickListener mListener;

    protected final Context mContext;

    public WeaponExpandableListGeneralAdapter(Context context, View.OnLongClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        WeaponViewHolder holder = (WeaponViewHolder) viewHolder;
        WeaponListEntry weaponEntry = (WeaponListEntry) getItemAt(position);
        holder.bindView(mContext, weaponEntry);
    }

    public static class WeaponViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public LinearLayout weaponLayout;
        public RelativeLayout clickableLayout;

        public TextView nameView;
        public TextView attackView;
        public TextView slotView;
        public TextView affinityView;
        public TextView defenseView;

        public ImageView iconView;

        public View colorBand;
        public View indentView;
        public View arrow;

        private static final int[] indColors = {R.color.rare_1, R.color.rare_2,
                R.color.rare_3, R.color.rare_4, R.color.rare_5,
                R.color.rare_6, R.color.rare_7, R.color.rare_8, R.color.rare_9,
                R.color.rare_10, R.color.rare_11};

        /**
         * Unit of indentation.
         */
        private final int mPaddingDP = 4;

        public WeaponViewHolder(View weaponView) {
            super(weaponView);
            view = weaponView;

            //
            // GENERAL VIEWS
            //

            // Set the layout id
            weaponLayout = weaponView.findViewById(R.id.main_layout);
            clickableLayout = weaponView.findViewById(R.id.clickable_layout);

            // Find all views
            nameView = weaponView.findViewById(R.id.name_text);
            attackView = weaponView.findViewById(R.id.attack_text);
            slotView = weaponView.findViewById(R.id.slots_text);
            affinityView = weaponView.findViewById(R.id.affinity_text);
            defenseView = weaponView.findViewById(R.id.defense_text);
            
            colorBand = weaponView.findViewById(R.id.color_band);
            indentView = weaponView.findViewById(R.id.indent_view);
            arrow = weaponView.findViewById(R.id.arrow);
        }

        public void bindView(Context context, WeaponListEntry entry){
            Weapon weapon = entry.weapon;

            String name = weapon.getName();
            // Add ? to indicate that a weapon is create-able
            if(weapon.getCreationCost() > 0)
                name = name+"\u2605";

            // Get the weapons attack
            String attack = "DMG: " + weapon.getAttackString();

            //
            // Get affinity and defense
            //
            String affinity = "";
            if (weapon.getAffinity().length() > 0) {
                affinity = weapon.getAffinity() + "%";
            }


            String defense = "";
            if (weapon.getDefense() != 0) {
                defense = "DEF: " + weapon.getDefense();
            }

            //
            // Set remaining items
            //
            nameView.setText(name);
            attackView.setText(attack);
            slotView.setText(weapon.getSlotString());
            affinityView.setText(affinity);
            defenseView.setText(defense);

            view.setOnClickListener(new WeaponClickListener(context, weapon.getId()));

            //
            // Handle indentation
            //

            //colorBand.setVisibility(View.VISIBLE);
            setColorBandColor(weapon.getRarity()-1);

            int leftPadding = Utils.getPaddingPixels(context, mPaddingDP)
                    * (entry.getIndentation());
            setPaddingLeft(leftPadding);

            //
            // Handle groups
            //
            arrow.setVisibility(View.INVISIBLE);
            if (entry.isGroup() && entry.getGroupSize() > 0) {
                arrow.setVisibility(View.VISIBLE);
            }
        }

        public void setPaddingLeft(int paddingLeft) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indentView.getLayoutParams();
            params.width = paddingLeft;
        }

        public void setColorBandColor(int indentation) {
            int color = view.getContext().getResources().getColor(indColors[indentation]);
            colorBand.setBackgroundColor(color);
        }
    }
}
