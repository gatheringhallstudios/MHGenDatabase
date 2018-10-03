package com.ghstudios.android.features.weapons.detail

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ElementStatus

import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.loader.WeaponLoader
import com.ghstudios.android.mhgendatabase.R
import kotlinx.android.synthetic.main.view_weapon_detail_bow.view.*

/**
 * Created by Joseph on 7/1/2016.
 */
class WeaponBowViewHolder(parent: ViewGroup) : WeaponViewHolder {
    private val mCoatingTextViews: List<TextView>
    private val view: View

    init {
        val inflater = LayoutInflater.from(parent.context)
        view = inflater.inflate(R.layout.view_weapon_detail_bow, parent, true)

        mCoatingTextViews = listOf(
                view.findViewById(R.id.power_1_text),
                view.findViewById(R.id.power_2_text),
                view.findViewById(R.id.element_1_text),
                view.findViewById(R.id.element_2_text),
                view.findViewById(R.id.crange_text),
                view.findViewById(R.id.poison_text),
                view.findViewById(R.id.para_text),
                view.findViewById(R.id.sleep_text),
                view.findViewById(R.id.exhaust_text),
                view.findViewById(R.id.blast_text),
                view.findViewById(R.id.paint_text))
    }

    override fun bindWeapon(weapon: Weapon) {
        val context = view.context

        // Usual weapon parameters
        view.attack_value.text = weapon.attack.toString()
        view.affinity_value.text = weapon.affinity + "%"
        view.defense_value.text = weapon.defense.toString()
        view.slots.setSlots(weapon.numSlots, 0)

        // bind weapon element (todo: if awaken element ever returns...make an isAwakened flag instead)
        if (weapon.elementEnum != ElementStatus.NONE) {
            view.element1_icon.setImageDrawable(AssetLoader.loadIconFor(weapon.elementEnum))
            view.element1_value.text = weapon.elementAttack.toString()
            view.element1_group.visibility = View.VISIBLE
        }

        // todo: charge levels... bow charges will be refactored soon. After that, bind them.

        // Coatings
        val coatings = Integer.parseInt(weapon.coatings)
        for (i in 10 downTo 0) {
            val show = coatings and (1 shl i) > 0
            if (show) {
                val color = ContextCompat.getColor(context, R.color.text_color_focused)
                mCoatingTextViews[10 - i].setTextColor(color)
                mCoatingTextViews[10 - i].setTypeface(null, Typeface.BOLD)
            }
        }
    }
}
