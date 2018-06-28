package com.ghstudios.android.features.monsters.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.ghstudios.android.AppSettings
import com.ghstudios.android.ClickListeners.LocationClickListener
import com.ghstudios.android.MHUtils
import com.ghstudios.android.components.SectionHeaderCell
import com.ghstudios.android.components.TitleBarCell
import com.ghstudios.android.data.classes.Habitat
import com.ghstudios.android.data.classes.MonsterAilment
import com.ghstudios.android.mhgendatabase.R

import butterknife.BindView
import butterknife.ButterKnife
import com.ghstudios.android.data.classes.WeaknessType

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

    @BindView(R.id.monster_header)
    lateinit var headerView: TitleBarCell

    @BindView(R.id.monster_state_list)
    lateinit var statesListView: LinearLayout

    @BindView(R.id.ailments_data) lateinit var ailmentListView: LinearLayout
    @BindView(R.id.habitat_list) lateinit var habitatListView: LinearLayout

    @BindView(R.id.ailments_empty) lateinit var ailmentsEmpty: View
    @BindView(R.id.habitats_empty) lateinit var habitatsEmpty: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_monster_summary, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProviders.of(activity!!).get(MonsterDetailViewModel::class.java)

        viewModel.monsterData.observe(this, Observer { monster ->
            if (monster == null) return@Observer

            val cellImage = "icons_monster/" + monster.getFileLocation()
            val monsterImage = MHUtils.loadAssetDrawable(context, cellImage)

            headerView.setIconDrawable(monsterImage)
            headerView.setTitleText(monster.name)
            headerView.setAltTitleText(monster.jpnName)
            headerView.setAltTitleEnabled(AppSettings.isJapaneseEnabled)
        })

        viewModel.weaknessData.observe(this, Observer(::updateWeaknesses))
        viewModel.ailmentData.observe(this, Observer(::populateAilments))
        viewModel.habitatData.observe(this, Observer(::populateHabitats))
    }

    /**
     * Populates weakness data in the view using the provided data.
     * If null or empty, then nothing is rendered regarding weaknesses
     */
    private fun updateWeaknesses(weaknesses: List<MonsterWeaknessResult>?) {
        statesListView.removeAllViews()
        if (weaknesses == null || weaknesses.isEmpty()) return

        for (weakness in weaknesses) {
            addWeakness(weakness)
        }
    }

    private fun addWeakness(mWeakness: MonsterWeaknessResult) {
        val inflater = LayoutInflater.from(context)
        val weaknessView = inflater.inflate(R.layout.fragment_monster_summary_state, statesListView, false)

        // Set title
        val header = weaknessView.findViewById<SectionHeaderCell>(R.id.state_name)
        header.setLabelText(mWeakness.state)

        val weaknessListView = weaknessView.findViewById<ViewGroup>(R.id.weakness_data)
        val itemListView = weaknessView.findViewById<ViewGroup>(R.id.item_data)

        // weakness line
        for (value in mWeakness.elementAndStatus) {
            val imagePath = when (value.type) {
                WeaknessType.FIRE -> resources.getString(R.string.image_location_fire)
                WeaknessType.WATER -> resources.getString(R.string.image_location_water)
                WeaknessType.THUNDER -> resources.getString(R.string.image_location_thunder)
                WeaknessType.ICE -> resources.getString(R.string.image_location_ice)
                WeaknessType.DRAGON -> resources.getString(R.string.image_location_dragon)
                WeaknessType.POISON -> resources.getString(R.string.image_location_poison)
                WeaknessType.PARALYSIS -> resources.getString(R.string.image_location_paralysis)
                WeaknessType.SLEEP -> resources.getString(R.string.image_location_sleep)
                else -> null
            }

            val imageModification = when (value.rating) {
                WeaknessRating.WEAK -> resources.getString(R.string.image_location_effectiveness_2)
                WeaknessRating.VERY_WEAK -> resources.getString(R.string.image_location_effectiveness_3)
                else -> null
            }

            imagePath?.let {
                addIcon(weaknessListView, imagePath, imageModification)
            }
        }

        // items line
        for (trapType in mWeakness.items) {
            val imagePath = when (trapType) {
                WeaknessType.PITFALL_TRAP -> resources.getString(R.string.image_location_pitfall_trap)
                WeaknessType.SHOCK_TRAP -> resources.getString(R.string.image_location_shock_trap)
                WeaknessType.MEAT -> resources.getString(R.string.image_location_meat)
                WeaknessType.FLASH_BOMB -> resources.getString(R.string.image_location_flash_bomb)
                WeaknessType.SONIC_BOMB -> resources.getString(R.string.image_location_sonic_bomb)
                WeaknessType.DUNG_BOMB -> resources.getString(R.string.image_location_dung_bomb)
                else -> null
            }

            imagePath?.let {
                addIcon(itemListView, imagePath, null)
            }
        }

        statesListView.addView(weaknessView)
    }

    /**
     * Populates ailment data in the view using the provided data.
     * If null or empty is given, the blank slate is shown instead.
     */
    private fun populateAilments(ailments: List<MonsterAilment>?) {
        // if no ailments, show blank slate instead of the ailment list, and return
        if (ailments == null || ailments.isEmpty()) {
            ailmentsEmpty.visibility = View.VISIBLE
            ailmentListView.visibility = View.GONE
            return
        }

        // hide blank slate, and make the ailment list visible
        ailmentsEmpty.visibility = View.GONE
        ailmentListView.visibility = View.VISIBLE
        ailmentListView.removeAllViews()

        val inflater = LayoutInflater.from(context)

        for (ailment in ailments) {
            val ailmentView = inflater.inflate(R.layout.fragment_ailment_listitem, ailmentListView, false)

            val labelView = ailmentView.findViewById<TextView>(R.id.ailment_text)
            labelView.text = ailment.ailment

            ailmentListView.addView(ailmentView)
        }
    }

    /**
     * Populates habitat data in th e view using the provided data.
     * If null or empty is given, the blank slate is shown instead.
     */
    private fun populateHabitats(habitats: List<Habitat>?) {
        if (habitats == null || habitats.isEmpty()) {
            habitatsEmpty.visibility = View.VISIBLE
            return
        }

        habitatsEmpty.visibility = View.GONE
        val inflater = LayoutInflater.from(context)

        habitatListView.removeAllViews()
        for (habitat in habitats) {
            val view = inflater.inflate(R.layout.fragment_monster_habitat_listitem, habitatListView, false)

            val itemLayout = view.findViewById<RelativeLayout>(R.id.listitem)
            val mapView = view.findViewById<ImageView>(R.id.mapImage)
            val mapTextView = view.findViewById<TextView>(R.id.map)
            val startTextView = view.findViewById<TextView>(R.id.start)
            val areaTextView = view.findViewById<TextView>(R.id.move)
            val restTextView = view.findViewById<TextView>(R.id.rest)

            mapTextView.text = habitat.location.name
            startTextView.text = habitat.start.toString()
            areaTextView.text = habitat.areas.joinToString(", ")
            restTextView.text = habitat.rest.toString()

            val cellImage = "icons_location/" + habitat.location.fileLocationMini
            val mapImage = MHUtils.loadAssetDrawable(context, cellImage)

            mapView.setImageDrawable(mapImage)

            val locationId = habitat.location.id
            itemLayout.tag = locationId
            itemLayout.setOnClickListener(LocationClickListener(context, locationId))

            habitatListView.addView(view)
        }
    }

    // Add small_icon to a particular LinearLayout
    private fun addIcon(parentview: ViewGroup, imagelocation: String, imagemodlocation: String?) {
        // Create new small_icon layout
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.small_icon, parentview, false)

        // Get reference to image in small_icon layout
        val mImage = view.findViewById<ImageView>(R.id.image)
        val mImageMod = view.findViewById<ImageView>(R.id.image_mod)

        // Open Image
        val image = MHUtils.loadAssetDrawable(context, imagelocation)
        mImage.setImageDrawable(image)

        // Open Image Mod if applicable
        if (imagemodlocation != null) {
            val modImage = MHUtils.loadAssetDrawable(context, imagemodlocation)
            mImageMod.setImageDrawable(modImage)
            mImageMod.visibility = View.VISIBLE
        }

        // Add small_icon to appropriate layout
        parentview.addView(view)
    }
}
