package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
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
import com.ghstudios.android.data.classes.Decoration
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity
import com.ghstudios.android.features.armorsetbuilder.armorselect.ArmorSelectActivity
import com.ghstudios.android.features.decorations.list.DecorationListActivity
import com.ghstudios.android.util.setImageAsset
import java.util.*

/**
 * Image alpha value for unselected items
 */
private const val unselectedAlpha = 160

/**
 * Custom view used to display a single armor piece for the ASB.
 * Displays the armor piece and all associated slots.
 */
class ASBPieceContainer
/**
 * It is required to call `initialize` after instantiating this class.
 */
(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var parentFragment: ASBFragment? = null

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
        icon = findViewById(R.id.armor_builder_item_icon)
        equipmentNameView = findViewById(R.id.armor_builder_item_name)
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
    fun initialize(session: ASBSession, pieceIndex: Int, parentFragment: ASBFragment) {
        this.session = session
        this.pieceIndex = pieceIndex
        this.parentFragment = parentFragment

        equipmentHeader.setOnClickListener {
            // If empty or is talisman, trigger the normal add routine
            val equipment = session.getEquipment(pieceIndex)
            if (pieceIndex == ASBSession.TALISMAN || equipment == null) {
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
        updateArmorPiece()
        updateDecorationsPreview()
        decorationSectionView.update()
    }

    /**
     * Internal helper to update the displayed armor piece based on the session selected equipment
     */
    private fun updateArmorPiece() {
        val selectedEquipment = session.getEquipment(pieceIndex)
        equipmentNameView.text = selectedEquipment?.name

        // Set image based on equipment
        if (selectedEquipment != null) {
            icon.setImageAsset(selectedEquipment)
        } else {
            // Since no equipment is selected, load the "empty image"
            val resId = when(pieceIndex) {
                ASBSession.HEAD -> R.drawable.armor_head
                ASBSession.BODY -> R.drawable.armor_body
                ASBSession.ARMS -> R.drawable.armor_arms
                ASBSession.WAIST -> R.drawable.armor_waist
                ASBSession.LEGS -> R.drawable.armor_legs
                ASBSession.TALISMAN -> R.drawable.talisman
                else -> 0
            }

            val image = ContextCompat.getDrawable(context, resId)?.mutate()
            image?.alpha = unselectedAlpha
            icon.setImageDrawable(image)
        }

        // set the add/remove button based on equipment
        equipmentButton.setImageResource(when (selectedEquipment != null) {
            true -> R.drawable.ic_remove
            false -> R.drawable.ic_add
        })
    }

    private fun updateDecorationsPreview() {
        val equipment = session.getEquipment(pieceIndex)

        val numSlots = equipment?.numSlots ?: 0
        val usedSlots = numSlots - session.getAvailableSlots(pieceIndex)
        equipmentSlots.setSlots(numSlots, usedSlots)
    }

    fun toggleDecorations() {
        if (decorationSectionView.container.visibility == View.GONE) {
            showDecorations()
        } else {
            hideDecorations()
        }
    }

    fun showDecorations() {
        parentFragment!!.onDecorationsMenuOpened()
        decorationSectionView.container.visibility = View.VISIBLE
        equipmentButton.visibility = View.INVISIBLE
        dropDownArrow.setImageDrawable(parentFragment!!.activity!!.resources.getDrawable(R.drawable.ic_drop_up_arrow))
    }

    fun hideDecorations() {
        decorationSectionView.container.visibility = View.GONE
        equipmentButton.visibility = View.VISIBLE
        dropDownArrow.setImageDrawable(parentFragment!!.activity!!.resources.getDrawable(R.drawable.ic_drop_down_arrow))
    }

    /**
     * Function that handles a user's attempt to add new equipment
     */
    private fun onAddEquipment() {
        if (pieceIndex == ASBSession.TALISMAN) {
            val d = ASBTalismanDialogFragment.newInstance()
            d.setTargetFragment(parentFragment, ASBPagerActivity.REQUEST_CODE_CREATE_TALISMAN)
            d.show(parentFragment!!.fragmentManager, "TALISMAN")
        } else {
            val i = Intent(context, ArmorSelectActivity::class.java)
            i.putExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, true)
            i.putExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
            i.putExtra(ASBPagerActivity.EXTRA_SET_RANK, session.rank)
            i.putExtra(ASBPagerActivity.EXTRA_SET_HUNTER_TYPE, session.hunterType)

            parentFragment!!.startActivityForResult(i, ASBPagerActivity.REQUEST_CODE_ADD_PIECE)
        }
    }

    /**
     * Function that handles a user's attempt to remove equipment
     */
    private fun onRemoveEquipment() {
        val data = Intent()
        data.putExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
        parentFragment!!.onActivityResult(ASBPagerActivity.REQUEST_CODE_REMOVE_PIECE, Activity.RESULT_OK, data)
    }

    /**
     * Internal class that contains view elements for a single row of the DecorationSectionView
     */
    private inner class DecorationLineViewHolder(val container: View) {
        val iconView = container.findViewById<ImageView>(R.id.decoration_icon)
        val nameView = container.findViewById<TextView>(R.id.decoration_name)
        val buttonView = container.findViewById<ImageView>(R.id.decoration_menu)

        /**
         * Sets the view to a certain decoration, and makes the view visible
         */
        fun setDecoration(decoration: Decoration) {
            iconView.setImageAsset(decoration)
            nameView.text = decoration.name
            nameView.setTextColor(resources.getColor(R.color.text_color))
            buttonView.setImageResource(R.drawable.ic_remove)
            container.visibility = View.VISIBLE
        }

        fun showEmpty(slotsRemaining: Int) {
            iconView.setImageDrawable(null)
            nameView.setText(R.string.asb_empty_slot)
            buttonView.setImageResource(R.drawable.ic_add)
            container.visibility = View.VISIBLE
        }

        /**
         * Clears the line and hides it from view
         */
        fun clear() {
            nameView.text = null
            iconView.setImageDrawable(null)
            buttonView.setImageDrawable(null)
            container.visibility = View.GONE
        }
    }

    /**
     * Internal class that manages the view containing the list of decorations
     */
    private inner class DecorationSectionView {
        internal var decorationViews: List<DecorationLineViewHolder>
        internal var container: ViewGroup

        init {
            container = findViewById(R.id.decorations)

            decorationViews = listOf(
                    DecorationLineViewHolder(findViewById(R.id.decoration_1_item)),
                    DecorationLineViewHolder(findViewById(R.id.decoration_2_item)),
                    DecorationLineViewHolder(findViewById(R.id.decoration_3_item))
            )

            for (i in 0..2) {
                val vh = decorationViews[i]
                vh.nameView.setOnClickListener {
                    val decoration = session.getDecoration(pieceIndex, i)
                    if (decoration != null) {
                        requestDecorationInfo(decoration)
                    }
                }

                vh.buttonView.setOnClickListener {
                    val decoration = session.getDecoration(pieceIndex, i)
                    if (decoration != null) {
                        requestRemoveDecoration(i)
                    } else {
                        requestAddDecoration()
                    }
                }
            }
        }

        fun update() {
            val equipment = session.getEquipment(pieceIndex)

            // If null, set all to blank
            if (equipment == null) {
                decorationViews.forEach { it.clear() }
                return
            }

            // Store a queue of all decoration lines.
            // We iterate over decorations, and pop a view to apply it on.
            // This will give better live performance over inflating.
            val decorationViewQueue = ArrayDeque(decorationViews)

            // Bind all decorations
            for (decoration in session.getDecorations(pieceIndex)) {
                val view = decorationViewQueue.pop()
                view.setDecoration(decoration)
            }

            // If slots are available, show an "add slot" line
            val availableSlots = session.getAvailableSlots(pieceIndex)
            if (availableSlots > 0) {
                val view = decorationViewQueue.pop()
                view.showEmpty(slotsRemaining = availableSlots)
            }

            // Clear any remaining views
            for (view in decorationViewQueue) {
                view.clear()
            }
        }

        private fun requestAddDecoration() {
            val i = Intent(parentFragment!!.activity, DecorationListActivity::class.java)
            i.putExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, true)
            i.putExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
            i.putExtra(ASBPagerActivity.EXTRA_DECORATION_MAX_SLOTS, session.getAvailableSlots(pieceIndex))

            parentFragment!!.startActivityForResult(i, ASBPagerActivity.REQUEST_CODE_ADD_DECORATION)
        }

        private fun requestRemoveDecoration(decorationIndex: Int) {
            val data = Intent()
            data.putExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, pieceIndex)
            data.putExtra(ASBPagerActivity.EXTRA_DECORATION_INDEX, decorationIndex)

            parentFragment!!.onActivityResult(ASBPagerActivity.REQUEST_CODE_REMOVE_DECORATION, Activity.RESULT_OK, data)
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
