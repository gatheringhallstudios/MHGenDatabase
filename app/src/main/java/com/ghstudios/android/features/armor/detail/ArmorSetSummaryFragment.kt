package com.ghstudios.android.features.armor.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.components.*
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

        val armorBinder = ArmorSkillPointsViewBinder()

        // Populate the armor piece list
        for ((idx, armorPointsEntry) in armorPoints.withIndex()) {
            val armorView = armorBinder.createView(armorListView)
            armorBinder.bindView(armorView, armorPointsEntry)

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