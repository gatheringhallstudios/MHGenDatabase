package com.ghstudios.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghstudios.android.AssetRegistry;
import com.ghstudios.android.data.classes.ElementStatus;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.components.WeaponListEntry;

/**
 * Created by Mark on 3/3/2015.
 */
public abstract class WeaponExpandableListElementAdapter extends WeaponExpandableListGeneralAdapter {

    public WeaponExpandableListElementAdapter(Context context, View.OnLongClickListener listener) {
        super(context, listener);
    }

    protected static class WeaponElementViewHolder extends WeaponViewHolder {

        // Element
        public TextView elementView;
        public TextView elementView2;
        public TextView awakenView;
        
        public ImageView elementIconView;
        public ImageView elementIconView2;

        public WeaponElementViewHolder(View weaponView) {
            super(weaponView);

            //
            // ELEMENT VIEWS
            //

            elementView = (TextView) weaponView.findViewById(R.id.element_text);
            elementView2 = (TextView) weaponView.findViewById(R.id.element_text2);
            awakenView = (TextView) weaponView.findViewById(R.id.awaken_text);
            elementIconView = (ImageView) weaponView.findViewById(R.id.element_image);
            elementIconView2 = (ImageView) weaponView.findViewById(R.id.element_image2);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        
        WeaponElementViewHolder holder = (WeaponElementViewHolder) viewHolder;
        Weapon weapon = ((WeaponListEntry) getItemAt(position)).weapon;

        // Set the element to view
        ElementStatus element = weapon.getElementEnum();
        ElementStatus awakenedElement = weapon.getAwakenElementEnum();
        ElementStatus element2 = weapon.getElement2Enum();

        long element_attack = weapon.getElementAttack();
        long element_2_attack = weapon.getElement2Attack();
        long awaken_attack = weapon.getAwakenAttack();

        String awakenText = "";


        holder.elementView.setText("");
        holder.elementView2.setText("");
        holder.elementIconView.setVisibility(View.INVISIBLE);
        holder.elementIconView2.setVisibility(View.INVISIBLE);

        if (element != ElementStatus.NONE || awakenedElement != ElementStatus.NONE) {
            if (awakenedElement != ElementStatus.NONE) {
                element = awakenedElement;
                element_attack = awaken_attack;
                awakenText = "(";
                holder.elementView.setText(Long.toString(element_attack) + ")");
            } else {
                holder.elementView.setText(Long.toString(element_attack));
            }

            holder.elementIconView.setTag(weapon.getId());
            holder.elementIconView.setVisibility(View.VISIBLE);

            int elementIconId = AssetRegistry.getElementRegistry().get(element, R.color.transparent);
            Drawable icon = ContextCompat.getDrawable(mContext, elementIconId);
            holder.elementIconView.setImageDrawable(icon);
        }

        if (element2 != ElementStatus.NONE) {
            holder.elementIconView2.setTag(weapon.getId());

            int elementIconId = AssetRegistry.getElementRegistry().get(element, R.color.transparent);
            Drawable icon = ContextCompat.getDrawable(mContext, elementIconId);
            holder.elementIconView2.setImageDrawable(icon);

            holder.elementIconView2.setVisibility(View.VISIBLE);

            holder.elementView2.setText("" + element_2_attack);
        }

        holder.awakenView.setText(awakenText);
    }
}
