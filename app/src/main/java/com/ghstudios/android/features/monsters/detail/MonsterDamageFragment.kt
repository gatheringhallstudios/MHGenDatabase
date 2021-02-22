package com.ghstudios.android.features.monsters.detail

import androidx.lifecycle.Observer
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.*
import com.ghstudios.android.data.classes.MonsterDamage
import com.ghstudios.android.data.classes.MonsterStatus
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.mhgendatabase.databinding.FragmentMonsterDamageBinding

class MonsterDamageFragment : Fragment() {
    companion object {
        private val ARG_MONSTER_ID = "MONSTER_ID"

        @JvmStatic
        fun newInstance(monsterId: Long): MonsterDamageFragment {
            val args = Bundle()
            args.putLong(ARG_MONSTER_ID, monsterId)
            val f = MonsterDamageFragment()
            f.arguments = args
            return f
        }
    }

    private lateinit var binding: FragmentMonsterDamageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMonsterDamageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(activity!!).get(MonsterDetailViewModel::class.java)

        viewModel.monsterData.observe(viewLifecycleOwner, Observer { monster ->
            if (monster == null) return@Observer
            binding.header.detailMonsterLabel.text = monster.name
            AssetLoader.setIcon(binding.header.detailMonsterImage, monster)
        })

        viewModel.damageData.observe(viewLifecycleOwner, Observer<List<MonsterDamage>> { this.populateDamage(it) })
        viewModel.statusData.observe(viewLifecycleOwner, Observer<List<MonsterStatus>> { this.populateStatus(it) })
    }

    private fun populateDamage(damages: List<MonsterDamage>?) {
        if (damages == null || damages.isEmpty()) return

        binding.weaponDamage.removeAllViews()
        binding.elementalDamage.removeAllViews()

        val altStateColor = ContextCompat.getColor(context!!, R.color.text_color_secondary)
        val altStateSpan = ForegroundColorSpan(altStateColor)

        val inflater = LayoutInflater.from(this.context)

        // Internal helper to style the alternative state
        fun createStyledString(bodyPart: String): SpannableString {
            val s = SpannableString(bodyPart)
            if (bodyPart.contains("(")) {
                val start = bodyPart.indexOf("(")
                val end = bodyPart.length
                s.setSpan(altStateSpan, start, end, 0)
                s.setSpan(RelativeSizeSpan(.8f), start, end, 0)
            }
            return s
        }

        // non-elemental table
        for (damage in damages) {
            val wdRow = inflater.inflate(
                    R.layout.fragment_monster_damage_listitem, binding.weaponDamage, false)

            val body_part_tv1 = wdRow.findViewById<TextView>(R.id.body_part)
            val dummy_tv = wdRow.findViewById<TextView>(R.id.dmg1)
            val cut_tv = wdRow.findViewById<TextView>(R.id.dmg2)
            val impact_tv = wdRow.findViewById<TextView>(R.id.dmg3)
            val shot_tv = wdRow.findViewById<TextView>(R.id.dmg4)
            val ko_tv = wdRow.findViewById<TextView>(R.id.dmg5)

            body_part_tv1.text = createStyledString(damage.bodyPart)

            checkDamageValue(damage.cut, cut_tv, false, false)
            checkDamageValue(damage.impact, impact_tv, false, false)
            checkDamageValue(damage.shot, shot_tv, false, false)
            checkDamageValue(damage.ko, ko_tv, false, true)

            dummy_tv.text = ""

            binding.weaponDamage.addView(wdRow)
        }

        // Elemental table
        for (damage in damages) {
            val edRow = inflater.inflate(
                    R.layout.fragment_monster_damage_listitem, binding.elementalDamage, false)

            val body_part_tv2 = edRow.findViewById<TextView>(R.id.body_part)
            val fire_tv = edRow.findViewById<TextView>(R.id.dmg1)
            val water_tv = edRow.findViewById<TextView>(R.id.dmg2)
            val ice_tv = edRow.findViewById<TextView>(R.id.dmg3)
            val thunder_tv = edRow.findViewById<TextView>(R.id.dmg4)
            val dragon_tv = edRow.findViewById<TextView>(R.id.dmg5)

            body_part_tv2.text = createStyledString(damage.bodyPart)

            checkDamageValue(damage.fire, fire_tv, true, false)
            checkDamageValue(damage.water, water_tv, true, false)
            checkDamageValue(damage.ice, ice_tv, true, false)
            checkDamageValue(damage.thunder, thunder_tv, true, false)
            checkDamageValue(damage.dragon, dragon_tv, true, false)

            binding.elementalDamage.addView(edRow)
        }
    }

    private fun checkDamageValue(damage: Int, tv: TextView, element: Boolean, isKO: Boolean): String {
        var ret = Integer.toString(damage)
        if (damage <= 0)
            ret = "-"

        tv.text = ret

        if (!isKO && !element && damage >= 45 || element && damage >= 25)
            tv.setTypeface(null, Typeface.BOLD)

        return ret
    }

    private fun populateStatus(statuses: List<MonsterStatus>?) {
        if (statuses == null || statuses.isEmpty()) return

        binding.statusData.removeAllViews()

        val inflater = LayoutInflater.from(this.context)

        for (currentStatus in statuses) {
            val wdRow = inflater.inflate(
                    R.layout.fragment_monster_status_listitem, binding.statusData, false)

            val DefaultString = "-"
            fun valToString(v: Long, suffix: String=""): String = when(v) {
                -1L, 0L -> DefaultString
                else -> v.toString() + suffix
            }

            // Get our strings
            val initial = valToString(currentStatus.initial)
            val increase = valToString(currentStatus.increase)
            val max = valToString(currentStatus.max)
            val duration = valToString(currentStatus.duration, "s")
            val damage = valToString(currentStatus.damage)


            val statusImage = wdRow.findViewById<View>(R.id.statusImage) as ImageView
            val initialView = wdRow.findViewById<View>(R.id.initial) as TextView
            val increaseView = wdRow.findViewById<View>(R.id.increase) as TextView
            val maxView = wdRow.findViewById<View>(R.id.max) as TextView
            val durationView = wdRow.findViewById<View>(R.id.duration) as TextView
            val damageView = wdRow.findViewById<View>(R.id.damage) as TextView

            // Check which image to load
            val element = currentStatus.statusEnum
            val imageFile = ElementRegistry.get(element, R.color.transparent)

            // initialize our views
            initialView.text = initial
            increaseView.text = increase
            maxView.text = max
            durationView.text = duration
            damageView.text = damage

            if (imageFile != -1) {
                val draw = ContextCompat.getDrawable(context!!, imageFile)
                val layoutParams = statusImage.layoutParams
                statusImage.layoutParams = layoutParams
                statusImage.setImageDrawable(draw)
            }

            binding.statusData.addView(wdRow)
        }
    }
}
