package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.MHUtils

/**
 * Internal class to describe a talisman.
 * TODO: Set this up to work deeper than just this viewmodel
 */
class Talisman(
        val typeIndex: Int,
        val skill1Id: Long,
        val skill1Points: Int,
        val skill2Id: Long,
        val skill2Points: Int,
        val numSlots: Int
)

/**
 * Defines a model for the ASB.
 * Sessions are synchronous, so this entire class works synchronously
 * outside of update events.
 */
class ASBDetailViewModel(val app: Application) : AndroidViewModel(app) {
    private val TAG = javaClass.simpleName

    private val dataManager = DataManager.get()

    private var sessionId = -1L
    lateinit var session: ASBSession

    private val updatePieceEventInner = MutableLiveData<Int>()

    val updatePieceEvent: LiveData<Int> get() = updatePieceEventInner

    fun loadSession(sessionId: Long) {
        if (this.sessionId == sessionId) {
            return
        }

        this.sessionId = sessionId
        session = dataManager.getASBSession(sessionId)!! // note: error should never happen
    }

    fun addArmor(armorId: Long) {
        val armor = dataManager.getArmor(armorId)

        val armorEnum = when (armor?.slot) {
            "Head" -> ASBSession.HEAD
            "Body" -> ASBSession.BODY
            "Arms" -> ASBSession.ARMS
            "Waist" -> ASBSession.WAIST
            "Legs" -> ASBSession.LEGS
            else -> null
        }

        if (armor != null && armorEnum != null) {
            session.setEquipment(armorEnum, armor)
            dataManager.queryPutASBSessionArmor(session.id, armorId, armorEnum)

            triggerPieceUpdated(armorEnum)
        }
    }

    fun removeArmorPiece(pieceIndex: Int) {
        session.removeEquipment(pieceIndex)

        if (pieceIndex == ASBSession.TALISMAN) {
            dataManager.queryRemoveASBSessionTalisman(session.id)
        } else {
            dataManager.queryRemoveASBSessionArmor(session.id, pieceIndex)
        }

        triggerPieceUpdated(pieceIndex)
    }

    fun bindDecoration(pieceIndex: Int, decorationId: Long) {
        val decoration = dataManager.getDecoration(decorationId)
        if (decoration == null) {
            Log.e(TAG, "Unexpected Decoration Null $decorationId")
            return
        }

        val decorationIndex = session.addDecoration(pieceIndex, decoration)

        if (decorationIndex != -1 && pieceIndex != -1) {
            dataManager.queryPutASBSessionDecoration(session.id, decorationId, pieceIndex, decorationIndex)
        }

        triggerPieceUpdated(pieceIndex)
    }

    fun unbindDecoration(pieceIndex: Int, decorationIndex: Int) {
        session.removeDecoration(pieceIndex, decorationIndex)
        dataManager.queryRemoveASBSessionDecoration(session.id, pieceIndex, decorationIndex)

        triggerPieceUpdated(pieceIndex)
    }

    fun setTalisman(data: Talisman) {
        val skill1Tree = dataManager.getSkillTree(data.skill1Id)

        // todo: consider an alternative talisman object
        // todo: find better way of loading talismans
        val talisman = ASBTalisman(data.typeIndex)
        val typeName = app.resources.getStringArray(R.array.talisman_names)[data.typeIndex]
        talisman.name = app.getString(R.string.talisman_full_name, typeName)
        talisman.numSlots = data.numSlots

        if (skill1Tree != null) {
            talisman.setFirstSkill(skill1Tree, data.skill1Points)
        }

        if (data.skill2Id >= 0) {
            val skill2Tree = dataManager.getSkillTree(data.skill2Id)
            talisman.setSecondSkill(skill2Tree, data.skill2Points)
        }

        dataManager.queryCreateASBSessionTalisman(
                session.id,
                data.typeIndex,
                data.numSlots,
                data.skill1Id,
                data.skill1Points,
                data.skill2Id,
                data.skill2Points)

        session.setEquipment(ASBSession.TALISMAN, talisman)

        triggerPieceUpdated(ASBSession.TALISMAN)
    }

    /**
     * Triggers an update event related to a piece.
     * Note: ASB constants are equal to the piece index, can use either
     */
    private fun triggerPieceUpdated(pieceIndex: Int) {
        // if this ever gets called from another thread, do a check and call postValue
        updatePieceEventInner.postValue(pieceIndex)
    }
}