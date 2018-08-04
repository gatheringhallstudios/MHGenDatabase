package com.ghstudios.android.features.armor

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.MHApplication
import com.ghstudios.android.MHUtils
import com.ghstudios.android.components.ColumnLabelTextCell
import com.ghstudios.android.components.ItemRecipeCell
import com.ghstudios.android.components.LabelTextCell
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.mhgendatabase.R

class ArmorSetSummaryFragment : Fragment() {

    @BindView(R.id.rare) lateinit var rareView: ColumnLabelTextCell
    @BindView(R.id.defense) lateinit var defenseView: ColumnLabelTextCell

    @BindView(R.id.skill_section) lateinit var skillSection: ViewGroup
    @BindView(R.id.skill_list) lateinit var skillListView: LinearLayout

    @BindView(R.id.recipe_header) lateinit var recipeHeader: View
    @BindView(R.id.recipe) lateinit var recipeView: ItemRecipeCell

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
        viewModel.skills.observe(this, Observer { populateSkills(it,viewModel.armors.value) })
        viewModel.components.observe(this, Observer { populateComponents(it)})
    }

    private fun populateArmor(armors: List<Armor>?){
        if(armors == null) return


        val cellDefense = armors.sumBy { it.defense }.toString() + "~" + armors.sumBy { it.maxDefense }

        rareView.setValueText(armors.first().rarityString)
        defenseView.setValueText(cellDefense)

        fireResTextView.text = armors.sumBy { it.fireRes }.toString()
        waterResTextView.text = armors.sumBy { it.waterRes }.toString()
        iceResTextView.text = armors.sumBy { it.iceRes }.toString()
        thunderResTextView.text = armors.sumBy { it.thunderRes }.toString()
        dragonResTextView.text = armors.sumBy { it.dragonRes }.toString()

    }

    private fun populateSkills(skills: HashMap<Long,List<ItemToSkillTree>>?, armors:List<Armor>?){
        if(skills == null || armors == null)return


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

            skillListView.addView(skillItem)
        }

        //Load Armor List
        val armorLayout:LinearLayout? = view?.findViewById(R.id.armor_pieces_layout)
        armors.forEach {
            val armorView = LayoutInflater.from(context).inflate(R.layout.fragment_armor_set_piece_listitem,armorLayout,false)
            val icon: ImageView? = armorView.findViewById(R.id.icon)
            val name:TextView? = armorView.findViewById(R.id.name)
            val slots:TextView? = armorView.findViewById(R.id.slots)
            val skillsTvs : Array<TextView?> = arrayOf(armorView.findViewById(R.id.skill_1),
                    armorView.findViewById(R.id.skill_2),
                    armorView.findViewById(R.id.skill_3),
                    armorView.findViewById(R.id.skill_4))
            skillsTvs.forEach { it?.visibility=View.GONE }
            AssetLoader.setIcon(icon!!,it)
            name?.text = it.name
            slots?.text = it.slotString
            if(skills.containsKey(it.id)){
                for(i in skills[it.id]!!.indices){
                    skillsTvs[i]?.visibility = View.VISIBLE
                    val points = skills[it.id]!![i].points
                    val skillString = skills[it.id]!![i].skillTree.name + if(points>0) "+$points" else points
                    skillsTvs[i]?.text = skillString
                }
            }
            armorLayout?.addView(armorView)
        }

    }

    private fun populateComponents(recipe:List<Component>?){
        if (recipe == null || recipe.isEmpty()) {
            recipeHeader.visibility = View.GONE
            recipeView.visibility = View.GONE
            return
        }

        recipeHeader.visibility = View.VISIBLE
        recipeView.visibility = View.VISIBLE

        for (component in recipe) {
            val item = component.component
            val itemCell = recipeView.addItem(item, item.name, component.quantity)
            itemCell.setOnClickListener(ItemClickListener(context, item))
        }
    }
}