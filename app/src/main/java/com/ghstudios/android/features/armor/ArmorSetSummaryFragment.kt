package com.ghstudios.android.features.armor

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.components.ColumnLabelTextCell
import com.ghstudios.android.components.ItemRecipeCell
import com.ghstudios.android.components.LabelTextCell
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.mhgendatabase.R

class ArmorSetSummaryFragment : Fragment() {

    //@BindView(R.id.rare) lateinit var rareView: ColumnLabelTextCell
    //@BindView(R.id.slots) lateinit var slotsReqView: ColumnLabelTextCell
    //@BindView(R.id.defense) lateinit var defenseView: ColumnLabelTextCell
    //@BindView(R.id.part) lateinit var partView: ColumnLabelTextCell

    @BindView(R.id.skill_section) lateinit var skillSection: ViewGroup
    @BindView(R.id.skill_list) lateinit var skillListView: LinearLayout

    //@BindView(R.id.recipe_header) lateinit var recipeHeader: View
    //@BindView(R.id.recipe) lateinit var recipeView: ItemRecipeCell

    @BindView(R.id.fire_res) lateinit var fireResTextView: TextView
    @BindView(R.id.water_res) lateinit var waterResTextView: TextView
    @BindView(R.id.ice_res) lateinit var iceResTextView: TextView
    @BindView(R.id.thunder_res) lateinit var thunderResTextView: TextView
    @BindView(R.id.dragon_res) lateinit var dragonResTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_armor_set_summary, container,false)
        ButterKnife.bind(this,view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(activity!!).get(ArmorSetDetailViewModel::class.java)
        viewModel.armors.observe(this, Observer { populateArmor(it) })
        viewModel.skills.observe(this, Observer { populateSkills(it) })
    }

    fun populateArmor(armors: List<Armor>?){
        if(armors == null) return

        fireResTextView.text = armors.sumBy { it.fireRes }.toString()
        waterResTextView.text = armors.sumBy { it.waterRes }.toString()
        iceResTextView.text = armors.sumBy { it.iceRes }.toString()
        thunderResTextView.text = armors.sumBy { it.thunderRes }.toString()
        dragonResTextView.text = armors.sumBy { it.dragonRes }.toString()

    }

    fun populateSkills(skills: HashMap<Long,List<ItemToSkillTree>>?){
        if(skills == null)return

        //skills is organized per armor piece, switch it to total for each skill
        val skillTotal = HashMap<Long,ItemToSkillTree>()
        skills.forEach { it.value.forEach {
            if(skillTotal.containsKey(it.skillTree.id)){
                skillTotal[it.skillTree.id]!!.points += it.points
            }else{
                skillTotal[it.skillTree.id] = ItemToSkillTree().apply {
                    id = it.id
                    points = it.points
                    skillTree = it.skillTree
                }
            }
        } }

        skillListView.removeAllViews()
        if (skillTotal.size == 0) {
            skillSection.visibility = View.GONE
            return
        }

        skillSection.visibility = View.VISIBLE
        for (skill in skillTotal.values) {
            val skillItem = LabelTextCell(context)
            skillItem.setLabelText(skill.skillTree.name)
            skillItem.setValueText(skill.points.toString())

            skillItem.setOnClickListener(
                    SkillClickListener(context, skill.skillTree.id)
            )

            skillListView?.addView(skillItem)
        }
    }
}