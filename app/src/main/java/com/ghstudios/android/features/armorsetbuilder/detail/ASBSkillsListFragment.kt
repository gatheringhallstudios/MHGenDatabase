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
import com.ghstudios.android.data.classes.ASBSession.*
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.SkillClickListener

import java.util.Comparator


private const val MINIMUM_SKILL_ACTIVATION_POINTS = 10

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
        val adapter = ASBSkillsAdapter(activity!!.applicationContext, session.skillTreesInSet, session)
        listView.adapter = adapter

        viewModel.updatePieceEvent.observe(this, Observer {
            if (it == null) return@Observer
            adapter.notifyDataSetChanged()
        })

        return v
    }

    private class ASBSkillsAdapter(context: Context, trees: List<ASBSession.SkillTreeInSet>, internal var session: ASBSession) : ArrayAdapter<ASBSession.SkillTreeInSet>(context, R.layout.fragment_asb_skills_listitem, trees) {
        internal var trees: List<SkillTreeInSet>

        internal var comparator: Comparator<ASBSession.SkillTreeInSet> = Comparator { lhs, rhs -> rhs.getTotal(trees) - lhs.getTotal(trees) }

        init {
            this.trees = trees
        }

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

            treeName.text = getItem(position)!!.skillTree.name

            if (session.isEquipmentSelected(ASBSession.HEAD) && getItem(position)!!.getPoints(ASBSession.HEAD) != 0) {
                headPoints.text = getItem(position)!!.getPoints(ASBSession.HEAD).toString()
            }

            if (session.isEquipmentSelected(ASBSession.BODY) && getItem(position)!!.getPoints(ASBSession.BODY, trees) != 0) { // NOTICE: We have to call the alternate getPoints method due to the possibility of Torso Up pieces.
                bodyPoints.text = getItem(position)!!.getPoints(ASBSession.BODY, trees).toString()
            }

            if (session.isEquipmentSelected(ASBSession.ARMS) && getItem(position)!!.getPoints(ASBSession.ARMS) != 0) {
                armsPoints.text = getItem(position)!!.getPoints(ASBSession.ARMS).toString()
            }

            if (session.isEquipmentSelected(ASBSession.WAIST) && getItem(position)!!.getPoints(ASBSession.WAIST) != 0) {
                waistPoints.text = getItem(position)!!.getPoints(ASBSession.WAIST).toString()
            }

            if (session.isEquipmentSelected(ASBSession.LEGS) && getItem(position)!!.getPoints(ASBSession.LEGS) != 0) {
                legsPoints.text = getItem(position)!!.getPoints(ASBSession.LEGS).toString()
            }

            if (session.isEquipmentSelected(ASBSession.TALISMAN) && getItem(position)!!.getPoints(ASBSession.TALISMAN) != 0) {
                talismanPoints.text = getItem(position)!!.getPoints(ASBSession.TALISMAN).toString()
            }

            totalPoints.text = getItem(position)!!.getTotal(trees).toString()

            if (getItem(position)!!.getTotal(trees) >= MINIMUM_SKILL_ACTIVATION_POINTS) {
                treeName.setTypeface(null, Typeface.BOLD)
                headPoints.setTypeface(null, Typeface.BOLD)
                bodyPoints.setTypeface(null, Typeface.BOLD)
                armsPoints.setTypeface(null, Typeface.BOLD)
                waistPoints.setTypeface(null, Typeface.BOLD)
                legsPoints.setTypeface(null, Typeface.BOLD)
                talismanPoints.setTypeface(null, Typeface.BOLD)
                totalPoints.setTypeface(null, Typeface.BOLD)
            }

            itemView.setOnClickListener(SkillClickListener(parent.context, getItem(position)!!.skillTree.id))

            return itemView
        }

        override fun notifyDataSetChanged() {
            setNotifyOnChange(false)
            sort(comparator)

            super.notifyDataSetChanged() // super#notifyDataSetChanged automatically sets notifyOnChange back to true.
        }
    }
}
