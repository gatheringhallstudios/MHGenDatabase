package com.ghstudios.android.features.weapons

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.Weapon

import com.ghstudios.android.features.weapons.list.WeaponListActivity
import com.ghstudios.android.mhgendatabase.R

class WeaponSelectionItem(
        val type: String,
        @DrawableRes val resource: Int
)

private val weaponListItems = listOf(
        WeaponSelectionItem(Weapon.GREAT_SWORD, R.drawable.icon_great_sword),
        WeaponSelectionItem(Weapon.LONG_SWORD, R.drawable.icon_long_sword),
        WeaponSelectionItem(Weapon.SWORD_AND_SHIELD, R.drawable.icon_sword_and_shield),
        WeaponSelectionItem(Weapon.DUAL_BLADES, R.drawable.icon_dual_blades),
        WeaponSelectionItem(Weapon.HAMMER, R.drawable.icon_hammer),
        WeaponSelectionItem(Weapon.HUNTING_HORN, R.drawable.icon_hunting_horn),
        WeaponSelectionItem(Weapon.LANCE, R.drawable.icon_lance),
        WeaponSelectionItem(Weapon.GUNLANCE, R.drawable.icon_gunlance),
        WeaponSelectionItem(Weapon.SWITCH_AXE, R.drawable.icon_switch_axe),
        WeaponSelectionItem(Weapon.CHARGE_BLADE, R.drawable.icon_charge_blade),
        WeaponSelectionItem(Weapon.INSECT_GLAIVE, R.drawable.icon_insect_glaive),
        WeaponSelectionItem(Weapon.LIGHT_BOWGUN, R.drawable.icon_light_bowgun),
        WeaponSelectionItem(Weapon.HEAVY_BOWGUN, R.drawable.icon_heavy_bowgun),
        WeaponSelectionItem(Weapon.BOW, R.drawable.icon_bow)
)

class WeaponSelectionListFragment : ListFragment() {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_generic_list, parent, false)

        val mAdapter = WeaponItemAdapter(weaponListItems)
        listAdapter = mAdapter

        return v
    }

    private inner class WeaponItemAdapter(items: List<WeaponSelectionItem>) : ArrayAdapter<WeaponSelectionItem>(requireContext(), 0, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // If there's already an inflated view, reuse it
            val view = when(convertView) {
                null -> {
                    val inflater = LayoutInflater.from(context)
                    inflater.inflate(R.layout.fragment_list_item_large,  parent, false)
                }
                else -> convertView
            }

            val item = checkNotNull(getItem(position))

            val textView = view.findViewById<TextView>(R.id.item_label)
            val imageView = view.findViewById<ImageView>(R.id.item_image)

            val itemLayout = view.findViewById<RelativeLayout>(R.id.listitem)

            textView.text = AssetLoader.localizeWeaponType(item.type)
            imageView.setImageResource(item.resource)

            itemLayout.setOnClickListener(WeaponListClickListener(view.context, item.type))

            return view
        }
    }

    private inner class WeaponListClickListener(private val c: Context, private val type: String) : OnClickListener {
        override fun onClick(v: View) {
            val intent = Intent(c, WeaponListActivity::class.java)
            intent.putExtra(WeaponListActivity.EXTRA_WEAPON_TYPE, type)
            c.startActivity(intent)
        }
    }
}
