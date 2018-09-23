package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.ghstudios.android.features.armor.detail.ArmorSetDetailPagerActivity
import com.ghstudios.android.features.decorations.detail.DecorationDetailActivity
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.armor.list.ArmorListPagerActivity

/**
 * This is where the magic happens baby. Users can define a custom armor set in this fragment.
 */
class ASBFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ASBDetailViewModel::class.java)
    }

    private lateinit var equipmentViews: List<ASBPieceContainer>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_asb, container, false)

        equipmentViews = listOf(
                view.findViewById(R.id.armor_builder_weapon),
                view.findViewById(R.id.armor_builder_head),
                view.findViewById(R.id.armor_builder_body),
                view.findViewById(R.id.armor_builder_arms),
                view.findViewById(R.id.armor_builder_waist),
                view.findViewById(R.id.armor_builder_legs),
                view.findViewById(R.id.armor_builder_talisman))

        for ((idx, equipView) in equipmentViews.withIndex()) {
            equipView.initialize(viewModel.session, idx, this)
        }

        viewModel.updatePieceEvent.observe(this, Observer {
            if (it != null) {
                equipmentViews.getOrNull(it)?.updateContents()
            }
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data == null)
            return

        if (resultCode == Activity.RESULT_OK) { // If the user canceled the request, we don't want to do anything.
            when (requestCode) {
                ASBPagerActivity.REQUEST_CODE_ADD_PIECE -> {
                    val armorId = data.getLongExtra(ArmorSetDetailPagerActivity.EXTRA_ARMOR_ID, -1)
                    viewModel.addArmor(armorId)
                }

                ASBPagerActivity.REQUEST_CODE_REMOVE_PIECE -> {
                    val pieceIndex = data.getIntExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, -1)
                    viewModel.removeArmorPiece(pieceIndex)
                }

                ASBPagerActivity.REQUEST_CODE_ADD_DECORATION -> {
                    val decorationId = data.getLongExtra(DecorationDetailActivity.EXTRA_DECORATION_ID, -1)
                    val pieceIndex = data.getIntExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, -1)
                    viewModel.bindDecoration(pieceIndex, decorationId)
                }

                ASBPagerActivity.REQUEST_CODE_REMOVE_DECORATION -> {
                    val pieceIndex = data.getIntExtra(ASBPagerActivity.EXTRA_PIECE_INDEX, -1)
                    val decorationIndex = data.getIntExtra(ASBPagerActivity.EXTRA_DECORATION_INDEX, -1)
                    viewModel.unbindDecoration(pieceIndex, decorationIndex)
                }

                ASBPagerActivity.REQUEST_CODE_CREATE_TALISMAN -> {
                    //if (data.hasExtra(ASBPagerActivity.EXTRA_TALISMAN_SKILL_TREE_2)) {

                    viewModel.setTalisman(
                            typeIndex = data.getIntExtra(ASBPagerActivity.EXTRA_TALISMAN_TYPE_INDEX, -1),
                            numSlots = data.getIntExtra(ASBPagerActivity.EXTRA_TALISMAN_SLOTS, 0),
                            skill1Id = data.getLongExtra(ASBPagerActivity.EXTRA_TALISMAN_SKILL_TREE_1, -1),
                            skill1Points = data.getIntExtra(ASBPagerActivity.EXTRA_TALISMAN_SKILL_POINTS_1, -1),

                            skill2Id = data.getLongExtra(ASBPagerActivity.EXTRA_TALISMAN_SKILL_TREE_2, -1),
                            skill2Points = data.getIntExtra(ASBPagerActivity.EXTRA_TALISMAN_SKILL_POINTS_2, 0)
                    )
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_asb, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            // The user wants to add an armor piece
            R.id.set_builder_add_piece -> {
                val intent = Intent(activity, ArmorListPagerActivity::class.java)
                intent.putExtra(ASBPagerActivity.EXTRA_FROM_SET_BUILDER, true)

                startActivityForResult(intent, ASBPagerActivity.REQUEST_CODE_ADD_PIECE)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /** Called when the user clicks the drop-down arrow on an equipment view.  */
    fun onDecorationsMenuOpened() {
        equipmentViews.forEach { it.hideDecorations() }
    }
}