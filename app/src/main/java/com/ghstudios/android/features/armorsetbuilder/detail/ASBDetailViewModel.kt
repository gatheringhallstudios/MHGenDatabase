package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.data.classes.ASBTalisman
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ArmorSet
import com.ghstudios.android.mhgendatabase.R

/**
 * Defines a model for the ASB.
 * Sessions are synchronous, so this entire class works synchronously
 * outside of update events.
 */
class ASBDetailViewModel(val app: Application) : AndroidViewModel(app) {
    private val TAG = javaClass.simpleName

    private val dataManager = DataManager.get()
    private val asbManager = dataManager.asbManager

    private var sessionId = -1L
    lateinit var session: ASBSession

    private val updatePieceEventInner = MutableLiveData<Int>()

    val updatePieceEvent: LiveData<Int> get() = updatePieceEventInner

    fun loadSession(sessionId: Long) {
        if (this.sessionId == sessionId) {
            return
        }

        this.sessionId = sessionId
        session = asbManager.getASBSession(sessionId)!! // note: error should never happen
    }

    /**
     * Updates the internal session weapon slot count,
     * then persists that change to the DB.
     */
    fun setWeaponSlots(slots: Int) {
        session.numWeaponSlots = slots
        triggerPieceUpdated(ArmorSet.WEAPON)
    }

    /**
     * Adds the armor to the internal session,
     * then persists that change to the DB.
     */
    fun addArmor(armorId: Long) {
        val armor = dataManager.getArmor(armorId)

        val armorEnum = when (armor?.slot) {
            Armor.ARMOR_SLOT_HEAD -> ArmorSet.HEAD
            Armor.ARMOR_SLOT_BODY -> ArmorSet.BODY
            Armor.ARMOR_SLOT_ARMS -> ArmorSet.ARMS
            Armor.ARMOR_SLOT_WAIST -> ArmorSet.WAIST
            Armor.ARMOR_SLOT_LEGS -> ArmorSet.LEGS
            else -> null
        }

        if (armor != null && armorEnum != null) {
            session.setEquipment(armorEnum, armor)
            triggerPieceUpdated(armorEnum)
        }
    }

    /**
     * Removes the armor from the specified piece index,
     * then persists that change to the DB.
     */
    fun removeArmorPiece(pieceIndex: Int) {
        session.removeEquipment(pieceIndex)
        triggerPieceUpdated(pieceIndex)
    }

    /**
     * Adds a decoration to the given slot, then updates the DB.
     */
    fun bindDecoration(pieceIndex: Int, decorationId: Long) {
        val decoration = dataManager.getDecoration(decorationId)
        if (decoration == null) {
            Log.e(TAG, "Unexpected Decoration Null $decorationId")
            return
        }

        val decorationIndex = session.addDecoration(pieceIndex, decoration)
        if (decorationIndex != -1) {
            triggerPieceUpdated(pieceIndex)
        }
    }

    /**
     * Removes a decoration from the specified slot, then updates the DB.
     */
    fun unbindDecoration(pieceIndex: Int, decorationIndex: Int) {
        session.removeDecoration(pieceIndex, decorationIndex)
        triggerPieceUpdated(pieceIndex)
    }

    /**
     * Sets the equipped talisman, then updates the DB.
     */
    fun setTalisman(
            typeIndex: Int,
            skill1Id: Long,
            skill1Points: Int,
            skill2Id: Long,
            skill2Points: Int,
            numSlots: Int) {
        val skill1Tree = dataManager.getSkillTree(skill1Id)

        // todo: consider an alternative talisman object
        // todo: find better way of loading talismans
        val talisman = ASBTalisman(typeIndex)
        val typeName = app.resources.getStringArray(R.array.talisman_names)[typeIndex]
        talisman.name = app.getString(R.string.talisman_full_name, typeName)
        talisman.numSlots = numSlots

        if (skill1Tree != null) {
            talisman.setFirstSkill(skill1Tree, skill1Points)
        }

        if (skill2Id >= 0) {
            val skill2Tree = dataManager.getSkillTree(skill2Id)
            talisman.setSecondSkill(skill2Tree, skill2Points)
        }

        session.setEquipment(ArmorSet.TALISMAN, talisman)
        triggerPieceUpdated(ArmorSet.TALISMAN)
    }

    /**
     * Triggers an update event related to a piece.
     * This updates the ASBSession in the database as well.
     * Note: ASB constants are equal to the piece index, can use either
     */
    private fun triggerPieceUpdated(pieceIndex: Int) {
        asbManager.updateASB(session)
        updatePieceEventInner.postValue(pieceIndex)
    }
}