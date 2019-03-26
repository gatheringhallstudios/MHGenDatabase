package com.ghstudios.android.features.armor.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.components.ColumnLabelTextCell
import com.ghstudios.android.components.ItemRecipeCell
import com.ghstudios.android.components.LabelTextRowCell
import com.ghstudios.android.components.SlotsView
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ArmorSkillPoints
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.SkillTreePoints
import com.ghstudios.android.features.wishlist.external.WishlistDataAddDialogFragment
import com.ghstudios.android.features.wishlist.external.WishlistItemType
import com.ghstudios.android.features.wishlist.list.WishlistListFragment
import com.ghstudios.android.mhgendatabase.R

/**
 * Figures out what kind of armor set type the entirety of the armor set uses.
 * It assumes a mix of ANY and one other. It returns that one other, or ANY if its the only type.
 */
fun determineArmorSetType(armor: List<Armor>): Int {
    val types = armor.map {it.hunterType }.distinct()
    if (types.size == 1) {
        return types[0]
    }
    return types.first { it != Armor.ARMOR_TYPE_BOTH }
}

class ArmorSetSummaryFragment : Fragment() {

    @BindView(R.id.rare) lateinit var rareView: ColumnLabelTextCell
    @BindView(R.id.weapon_type) lateinit var typeView: ColumnLabelTextCell
    @BindView(R.id.defense) lateinit var defenseView: ColumnLabelTextCell

    @BindView(R.id.armor_pieces_layout) lateinit var armorListView: ViewGroup

    @BindView(R.id.skill_section) lateinit var skillSection: ViewGroup
    @BindView(R.id.skill_list) lateinit var skillListView: LinearLayout

    @BindView(R.id.recipe_header) lateinit var recipeHeader: View
    @BindView(R.id.recipe) lateinit var recipeView: ItemRecipeCell

    @BindView(R.id.fire_res) lateinit var fireResTextView: TextView
    @BindView(R.id.water_res) lateinit var waterResTextView: TextView
    @BindView(R.id.ice_res) lateinit var iceResTextView: TextView
    @BindView(R.id.thunder_res) lateinit var thunderResTextView: TextView
    @BindView(R.id.dragon_res) lateinit var dragonResTextView: TextView

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ArmorSetDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_armor_set_summary, container,false)
        ButterKnife.bind(this,view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.armors.observe(this, Observer(::populateArmor))
        viewModel.setSkills.observe(this, Observer(::populateSkills))
        viewModel.setComponents.observe(this, Observer(::populateComponents))
    }

    private fun populateArmor(armorPoints: List<ArmorSkillPoints>?){
        if(armorPoints == null) return

        val armors = armorPoints.map { it.armor }

        val cellDefense = armors.sumBy { it.defense }.toString() + "~" + armors.sumBy { it.maxDefense }

        rareView.setValueText(armors.first().rarityString)
        defenseView.setValueText(cellDefense)
        typeView.setValueText(getString(when (determineArmorSetType(armors)) {
            Armor.ARMOR_TYPE_BLADEMASTER -> R.string.armor_type_blade
            Armor.ARMOR_TYPE_GUNNER -> R.string.armor_type_gunner
            else -> R.string.armor_type_both
        }))

        fireResTextView.text = armors.sumBy { it.fireRes }.toString()
        waterResTextView.text = armors.sumBy { it.waterRes }.toString()
        iceResTextView.text = armors.sumBy { it.iceRes }.toString()
        thunderResTextView.text = armors.sumBy { it.thunderRes }.toString()
        dragonResTextView.text = armors.sumBy { it.dragonRes }.toString()

        val inflater = LayoutInflater.from(context)

        // Populate the armor piece list
        for ((idx, armorPointsEntry) in armorPoints.withIndex()) {
            val armor = armorPointsEntry.armor
            val skills = armorPointsEntry.skills

            val armorView = inflater.inflate(R.layout.listitem_armor_piece, armorListView,false)
            val icon: ImageView? = armorView.findViewById(R.id.icon)
            val name: TextView? = armorView.findViewById(R.id.name)
            val slots: SlotsView? = armorView.findViewById(R.id.slots)

            AssetLoader.setIcon(icon!!,armor)
            name?.text = armor.name
            slots?.setSlots(armor.numSlots, 0)

            val skillsTvs : Array<TextView?> = arrayOf(armorView.findViewById(R.id.skill_1),
                    armorView.findViewById(R.id.skill_2),
                    armorView.findViewById(R.id.skill_3),
                    armorView.findViewById(R.id.skill_4))
            skillsTvs.forEach { it?.visibility=View.GONE }

            for((i, skill) in skills.withIndex()) {
                    skillsTvs[i]?.visibility = View.VISIBLE
                    val points = skill.points
                    val skillString = skill.skillTree.name + if(points>0) "+$points" else points
                    skillsTvs[i]?.text = skillString
            }

            // clicking on the armor piece should change to the tab to that armor
            armorView.setOnClickListener {
                val activity = this.activity as? BasePagerActivity
                activity?.setSelectedTab(idx + 1)
            }

            armorListView.addView(armorView)
        }
    }

    /**
     * Populates the view with armor set skills gained from equipping the entire set.
     */
    private fun populateSkills(skills: List<SkillTreePoints>?){
        if (skills == null || skills.isEmpty()) {
            skillSection.visibility = View.GONE
            return
        }

        skillListView.removeAllViews()
        skillSection.visibility = View.VISIBLE

        for (skill in skills) {
            val skillItem = LabelTextRowCell(context)
            skillItem.setLabelText(skill.skillTree.name)
            skillItem.setValueText(skill.points.toString())

            skillItem.setOnClickListener(
                    SkillClickListener(context, skill.skillTree.id)
            )

            skillListView.addView(skillItem)
        }
    }

    /**
     * Populates the view with the setComponents needed to build the entire set.
     */
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
            val itemCell = recipeView.addItem(item, item.name, component.quantity,component.isKey)
            itemCell.setOnClickListener(ItemClickListener(context!!, item))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_add_to_wishlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.add_to_wishlist -> {
                val fm = this.fragmentManager
                WishlistDataAddDialogFragment.newInstance(
                        WishlistItemType.ARMORSET,
                        viewModel.familyId,
                        viewModel.familyName).show(fm, WishlistListFragment.DIALOG_WISHLIST_ADD)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}