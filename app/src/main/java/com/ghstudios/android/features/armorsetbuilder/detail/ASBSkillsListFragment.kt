package com.ghstudios.android.features.armorsetbuilder.detail

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
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
        ViewModelProviders.of(activity!!).get(ASBDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_asb_skills_list, container, false)

        val listView = v.findViewById<View>(R.id.list) as ListView

        val session = viewModel.session
        val calculator = ArmorSetCalculator(session)
        val adapter = ASBSkillsAdapter(activity!!.applicationContext, calculator.results, session)
        listView.adapter = adapter

        viewModel.updatePieceEvent.observe(this, Observer {
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

            val treeName = itemView.findViewById<View>(R.id.skill_tree_name) as TextView
            val headPoints = itemView.findViewById<View>(R.id.helmet) as TextView
            val bodyPoints = itemView.findViewById<View>(R.id.body) as TextView
            val armsPoints = itemView.findViewById<View>(R.id.arms) as TextView
            val waistPoints = itemView.findViewById<View>(R.id.waist) as TextView
            val legsPoints = itemView.findViewById<View>(R.id.legs) as TextView
            val talismanPoints = itemView.findViewById<View>(R.id.talisman) as TextView
            val totalPoints = itemView.findViewById<View>(R.id.total) as TextView

            val data = getItem(position)
            
            treeName.text = data.skillTree.name

            if (session.getEquipment(ArmorSet.HEAD) != null && data.getPoints(ArmorSet.HEAD) != 0) {
                headPoints.text = data.getPoints(ArmorSet.HEAD).toString()
            }

            if (session.getEquipment(ArmorSet.BODY) != null && data.getPoints(ArmorSet.BODY) != 0) {
                bodyPoints.text = data.getPoints(ArmorSet.BODY).toString()
            }

            if (session.getEquipment(ArmorSet.ARMS) != null && data.getPoints(ArmorSet.ARMS) != 0) {
                armsPoints.text = data.getPoints(ArmorSet.ARMS).toString()
            }

            if (session.getEquipment(ArmorSet.WAIST) != null && data.getPoints(ArmorSet.WAIST) != 0) {
                waistPoints.text = data.getPoints(ArmorSet.WAIST).toString()
            }

            if (session.getEquipment(ArmorSet.LEGS) != null && data.getPoints(ArmorSet.LEGS) != 0) {
                legsPoints.text = data.getPoints(ArmorSet.LEGS).toString()
            }

            if (session.getEquipment(ArmorSet.TALISMAN) != null && data.getPoints(ArmorSet.TALISMAN) != 0) {
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
