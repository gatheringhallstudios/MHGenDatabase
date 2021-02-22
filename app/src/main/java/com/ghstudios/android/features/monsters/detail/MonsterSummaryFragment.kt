package com.ghstudios.android.features.monsters.detail

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.ClickListeners.LocationClickListener
import com.ghstudios.android.components.SectionHeaderCell
import com.ghstudios.android.components.TitleBarCell
import com.ghstudios.android.mhgendatabase.R

import com.ghstudios.android.*
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.mhgendatabase.databinding.FragmentMonsterSummaryBinding

private fun imageFromWeaknessRating(weaknessRating: WeaknessRating) = when(weaknessRating) {
    WeaknessRating.WEAK -> R.drawable.effectiveness_2
    WeaknessRating.VERY_WEAK -> R.drawable.effectiveness_3
    else -> null
}

private fun localizeAilment(ctx: Context, ailmentStr: String): String {
    val resId = when (ailmentStr) {
        "Small Roar" -> R.string.ailment_roar_small
        "Large Roar" -> R.string.ailment_roar_large
        "Small Special Roar" -> R.string.ailment_roar_small_special
        "Special Roar" -> R.string.ailment_roar_special
        "Small Wind Pressure" -> R.string.ailment_wind_small
        "Large Wind Pressure" -> R.string.ailment_wind_large
        "Dragon Wind Pressure" -> R.string.ailment_wind_dragon
        "Tremor" -> R.string.ailment_tremor
        "Fireblight" -> R.string.ailment_fire
        "Waterblight" -> R.string.ailment_water
        "Thunderblight" -> R.string.ailment_thunder
        "Iceblight" -> R.string.ailment_ice
        "Dragonblight" -> R.string.ailment_dragon
        "Blastblight" -> R.string.ailment_blast
        "Bleeding" -> R.string.ailment_bleed
        "Poison" -> R.string.ailment_poison
        "Noxious Poison" -> R.string.ailment_poison_noxious
        "Deadly Poison" -> R.string.ailment_poison_deadly
        "Sleep" -> R.string.ailment_sleep
        "Paralysis" -> R.string.ailment_paralysis
        "Stun" -> R.string.ailment_stun
        "Snowman" -> R.string.ailment_snowman
        "Muddy" -> R.string.ailment_muddy
        "Bubbles" -> R.string.ailment_bubbles
        "Boned" -> R.string.ailment_boned
        "Mucus" -> R.string.ailment_mucus
        "Soiled" -> R.string.ailment_soiled
        "Environmental" -> R.string.ailment_environmental
        "Defense Down" -> R.string.ailment_defensedown
        "Frenzy Virus" -> R.string.ailment_frenzy
        "Confusion" -> R.string.ailment_confusion
        else -> 0
    }

    if (resId == 0) {
        Log.e("MonsterSummary", "Ailment localization failed for $ailmentStr")
        return ailmentStr
    }

    return ctx.getString(resId)
}

/**
 * Represents a subfragment displayed in the summary tab of the monster detail.
 */
class MonsterSummaryFragment : Fragment() {
    companion object {
        private val ARG_MONSTER_ID = "MONSTER_ID"

        @JvmStatic
        fun newInstance(monsterId: Long): MonsterSummaryFragment {
            val args = Bundle()
            args.putLong(ARG_MONSTER_ID, monsterId)
            val f = MonsterSummaryFragment()
            f.arguments = args
            return f
        }
    }

    private val TAG = this::class.java.simpleName

    private lateinit var binding: FragmentMonsterSummaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMonsterSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(activity!!).get(MonsterDetailViewModel::class.java)

        viewModel.monsterData.observe(viewLifecycleOwner, Observer { monster ->
            if (monster == null) return@Observer

            binding.monsterHeader.setIcon(monster)
            binding.monsterHeader.setTitleText(monster.name)
        })

        viewModel.weaknessData.observe(viewLifecycleOwner, Observer(::updateWeaknesses))
        viewModel.ailmentData.observe(viewLifecycleOwner, Observer(::populateAilments))
        viewModel.habitatData.observe(viewLifecycleOwner, Observer(::populateHabitats))
    }

    /**
     * Populates weakness data in the view using the provided data.
     * If null or empty, then nothing is rendered regarding weaknesses
     */
    private fun updateWeaknesses(weaknesses: List<MonsterWeaknessResult>?) {
        binding.monsterStateList.removeAllViews()
        if (weaknesses == null || weaknesses.isEmpty()) return

        for (weakness in weaknesses) {
            addWeakness(weakness)
        }
    }

    private fun addWeakness(mWeakness: MonsterWeaknessResult) {
        val inflater = LayoutInflater.from(context)
        val weaknessView = inflater.inflate(R.layout.fragment_monster_summary_state, binding.monsterStateList, false)

        // Set title
        val header = weaknessView.findViewById<SectionHeaderCell>(R.id.state_name)
        header.setLabelText(mWeakness.state)

        val weaknessListView = weaknessView.findViewById<ViewGroup>(R.id.weakness_data)
        val itemListView = weaknessView.findViewById<ViewGroup>(R.id.item_data)

        // weakness line (element part)
        for (value in mWeakness.element) {
            val imagePath = ElementRegistry[value.type]
            val imageModification = imageFromWeaknessRating(value.rating)
            addIcon(weaknessListView, imagePath, imageModification)
        }

        // weakness line (status part)
        for (value in mWeakness.status) {
            val imagePath = ElementRegistry[value.type]
            val imageModification = imageFromWeaknessRating(value.rating)
            addIcon(weaknessListView, imagePath, imageModification)
        }

        // items line
        for (trapType in mWeakness.items) {
            val imagePath = when (trapType) {
                WeaknessType.PITFALL_TRAP -> R.drawable.item_trap_pitfall
                WeaknessType.SHOCK_TRAP -> R.drawable.item_trap_shock
                WeaknessType.MEAT -> R.drawable.item_meat
                WeaknessType.FLASH_BOMB -> R.drawable.item_bomb_flash
                WeaknessType.SONIC_BOMB -> R.drawable.item_bomb_sonic
                WeaknessType.DUNG_BOMB -> R.drawable.item_bomb_dung
            }

            addIcon(itemListView, imagePath, null)
        }

        binding.monsterStateList.addView(weaknessView)
    }

    /**
     * Populates ailment data in the view using the provided data.
     * If null or empty is given, the blank slate is shown instead.
     */
    private fun populateAilments(ailments: List<MonsterAilment>?) {
        // if no ailments, show blank slate instead of the ailment list, and return
        if (ailments == null || ailments.isEmpty()) {
            binding.ailmentsEmpty.root.visibility = View.VISIBLE
            binding.ailmentsData.visibility = View.GONE
            return
        }

        // hide blank slate, and make the ailment list visible
        binding.ailmentsEmpty.root.visibility = View.GONE
        binding.ailmentsData.visibility = View.VISIBLE
        binding.ailmentsData.text = ailments.joinToString("\n") {
            localizeAilment(context!!, it.ailment)
        }
    }

    /**
     * Populates habitat data in th e view using the provided data.
     * If null or empty is given, the blank slate is shown instead.
     */
    private fun populateHabitats(habitats: List<Habitat>?) {
        if (habitats == null || habitats.isEmpty()) {
            binding.habitatsEmpty.root.visibility = View.VISIBLE
            return
        }

        binding.habitatsEmpty.root.visibility = View.GONE
        val inflater = LayoutInflater.from(context)

        binding.habitatList.removeAllViews()
        for (habitat in habitats) {
            val view = inflater.inflate(R.layout.fragment_monster_habitat_listitem, binding.habitatList, false)

            val itemLayout = view.findViewById<RelativeLayout>(R.id.listitem)
            val mapView = view.findViewById<ImageView>(R.id.mapImage)
            val mapTextView = view.findViewById<TextView>(R.id.map)
            val startTextView = view.findViewById<TextView>(R.id.start)
            val areaTextView = view.findViewById<TextView>(R.id.move)
            val restTextView = view.findViewById<TextView>(R.id.rest)

            mapTextView.text = habitat.location?.name
            startTextView.text = habitat.start.toString()
            areaTextView.text = habitat.areas?.joinToString(", ")
            restTextView.text = habitat.rest.toString()

            AssetLoader.setIcon(mapView,habitat.location!!)

            val locationId = habitat.location?.id
            if (locationId != null) {
                itemLayout.tag = locationId
                itemLayout.setOnClickListener(LocationClickListener(context, locationId))
            }

            binding.habitatList.addView(view)
        }
    }

    // Add small_icon to a particular LinearLayout
    private fun addIcon(parentview: ViewGroup, @DrawableRes image: Int?, @DrawableRes mod: Int?) {
        if (image == null) {
            Log.e(TAG, "Tried to add null image as an icon")
            return
        }

        // Create new small_icon layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.fragment_monster_summary_weakness, parentview, false)

        // Get reference to image in small_icon layout
        val mImage = view.findViewById<ImageView>(R.id.image)
        val mImageMod = view.findViewById<ImageView>(R.id.image_mod)

        // Open Image
        val mainImage = ContextCompat.getDrawable(context!!, image)
        mImage.setImageDrawable(mainImage)

        // Open Image Mod if applicable
        if (mod != null) {
            val modImage = ContextCompat.getDrawable(context!!, mod)
            mImageMod.setImageDrawable(modImage)
            mImageMod.visibility = View.VISIBLE
        }

        // Add small_icon to appropriate layout
        parentview.addView(view)
    }
}
