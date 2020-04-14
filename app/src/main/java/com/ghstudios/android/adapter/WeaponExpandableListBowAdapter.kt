package com.ghstudios.android.adapter

import android.content.Context
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.AssetLoader

import com.ghstudios.android.components.FixedImageView
import com.ghstudios.android.components.WeaponListEntry
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R

/**
 * Created by Mark on 3/5/2015.
 * Converted to Kotlin on 10/4, but hasn't been refactored yet.
 */
class WeaponExpandableListBowAdapter(context: Context, listener: View.OnLongClickListener) : WeaponExpandableListElementAdapter(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val v: View
        val viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder

        val resource = R.layout.fragment_weapon_tree_item_bow
        v = LayoutInflater.from(parent.context)
                .inflate(resource, parent, false)

        viewHolder = WeaponBowViewHolder(v)

        v.setOnLongClickListener(mListener)

        return viewHolder
    }

    class WeaponBowViewHolder(weaponView: View) : WeaponExpandableListElementAdapter.WeaponElementViewHolder(weaponView) {
        // Bow
        internal var powerv: FixedImageView
        internal var crangev: FixedImageView
        internal var poisonv: FixedImageView
        internal var parav: FixedImageView
        internal var sleepv: FixedImageView
        internal var exhaustv: FixedImageView
        internal var slimev: FixedImageView
        internal var paintv: FixedImageView

        internal var arctv: TextView
        internal var chargetv: TextView


        init {
            //
            // BOW VIEWS
            arctv = weaponView.findViewById(R.id.arc_shot_text)
            chargetv = weaponView.findViewById(R.id.charge_text)

            // Coatings
            powerv = weaponView.findViewById(R.id.power)
            crangev = weaponView.findViewById(R.id.crange)
            poisonv = weaponView.findViewById(R.id.poison)
            parav = weaponView.findViewById(R.id.para)
            sleepv = weaponView.findViewById(R.id.sleep)
            exhaustv = weaponView.findViewById(R.id.exhaust)
            slimev = weaponView.findViewById(R.id.blast)
            paintv = weaponView.findViewById(R.id.paint)
        }

        override fun bindView(context: Context, entry: WeaponListEntry) {
            super.bindView(context, entry)

            val weapon = entry.weapon

            val arc = weapon.recoil

            arctv.text = arc
            chargetv.text = weapon.charges.joinToString(" / ") {
                AssetLoader.localizeChargeLevel(it).replace(" ", "")
            }

            // Clear images
            powerv.setImageDrawable(null)
            crangev.setImageDrawable(null)
            poisonv.setImageDrawable(null)
            parav.setImageDrawable(null)
            sleepv.setImageDrawable(null)
            exhaustv.setImageDrawable(null)
            slimev.setImageDrawable(null)
            paintv.setImageDrawable(null)

            powerv.visibility = View.GONE
            crangev.visibility = View.GONE
            poisonv.visibility = View.GONE
            parav.visibility = View.GONE
            sleepv.visibility = View.GONE
            exhaustv.visibility = View.GONE
            slimev.visibility = View.GONE
            paintv.visibility = View.GONE

            //TODO:make the actual field in the db an int.
            val coatings = weapon.coatings!!

            if (coatings.hasPower) {
                powerv.setImageResource(R.drawable.icon_bottle)
                powerv.setColorFilter(ContextCompat.getColor(context, R.color.item_red), PorterDuff.Mode.MULTIPLY)
                powerv.visibility = View.VISIBLE
            }
            if (coatings.poison) {
                poisonv.setImageResource(R.drawable.icon_bottle)
                poisonv.setColorFilter(ContextCompat.getColor(context, R.color.item_purple), PorterDuff.Mode.MULTIPLY)
                poisonv.visibility = View.VISIBLE
            }
            if (coatings.para) {
                parav.setImageResource(R.drawable.icon_bottle)
                parav.setColorFilter(ContextCompat.getColor(context, R.color.item_yellow), PorterDuff.Mode.MULTIPLY)
                parav.visibility = View.VISIBLE
            }
            if (coatings.sleep) {
                sleepv.setImageResource(R.drawable.icon_bottle)
                sleepv.setColorFilter(ContextCompat.getColor(context, R.color.item_cyan), PorterDuff.Mode.MULTIPLY)
                sleepv.visibility = View.VISIBLE
            }
            if (coatings.crange) {
                crangev.setImageResource(R.drawable.icon_bottle)
                crangev.setColorFilter(ContextCompat.getColor(context, R.color.item_white), PorterDuff.Mode.MULTIPLY)
                crangev.visibility = View.VISIBLE
            }
            if (coatings.exhaust) {
                exhaustv.setImageResource(R.drawable.icon_bottle)
                exhaustv.setColorFilter(ContextCompat.getColor(context, R.color.item_blue), PorterDuff.Mode.MULTIPLY)
                exhaustv.visibility = View.VISIBLE
            }
            if (coatings.blast) {
                slimev.setImageResource(R.drawable.icon_bottle)
                slimev.setColorFilter(ContextCompat.getColor(context, R.color.item_orange), PorterDuff.Mode.MULTIPLY)
                slimev.visibility = View.VISIBLE
            }
        }
    }

    override fun onBindViewHolder(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        val holder = viewHolder as WeaponBowViewHolder
        holder.bindView(mContext, getItemAt(position) as WeaponListEntry)
    }
}
