package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.components.SlotsView
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.data.classes.ArmorSet
import com.ghstudios.android.data.classes.Decoration
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity
import com.ghstudios.android.features.decorations.list.DecorationListActivity
import com.ghstudios.android.util.getColorCompat
import com.ghstudios.android.util.setImageAsset
import java.util.*

// TODO: While refactoring has started, many more refactoring efforts are required to make this "good"
// Overall, this entire container relies on having an entire session with a "piece index". Instead,
// it should have an encapsulated object representing a piece.
//
// Also, it uses activity results to modify the session object. Instead it should contain a callback
// that it executes, which the parent fragment listens too. The callback should probably be a special interface
// that is an ASBPieceResponder or something like that.

/**
 * Image alpha value for unselected items
 */
private const val unselectedAlpha = 160

/**
 * Handles communicates between the ASBPieceContainer and its user.
 * Implement this to receive communications that should be handled by the parent fragment
 */
interface ASBPieceContainerListener {
    fun onChangeWeaponSlots()
    fun onChangeArmor(pieceIndex: Int)
    fun onChangeTalisman()
}

/**
 * Custom view used to display a single armor piece for the ASB.
 * Displays the armor piece and all associated slots.
 * It is required to call `initialize` after instantiating this class.
 */
class ASBPieceContainer(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var parentFragment: ASBFragment? = null
    private lateinit var listener: ASBPieceContainerListener

    private val equipmentHeader: View
    private val icon: ImageView
    private val equipmentNameView: TextView

    private val equipmentSlots: SlotsView
    private val equipmentButton: ImageView

    private val decorationHeader: View
    private val dropDownArrow: ImageView
    private val decorationSectionView: DecorationSectionView

    private lateinit var session: ASBSession
    private var pieceIndex: Int = 0

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_asb_piece_container, this)

        equipmentHeader = findViewById(R.id.equipment_header)
        icon = findViewById(R.id.equipment_icon)
        equipmentNameView = findViewById(R.id.equipment_name)
        equipmentSlots = findViewById(R.id.equipment_slots)
        equipmentButton = findViewById(R.id.add_equipment_button)
        decorationHeader = findViewById(R.id.decoration_header)
        dropDownArrow = findViewById(R.id.drop_down_arrow)

        decorationSectionView = DecorationSectionView()
    }

    /**
     * Provides necessary external initialization logic.
     * Should always be called after the container's constructor.
     */
    fun initialize(session: ASBSession, pieceIndex: Int, parentFragment: ASBFragment, listener: ASBPieceContainerListener) {
        this.session = session
        this.pieceIndex = pieceIndex
        this.parentFragment = parentFragment
        this.listener = listener

        equipmentHeader.setOnClickListener {
            // If empty or is weapon/talisman, trigger the normal add routine
            val equipment = session.getEquipment(pieceIndex)
            if (pieceIndex == ArmorSet.WEAPON || pieceIndex == ArmorSet.TALISMAN || equipment == null) {
                onAddEquipment()
                return@setOnClickListener
            }

            // navigate to armor page
            ArmorClickListener(context, equipment.id, false).onClick(it)
        }

        equipmentButton.setOnClickListener {
            if (session.getEquipment(pieceIndex) == null) {
                onAddEquipment()
            } else {
                onRemoveEquipment()
            }
        }

        decorationHeader.setOnClickListener {
            toggleDecorations()
        }

        // Reflect current state
        updateContents()
    }

    /**
     * Refreshes the contents of the piece container based on the `ASBSession`.
     */
    fun updateContents() {
        updateEquipmentView()
        updateSlotsHeader()
        decorationSectionView.update()

        // Ensure that decorations are closed and cannot be opened if unselected
        val equipmentSelected = session.getEquipment(pieceIndex) != null
        decorationHeader.isEnabled = equipmentSelected
        if (!equipmentSelected) {
            hideDecorations()
        }
    }

    /**
     * Internal helper to update the displayed armor piece based on the session selected equipment
     */
    private fun updateEquipmentView() {
        val selectedEquipment = session.getEquipment(pieceIndex)

        // set text color to default
        equipmentNameView.setTextColor(context.getColorCompat(R.color.text_color))

        // set text (and maybe text color) based on equipment
        if (pieceIndex == ArmorSet.WEAPON) {
            // If weapon
            equipmentNameView.text = context.getString(when(session.numWeaponSlots) {
                1 -> R.string.asb_weapon_slots_one
                2 -> R.string.asb_weapon_slots_two
                3 -> R.string.asb_weapon_slots_three
                else -> R.string.asb_weapon_slots_none
            })
        } else if (selectedEquipment == null) {
            // If unequipped
            equipmentNameView.text = context.getString(R.string.asb_none)
            equipmentNameView.setTextColor(context.getColorCompat(R.color.text_color_secondary))
        } else if (pieceIndex == ArmorSet.TALISMAN) {
            // If talisman
            val talisman = selectedEquipment as? ASBTalisman
            if (talisman != null) {
                val values = talisman.skills.map {
                    context.getString(R.string.skill_points, it.skillTree.name, it.points)
                }
                equipmentNameView.text = when (values.size) {
                    0 -> null
                    1 -> values.first()
                    else -> context.getString(R.string.skill_points_joined, values[0], values[1])
                }
            } else {
                equipmentNameView.text = null
            }
        } else {
            // Anything else (usually armor pieces
            equipmentNameView.text = selectedEquipment.name
        }

        // Set image based on equipment
        if (selectedEquipment != null) {
            icon.setImageAsset(selectedEquipment)
        } else {
            // Since no equipment is selected, load the "empty image"
            val resId = when(pieceIndex) {
                ArmorSet.HEAD -> R.drawable.armor_head
                ArmorSet.BODY -> R.drawable.armor_body
                ArmorSet.ARMS -> R.drawable.armor_arms
                ArmorSet.WAIST -> R.drawable.armor_waist
                ArmorSet.LEGS -> R.drawable.armor_legs
                ArmorSet.TALISMAN -> R.drawable.talisman
                else -> 0
            }

            if (resId == 0) {
                icon.setImageDrawable(null)
            } else {
                val image = ContextCompat.getDrawable(context, resId)?.mutate()
                image?.alpha = unselectedAlpha
                icon.setImageDrawable(image)
            }

        }

        // set the add/remove button based on equipment
        equipmentButton.setImageResource(when (selectedEquipment != null) {
            true -> R.drawable.ic_remove
            false -> R.drawable.ic_add
        })
    }

    /**
     * Update the slots header to reflect the selected equipment
     */
    private fun updateSlotsHeader() {
        val equipment = session.getEquipment(pieceIndex)
        val numSlots = equipment?.numSlots ?: 0
        val usedSlots = numSlots - session.getAvailableSlots(pieceIndex)
        equipmentSlots.setSlots(numSlots, usedSlots)
    }

    /**
     * Toggles the visibility of the decorations drawer
     */
    fun toggleDecorations() {
        if (decorationSectionView.container.visibility == View.GONE) {
            showDecorations()
        } else {
            hideDecorations()
        }
    }

    /**
     * Opens the decoration drawer
     */
    fun showDecorations() {
        parentFragment?.onDecorationsMenuOpened()
        decorationSectionView.container.visibility = View.VISIBLE
        dropDownArrow.setImageResource(R.drawable.ic_drop_up_arrow)
    }

    /**
     * Closes the decoration drawer
     */
    fun hideDecorations() {
        decorationSectionView.container.visibility = View.GONE
        dropDownArrow.setImageResource(R.drawable.ic_drop_down_arrow)
    }

    /**
     * Function that handles a user's attempt to add new equipment
     */
    private fun onAddEquipment() {
        if (pieceIndex == ArmorSet.WEAPON) {
            listener.onChangeWeaponSlots()
        } else if (pieceIndex == ArmorSet.TALISMAN) {
            listener.onChangeTalisman()
        } else {
            listener.onChangeArmor(pieceIndex)
        }
    }

    /**
     * Function that handles a user's attempt to remove equipment
     */
    private fun onRemoveEquipment() {
        val data = Intent()
        data.putExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
        parentFragment!!.onActivityResult(ASBDetailPagerActivity.REQUEST_CODE_REMOVE_PIECE, Activity.RESULT_OK, data)
    }

    /**
     * Internal class that contains view elements for a single row of the DecorationSectionView
     */
    private inner class DecorationLineViewHolder(val container: View) {
        val decorationHeader = container.findViewById<View>(R.id.decoration_header)
        val iconView = container.findViewById<ImageView>(R.id.decoration_icon)
        val nameView = container.findViewById<TextView>(R.id.decoration_name)
        val slotsView = container.findViewById<SlotsView>(R.id.decoration_slots)
        val buttonView = container.findViewById<ImageView>(R.id.decoration_menu)

        var onRemove: ((Decoration) -> Unit)? = null
        var onSelect: ((Decoration) -> Unit)? = null

        /**
         * Sets the view to a certain decoration, and makes the view visible
         */
        fun setDecoration(decoration: Decoration) {
            iconView.setImageAsset(decoration)
            nameView.text = decoration.name
            slotsView.setSlots(decoration.numSlots, decoration.numSlots)
            container.visibility = View.VISIBLE

            decorationHeader.setOnClickListener { onSelect?.invoke(decoration) }
            buttonView.setOnClickListener { onRemove?.invoke(decoration) }
        }

        /**
         * Clears the line and hides it from view
         */
        fun clear() {
            nameView.text = null
            iconView.setImageDrawable(null)
            container.visibility = View.GONE
        }
    }

    /**
     * Internal class that manages the view containing the list of decorations
     */
    private inner class DecorationSectionView {
        internal val container = findViewById<ViewGroup>(R.id.decorations)
        internal val blankSlate = findViewById<View>(R.id.decoration_blank_slate)
        internal val decorationViews = listOf(
                DecorationLineViewHolder(findViewById(R.id.decoration_1_item)),
                DecorationLineViewHolder(findViewById(R.id.decoration_2_item)),
                DecorationLineViewHolder(findViewById(R.id.decoration_3_item))
        )
        internal val decorationAdd = findViewById<View>(R.id.add_decoration)
        internal val availableSlotsView = findViewById<SlotsView>(R.id.slots_available)

        init {
            decorationAdd.setOnClickListener {
                requestAddDecoration()
            }

            for (i in 0..2) {
                val vh = decorationViews[i]
                vh.onSelect = { decoration -> requestDecorationInfo(decoration) }
                vh.onRemove = { requestRemoveDecoration(i) }
            }
        }

        fun update() {
            val equipment = session.getEquipment(pieceIndex)

            // If null or no slots, set all to blank and enable the blank slate
            if (equipment == null || equipment.numSlots == 0) {
                decorationViews.forEach { it.clear() }
                blankSlate.visibility = View.VISIBLE
                updateAvailableSlots(0)
                return
            }

            // Slots are possible, so hide the "No Slots" message
            blankSlate.visibility = View.GONE

            // Store a queue of all decoration lines.
            // We iterate over decorations, and for each one we pop a view to apply it on.
            // This will give better live performance over inflating.
            val decorationViewQueue = ArrayDeque(decorationViews)

            // Bind all decorations
            for (decoration in session.getDecorations(pieceIndex)) {
                val view = decorationViewQueue.pop()
                view.setDecoration(decoration)
            }

            // If slots are available, show an "add slot" line
            val availableSlots = session.getAvailableSlots(pieceIndex)
            updateAvailableSlots(availableSlots)

            // Clear any remaining views
            decorationViewQueue.forEach { it.clear() }
        }

        /**
         * Updates the view to show the add decoration view (if non-zero), holding a number of available slots.
         * Hides the view if there aren't any
         */
        private fun updateAvailableSlots(available: Int) {
            if (available > 0) {
                decorationAdd.visibility = View.VISIBLE
                availableSlotsView.setSlots(available, 0)
            } else {
                decorationAdd.visibility = View.GONE
            }
        }

        /**
         * Sends a request to add a decoration at a given index, which is sent back to the parent fragment.
         */
        private fun requestAddDecoration() {
            val i = Intent(parentFragment!!.activity, DecorationListActivity::class.java)
            i.putExtra(ASBDetailPagerActivity.EXTRA_FROM_SET_BUILDER, true)
            i.putExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
            i.putExtra(ASBDetailPagerActivity.EXTRA_DECORATION_MAX_SLOTS, session.getAvailableSlots(pieceIndex))

            parentFragment!!.startActivityForResult(i, ASBDetailPagerActivity.REQUEST_CODE_ADD_DECORATION)
        }

        /**
         * Lets the parent fragment know to remove the decoration at a given index
         */
        private fun requestRemoveDecoration(decorationIndex: Int) {
            val data = Intent()
            data.putExtra(ASBDetailPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
            data.putExtra(ASBDetailPagerActivity.EXTRA_DECORATION_INDEX, decorationIndex)

            parentFragment!!.onActivityResult(ASBDetailPagerActivity.REQUEST_CODE_REMOVE_DECORATION, Activity.RESULT_OK, data)
        }

        /**
         * Navigate to decoration page.
         * TODO: Replace reference with navigator of some sort
         */
        private fun requestDecorationInfo(decoration: Decoration) {
            val i = Intent(parentFragment!!.activity, DecorationDetailActivity::class.java)
            i.putExtra(DecorationDetailActivity.EXTRA_DECORATION_ID, decoration.id)
            parentFragment!!.startActivity(i)
        }
    }
}
