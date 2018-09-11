package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.ArmorClickListener
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity
import com.ghstudios.android.features.armorsetbuilder.armorselect.ArmorSelectActivity
import com.ghstudios.android.features.decorations.list.DecorationListActivity
import com.ghstudios.android.util.setImageAsset

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

    private val decorationStates: List<ImageView>
    private val equipmentButton: ImageView

    private val decorationHeader: View
    private val dropDownArrow: ImageView
    private val decorationView: DecorationView

    private lateinit var session: ASBSession
    private var pieceIndex: Int = 0

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_asb_piece_container, this)

        equipmentHeader = findViewById(R.id.equipment_header)
        icon = findViewById(R.id.armor_builder_item_icon)
        equipmentNameView = findViewById(R.id.armor_builder_item_name)
        equipmentButton = findViewById(R.id.add_equipment_button)
        decorationHeader = findViewById(R.id.decoration_header)
        dropDownArrow = findViewById(R.id.drop_down_arrow)

        decorationStates = listOf(
                findViewById(R.id.decoration_1_state),
                findViewById(R.id.decoration_2_state),
                findViewById(R.id.decoration_3_state)
        )

        decorationView = DecorationView()
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
        decorationView.update()
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
        for (i in 0..2) {
            if (equipment == null) {
                decorationStates[i].setImageResource(R.drawable.decoration_none)
            } else if (session.decorationIsReal(pieceIndex, i)) {
                decorationStates[i].setImageResource(R.drawable.decoration_real)
            } else if (session.decorationIsDummy(pieceIndex, i)) {
                decorationStates[i].setImageResource(R.drawable.decoration_real)
            } else if (equipment.numSlots > i) {
                decorationStates[i].setImageResource(R.drawable.decoration_empty)
            } else {
                decorationStates[i].setImageResource(R.drawable.decoration_none)
            }
        }
    }

    fun toggleDecorations() {
        if (decorationView.container.visibility == View.GONE) {
            showDecorations()
        } else {
            hideDecorations()
        }
    }

    fun showDecorations() {
        parentFragment!!.onDecorationsMenuOpened()
        decorationView.container.visibility = View.VISIBLE
        equipmentButton.visibility = View.INVISIBLE
        dropDownArrow.setImageDrawable(parentFragment!!.activity!!.resources.getDrawable(R.drawable.ic_drop_up_arrow))
    }

    fun hideDecorations() {
        decorationView.container.visibility = View.GONE
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
     * Internal class that contains view elements for a single row of the DecorationView
     */
    private inner class DecorationLineViewHolder(val container: View) {
        val iconView = container.findViewById<ImageView>(R.id.decoration_icon)
        val nameView = container.findViewById<TextView>(R.id.decoration_name)
        val buttonView = container.findViewById<ImageView>(R.id.decoration_menu)

        fun clear() {
            nameView.text = null
            iconView.setImageDrawable(null)
            buttonView.setImageDrawable(null)
        }
    }

    /**
     * Internal class that manages the view containing the list of decorations
     */
    private inner class DecorationView {
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
                vh.nameView.setOnClickListener { v ->
                    if (session.decorationIsReal(pieceIndex, i)) {
                        requestDecorationInfo(i)
                    }
                }

                vh.buttonView.setOnClickListener { v ->
                    if (session.decorationIsReal(pieceIndex, i)) {
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

            // equipment is not null
            var addButtonExists = false
            for (i in decorationViews.indices) {
                val vh = decorationViews[i]

                fetchDecorationIcon(vh.iconView, pieceIndex, i)

                if (session.decorationIsReal(pieceIndex, i)) {
                    vh.nameView.text = session.getDecoration(pieceIndex, i)!!.name
                    vh.nameView.setTextColor(resources.getColor(R.color.text_color))
                    vh.buttonView.setImageDrawable(resources.getDrawable(R.drawable.ic_remove))
                } else {
                    if (session.decorationIsDummy(pieceIndex, i)) {
                        vh.nameView.text = session.findRealDecorationOfDummy(pieceIndex, i).name
                        vh.buttonView.setImageDrawable(null)
                    } else if (equipment.numSlots > i) {
                        vh.nameView.setText(R.string.asb_empty_slot)

                        if (!addButtonExists) {
                            vh.buttonView.setImageDrawable(resources.getDrawable(R.drawable.ic_add))
                            addButtonExists = true
                        } else {
                            vh.buttonView.setImageDrawable(null)
                        }
                    } else {
                        vh.nameView.setText(R.string.asb_no_slot)
                        vh.buttonView.setImageDrawable(null)
                    }

                    vh.nameView.setTextColor(resources.getColor(R.color.text_color_secondary))
                }
            }
        }

        private fun fetchDecorationIcon(iv: ImageView, pieceIndex: Int, decorationIndex: Int) {
            if (session.decorationIsReal(pieceIndex, decorationIndex)) {
                AssetLoader.setIcon(iv, session.getDecoration(pieceIndex, decorationIndex)!!)
            } else if (session.decorationIsDummy(pieceIndex, decorationIndex)) {
                iv.setImageResource(R.drawable.icon_jewel)
                iv.setColorFilter(0xFFFFFF, PorterDuff.Mode.MULTIPLY)
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

        private fun requestDecorationInfo(decorationIndex: Int) {
            val i = Intent(parentFragment!!.activity, DecorationDetailActivity::class.java)

            i.putExtra(DecorationDetailActivity.EXTRA_DECORATION_ID,
                    session.getDecoration(pieceIndex, decorationIndex)?.id)

            parentFragment!!.startActivity(i)
        }
    }
}
