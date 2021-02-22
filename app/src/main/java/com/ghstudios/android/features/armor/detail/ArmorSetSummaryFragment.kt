package com.ghstudios.android.features.armor.detail

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.BasePagerActivity
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.ClickListeners.SkillClickListener
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
import com.ghstudios.android.mhgendatabase.databinding.FragmentArmorSetSummaryBinding

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

    private lateinit var binding: FragmentArmorSetSummaryBinding

    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(ArmorSetDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArmorSetSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.armors.observe(viewLifecycleOwner, Observer(::populateArmor))
        viewModel.setSkills.observe(viewLifecycleOwner, Observer(::populateSkills))
        viewModel.setComponents.observe(viewLifecycleOwner, Observer(::populateComponents))
    }

    private fun populateArmor(armorPoints: List<ArmorSkillPoints>?){
        if(armorPoints == null) return

        val armors = armorPoints.map { it.armor }

        val cellDefense = armors.sumBy { it.defense }.toString() + "~" + armors.sumBy { it.maxDefense }

        binding.rare.setValueText(armors.first().rarityString)
        binding.defense.setValueText(cellDefense)
        binding.weaponType.setValueText(getString(when (determineArmorSetType(armors)) {
            Armor.ARMOR_TYPE_BLADEMASTER -> R.string.armor_type_blade
            Armor.ARMOR_TYPE_GUNNER -> R.string.armor_type_gunner
            else -> R.string.armor_type_both
        }))

        with(binding.armorResits) {
            fireRes.text = armors.sumBy { it.fireRes }.toString()
            waterRes.text = armors.sumBy { it.waterRes }.toString()
            iceRes.text = armors.sumBy { it.iceRes }.toString()
            thunderRes.text = armors.sumBy { it.thunderRes }.toString()
            dragonRes.text = armors.sumBy { it.dragonRes }.toString()
        }

        val inflater = LayoutInflater.from(context)

        // Populate the armor piece list
        for ((idx, armorPointsEntry) in armorPoints.withIndex()) {
            val armor = armorPointsEntry.armor
            val skills = armorPointsEntry.skills

            val armorView = inflater.inflate(R.layout.listitem_armor_piece, binding.armorPiecesLayout,false)
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

            binding.armorPiecesLayout.addView(armorView)
        }
    }

    /**
     * Populates the view with armor set skills gained from equipping the entire set.
     */
    private fun populateSkills(skills: List<SkillTreePoints>?){
        if (skills == null || skills.isEmpty()) {
            binding.skillSection.visibility = View.GONE
            return
        }

        binding.skillList.removeAllViews()
        binding.skillSection.visibility = View.VISIBLE

        for (skill in skills) {
            val skillItem = LabelTextRowCell(context)
            skillItem.setLabelText(skill.skillTree.name)
            skillItem.setValueText(skill.points.toString())

            skillItem.setOnClickListener(
                    SkillClickListener(context, skill.skillTree.id)
            )

            binding.skillList.addView(skillItem)
        }
    }

    /**
     * Populates the view with the setComponents needed to build the entire set.
     */
    private fun populateComponents(recipe:List<Component>?){
        if (recipe == null || recipe.isEmpty()) {
            binding.recipeHeader.visibility = View.GONE
            binding.recipe.visibility = View.GONE
            return
        }

        binding.recipeHeader.visibility = View.VISIBLE
        binding.recipe.visibility = View.VISIBLE

        for (component in recipe) {
            val item = component.component
            val itemCell = binding.recipe.addItem(item, item.name, component.quantity,component.isKey)
            itemCell.setOnClickListener(ItemClickListener(context!!, item))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_to_wishlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_to_wishlist -> {
                val fm = this.parentFragmentManager
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
