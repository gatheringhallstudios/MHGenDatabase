package com.ghstudios.android.features.armorsetbuilder.detail

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.features.armorsetbuilder.talismans.TalismanMetadata
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.loggedThread

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

    val session: ASBSession
        get() = sessionData.value!!

    val sessionData = MutableLiveData<ASBSession>()

    val updatePieceEvent = MutableLiveData<Int>()

    /**
     * Sets the session id and starts the initial load.
     */
    fun loadSession(sessionId: Long) {
        if (this.sessionId == sessionId) {
            return
        }

        this.sessionId = sessionId
        reload()
    }

    /**
     * Reloads the session in place. Use after external updates.
     */
    fun reload() {
        if (sessionId < 0) {
            return
        }

        sessionData.value = asbManager.getASBSession(sessionId) // note: error should never happen
    }

    /**
     * Creates a new wishlist and adds the armor items to that wishlist.
     * @param callback Executed if the operation was a success
     */
    fun addToNewWishlist(name: String, callback: () -> Unit) {
        loggedThread("Add to wishlist") {
            val wishlistManager = dataManager.wishlistManager
            val wishlistId = wishlistManager.addWishlist(name)
            for (piece in session.pieces) {
                if (piece.equipment is Armor) {
                    val armorId = piece.equipment.id
                    wishlistManager.addWishlistItem(wishlistId, armorId, quantity = 1)
                }
            }

            // execute callback now that it finished
            Handler(Looper.getMainLooper()).post(callback)
        }
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
    fun setTalisman(data: TalismanMetadata) {
        val talisman = ASBTalisman(data.typeIndex)
        val typeName = app.resources.getStringArray(R.array.talisman_names)[data.typeIndex]
        talisman.name = app.getString(R.string.talisman_full_name, typeName)
        talisman.numSlots = data.numSlots
        for ((skillId, points) in data.skills) {
            if (skillId < 0) continue

            val skillTree = dataManager.getSkillTree(skillId)
            if (skillTree != null) {
                talisman.addSkill(skillTree, points)
            }
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
        updatePieceEvent.postValue(pieceIndex)
    }

    /**
     * Updates the values of an ASBSet with a certain id, then reloads the data.
     */
    fun updateSet(name: String, rank: Rank, hunterType: Int) {
        // remove invalid pieces (doesn't restrict hunter rank yet...only gunner/blademaster really matters)
        for (piece in session.pieces.toList()) {
            if (piece.equipment !is Armor) {
                continue // ignore non-armor
            }
            if (piece.idx == ArmorSet.HEAD || piece.equipment.hunterType == Armor.ARMOR_TYPE_BOTH) {
                continue // ignore unrestricted armor and head pieces
            }

            if (piece.equipment.hunterType != hunterType) {
                session.removeEquipment(piece.idx)
            }
        }

        // Perform update
        session.name = name
        session.rank = rank
        session.hunterType = hunterType
        asbManager.updateASB(session)

        // Perform reload
        reload()
    }

    /**
     * Deletes the current session, but does not update the viewmodel afterwards.
     * After calling this, it is important to exit the activity.
     */
    fun deleteSet() {
        asbManager.queryDeleteASBSet(sessionId)
    }
}