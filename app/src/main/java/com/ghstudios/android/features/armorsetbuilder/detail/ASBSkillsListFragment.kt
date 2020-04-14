package com.ghstudios.android.features.armorsetbuilder.detail

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.data.classes.ArmorSet
import com.ghstudios.android.features.armorsetbuilder.ArmorSetCalculator

/**
 * Fragment to display the list of skills granted from an armor set.
 */
class ASBSkillsListFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(ASBDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_asb_skills_list, container, false)

        val listView = v.findViewById<View>(R.id.list) as ListView

        val session = viewModel.session
        val calculator = ArmorSetCalculator(session)
        val adapter = ASBSkillsAdapter(activity!!.applicationContext, calculator.results, session)
        listView.adapter = adapter

        viewModel.updatePieceEvent.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            calculator.recalculate()
            adapter.notifyDataSetChanged()
        })

        return v
    }

    private class ASBSkillsAdapter(
            context: Context,
            trees: List<ArmorSetCalculator.SkillTreeInSet>,
            internal var session: ASBSession
    ) : ArrayAdapter<ArmorSetCalculator.SkillTreeInSet>(context, R.layout.fragment_asb_skills_listitem, trees) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)

            @SuppressLint("ViewHolder")
            val itemView = inflater.inflate(R.layout.fragment_asb_skills_listitem, parent, false)
            // Conditional inflation really isn't necessary simply because of how many skills you'd have to have.

            val treeName = itemView.findViewById<TextView>(R.id.skill_tree_name)
            val weaponPoints = itemView.findViewById<TextView>(R.id.weapon)
            val headPoints = itemView.findViewById<TextView>(R.id.helmet)
            val bodyPoints = itemView.findViewById<TextView>(R.id.body)
            val armsPoints = itemView.findViewById<TextView>(R.id.arms)
            val waistPoints = itemView.findViewById<TextView>(R.id.waist)
            val legsPoints = itemView.findViewById<TextView>(R.id.legs)
            val talismanPoints = itemView.findViewById<TextView>(R.id.talisman)
            val totalPoints = itemView.findViewById<TextView>(R.id.total)

            val data = getItem(position) ?: return itemView
            treeName.text = data.skillTree.name

            if (data.getPoints(ArmorSet.WEAPON) != 0) {
                weaponPoints.text = data.getPoints(ArmorSet.WEAPON).toString()
            }

            if (data.getPoints(ArmorSet.HEAD) != 0) {
                headPoints.text = data.getPoints(ArmorSet.HEAD).toString()
            }

            if (data.getPoints(ArmorSet.BODY) != 0) {
                bodyPoints.text = data.getPoints(ArmorSet.BODY).toString()
            }

            if (data.getPoints(ArmorSet.ARMS) != 0) {
                armsPoints.text = data.getPoints(ArmorSet.ARMS).toString()
            }

            if (data.getPoints(ArmorSet.WAIST) != 0) {
                waistPoints.text = data.getPoints(ArmorSet.WAIST).toString()
            }

            if (data.getPoints(ArmorSet.LEGS) != 0) {
                legsPoints.text = data.getPoints(ArmorSet.LEGS).toString()
            }

            if (data.getPoints(ArmorSet.TALISMAN) != 0) {
                talismanPoints.text = data.getPoints(ArmorSet.TALISMAN).toString()
            }

            totalPoints.text = data.getTotal().toString()

            if (data.active) {
                treeName.setTypeface(null, Typeface.BOLD)
                headPoints.setTypeface(null, Typeface.BOLD)
                bodyPoints.setTypeface(null, Typeface.BOLD)
                armsPoints.setTypeface(null, Typeface.BOLD)
                waistPoints.setTypeface(null, Typeface.BOLD)
                legsPoints.setTypeface(null, Typeface.BOLD)
                talismanPoints.setTypeface(null, Typeface.BOLD)
                totalPoints.setTypeface(null, Typeface.BOLD)
            }

            itemView.setOnClickListener(SkillClickListener(parent.context, data.skillTree.id))

            return itemView
        }
    }
}
