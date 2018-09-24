package com.ghstudios.android.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.cursors.ASBSessionCursor
import com.ghstudios.android.data.cursors.ASBSetCursor
import com.ghstudios.android.data.database.MonsterHunterDatabaseHelper
import com.ghstudios.android.data.database.S
import com.ghstudios.android.util.firstOrNull

private fun contentValuesFromMap(map: Map<String, Any>): ContentValues {
    val result = ContentValues()
    for ((key, value) in map.entries) {
        when (value) {
            is Boolean -> result.put(key, value)
            is String -> result.put(key, value)
            is Int -> result.put(key, value)
            is Long -> result.put(key, value)
            is Double -> result.put(key, value)
            is Float -> result.put(key, value)
            is ByteArray -> result.put(key, value)
            else -> {
                Log.e("ASBManager", "UNSUPPORTED TYPE for ${value}")
            }
        }
    }
    return result
}

class ASBManager internal constructor(
        private val mAppContext: Context,
        private val mHelper: MonsterHunterDatabaseHelper
) {
    fun queryASBSets(): ASBSetCursor {
        return mHelper.queryASBSets()
    }

    fun getASBSet(id: Long): ASBSet? {
        return mHelper.queryASBSet(id).firstOrNull { it.asbSet }
    }

    /** Get a cursor with a list of all armor sets.  */
    fun queryASBSessions(): ASBSessionCursor {
        return mHelper.queryASBSessions()
    }

    /** Get a specific armor set.  */
    fun getASBSession(id: Long): ASBSession? {
        return mHelper.queryASBSession(id).firstOrNull { it.getASBSession(mAppContext) }
    }

    /** Adds a new ASB set to the list.  */
    fun queryAddASBSet(name: String, rank: Int, hunterType: Int) {
        mHelper.queryAddASBSet(name, rank, hunterType)
    }

    // todo: rename, or replace for something else
    /** Adds a new set that is a copy of the designated set to the list.  */
    fun queryAddASBSet(setId: Long) {
        val set = getASBSet(setId)
        mHelper.queryAddASBSet(set!!.name!!, set.rank, set.hunterType)
    }

    fun queryDeleteASBSet(setId: Long) {
        mHelper.queryDeleteASBSet(setId)
    }

    fun queryUpdateASBSet(setId: Long, name: String, rank: Int, hunterType: Int) {
        mHelper.queryUpdateASBSet(setId, name, rank, hunterType)
    }

    fun updateASB(set: ASBSession): Long {
        val weaponPiece = set.getPiece(ArmorSet.WEAPON)
        val headPiece = set.getPiece(ArmorSet.HEAD)
        val bodyPiece = set.getPiece(ArmorSet.BODY)
        val armsPiece = set.getPiece(ArmorSet.ARMS)
        val waistPiece = set.getPiece(ArmorSet.WAIST)
        val legsPiece = set.getPiece(ArmorSet.LEGS)
        val talismanPiece = set.getPiece(ArmorSet.TALISMAN)

        fun getArmorId(piece: ArmorSetPiece?): Long {
            return piece?.equipment?.id ?: -1L
        }

        fun getDeco(piece: ArmorSetPiece?, idx: Int): Long {
            return piece?.decorations?.getOrNull(idx)?.id ?: -1L
        }

        val updatedColumns = mapOf<String, Any>(
                S.COLUMN_ASB_SET_NAME to set.name,
                S.COLUMN_ASB_SET_HUNTER_TYPE to set.hunterType,
                S.COLUMN_ASB_SET_RANK to set.rank,

                S.COLUMN_ASB_WEAPON_SLOTS to set.numWeaponSlots,
                S.COLUMN_ASB_WEAPON_DECORATION_1_ID to getDeco(weaponPiece, 0),
                S.COLUMN_ASB_WEAPON_DECORATION_2_ID to getDeco(weaponPiece, 1),
                S.COLUMN_ASB_WEAPON_DECORATION_3_ID to getDeco(weaponPiece, 2),

                S.COLUMN_HEAD_ARMOR_ID to getArmorId(headPiece),
                S.COLUMN_HEAD_DECORATION_1_ID to getDeco(headPiece, 0),
                S.COLUMN_HEAD_DECORATION_2_ID to getDeco(headPiece, 1),
                S.COLUMN_HEAD_DECORATION_3_ID to getDeco(headPiece, 2),

                S.COLUMN_BODY_ARMOR_ID to getArmorId(bodyPiece),
                S.COLUMN_BODY_DECORATION_1_ID to getDeco(bodyPiece, 0),
                S.COLUMN_BODY_DECORATION_2_ID to getDeco(bodyPiece, 1),
                S.COLUMN_BODY_DECORATION_3_ID to getDeco(bodyPiece, 2),

                S.COLUMN_ARMS_ARMOR_ID to getArmorId(armsPiece),
                S.COLUMN_ARMS_DECORATION_1_ID to getDeco(armsPiece, 0),
                S.COLUMN_ARMS_DECORATION_2_ID to getDeco(armsPiece, 1),
                S.COLUMN_ARMS_DECORATION_3_ID to getDeco(armsPiece, 2),

                S.COLUMN_WAIST_ARMOR_ID to getArmorId(waistPiece),
                S.COLUMN_WAIST_DECORATION_1_ID to getDeco(waistPiece, 0),
                S.COLUMN_WAIST_DECORATION_2_ID to getDeco(waistPiece, 1),
                S.COLUMN_WAIST_DECORATION_3_ID to getDeco(waistPiece, 2),

                S.COLUMN_LEGS_ARMOR_ID to getArmorId(legsPiece),
                S.COLUMN_LEGS_DECORATION_1_ID to getDeco(legsPiece, 0),
                S.COLUMN_LEGS_DECORATION_2_ID to getDeco(legsPiece, 1),
                S.COLUMN_LEGS_DECORATION_3_ID to getDeco(legsPiece, 2),

                S.COLUMN_TALISMAN_EXISTS to (talismanPiece != null),
                S.COLUMN_TALISMAN_TYPE to (set.talisman?.typeIndex ?: -1),
                S.COLUMN_TALISMAN_SLOTS to (set.talisman?.numSlots ?: 0),
                S.COLUMN_TALISMAN_SKILL_1_ID to (set.talisman?.firstSkill?.skillTree?.id ?: -1L),
                S.COLUMN_TALISMAN_SKILL_1_POINTS to (set.talisman?.firstSkill?.points ?: 0),
                S.COLUMN_TALISMAN_SKILL_2_ID to (set.talisman?.secondSkill?.skillTree?.id ?: -1L),
                S.COLUMN_TALISMAN_SKILL_2_POINTS to (set.talisman?.secondSkill?.points ?: 0),
                S.COLUMN_TALISMAN_DECORATION_1_ID to getDeco(talismanPiece, 0),
                S.COLUMN_TALISMAN_DECORATION_2_ID to getDeco(talismanPiece, 1),
                S.COLUMN_TALISMAN_DECORATION_3_ID to getDeco(talismanPiece, 2)
        )

        val filter = S.COLUMN_ASB_SET_ID + " = " + set.id
        val values = contentValuesFromMap(updatedColumns)
        return mHelper.updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }
}