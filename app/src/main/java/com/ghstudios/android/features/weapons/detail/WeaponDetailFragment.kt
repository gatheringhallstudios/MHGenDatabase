package com.ghstudios.android.features.weapons.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.TextView

import com.ghstudios.android.AppSettings
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.components.ColumnLabelTextCell
import com.ghstudios.android.components.ItemRecipeCell
import com.ghstudios.android.components.TitleBarCell
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.applyArguments

/**
 * The superclass of all weapon detail fragments.
 * TODO: Fragments do not lend themselves well to being inherited for unique views.
 * Find an alternative way to handle it like custom view groups, or move more functionality
 * to the superclass
 */
open class WeaponDetailFragment : Fragment() {
    companion object {
        val ARG_WEAPON_ID = "WEAPON_ID"

        @JvmStatic open fun newInstance(weaponId: Long): WeaponDetailFragment {
            return WeaponDetailFragment().applyArguments {
                putLong(ARG_WEAPON_ID, weaponId)
            }
        }
    }

//    private val viewModel by lazy {
//        ViewModelProviders.of(this).get(WeaponDetailViewModel::class.java)
//    }

    // note: we can't use KTX or ButterKnife because of the awkward fragment inheritance strategy

    private var titleBar: TitleBarCell? = null
    private var rarityCell: ColumnLabelTextCell? = null
    private var attackCell: ColumnLabelTextCell? = null
    private var element1Cell: ColumnLabelTextCell? = null
    private var element2Cell: ColumnLabelTextCell? = null
    private var affinityCell: ColumnLabelTextCell? = null
    private var slotsCell: ColumnLabelTextCell? = null

    protected var mWeaponDescription: TextView? = null
    protected var mWeaponDefenseTextView: TextView? = null
    protected var mWeaponDefenseTextTextView: TextView? = null
    protected var mWeaponCreationTextView: TextView? = null
    protected var mWeaponUpgradeTextView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind general view elements
        // subclasses implement onCreateView, so we have to do it here instead
        titleBar = view.findViewById(R.id.titlebar)
        rarityCell = view.findViewById(R.id.rare)
        attackCell = view.findViewById(R.id.attack)
        element1Cell = view.findViewById(R.id.element1)
        element2Cell = view.findViewById(R.id.element2)
        affinityCell = view.findViewById(R.id.affinity)
        slotsCell = view.findViewById(R.id.slots)

        val viewModel = ViewModelProviders.of(activity!!).get(WeaponDetailViewModel::class.java)

        viewModel.weaponData.observe(this, Observer(::populateWeapon))
        viewModel.weaponElementData.observe(this, Observer(::populateElementData))

        viewModel.createComponentData.observe(this, Observer(::populateCreateComponents))
        viewModel.improveComponentData.observe(this, Observer(::populateUpgradeComponents))
    }

    protected open fun populateWeapon(weapon: Weapon?) {
        if (weapon == null) return

        titleBar!!.setIcon(weapon)
        titleBar!!.setTitleText(weapon.name)
        titleBar?.setAltTitleText(getString(R.string.value_rare, weapon.rarityString))
        
        attackCell!!.setValueText("" + weapon.attack)
        affinityCell!!.setValueText(weapon.affinity!! + "%")
        slotsCell!!.setValueText("" + weapon.slotString)

        /*
         * Items below are from old code
         */

        mWeaponDescription!!.text = weapon.description

        if (weapon.defense == 0) {
            mWeaponDefenseTextTextView!!.visibility = View.GONE
            mWeaponDefenseTextView!!.visibility = View.GONE
        } else
            mWeaponDefenseTextView!!.text = "" + weapon.defense


        val createCost = "" + weapon.creationCost + "z"
        val upgradeCost = "" + weapon.upgradeCost + "z"
        mWeaponCreationTextView!!.text = createCost
        mWeaponUpgradeTextView!!.text = upgradeCost
    }

    private fun populateElementData(items: List<WeaponElementData>?) {
        element1Cell!!.visibility = View.GONE
        element2Cell!!.visibility = View.GONE

        if (items == null) {
            return
        }

        if (items.isNotEmpty()) {
            val (element, value) = items[0]
            element1Cell!!.setLabelText(element)
            element1Cell!!.setValueText(value.toString())
            element1Cell!!.visibility = View.VISIBLE
        }

        if (items.size >= 2) {
            val (element, value) = items[1]
            element2Cell!!.setLabelText(element)
            element2Cell!!.setValueText(value.toString())
            element2Cell!!.visibility = View.VISIBLE
        }
    }

    private fun populateCreateComponents(components: List<Component>?) {
        val section = view!!.findViewById<View>(R.id.create_section)

        if (components == null || components.isEmpty()) {
            section.visibility = View.GONE
            return
        }

        section.visibility = View.VISIBLE

        val recipeView = view!!.findViewById<ItemRecipeCell>(R.id.create_recipe)
        for (component in components) {
            val item = component.component
            val itemCell = recipeView.addItem(item, item?.name, component.quantity)
            itemCell.setOnClickListener(ItemClickListener(context!!, item!!))
        }
    }

    private fun populateUpgradeComponents(components: List<Component>?) {
        val section = view!!.findViewById<View>(R.id.upgrade_section)

        if (components == null || components.isEmpty()) {
            section.visibility = View.GONE
            return
        }

        section.visibility = View.VISIBLE

        val recipeView = view!!.findViewById<ItemRecipeCell>(R.id.upgrade_recipe)
        for (component in components) {
            val item = component.component
            val itemCell = recipeView.addItem(item, item?.name, component.quantity)
            itemCell.setOnClickListener(ItemClickListener(context!!, item!!))
        }
    }
}
