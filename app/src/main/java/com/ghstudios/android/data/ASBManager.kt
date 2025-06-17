package com.ghstudios.android.data

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.cursors.ASBSessionCursor
import com.ghstudios.android.data.cursors.ASBSetCursor
import com.ghstudios.android.data.database.MonsterHunterDatabaseHelper
import com.ghstudios.android.data.database.S
import com.ghstudios.android.util.firstOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException

/**
 * Extension used to build an iterator from a JSONArray. Each index is evaluated using transform.
 */
fun <T> JSONArray.iter(transform: JSONArray.(Int) -> T) = sequence {
    for (i in 0..(this@iter.length() - 1)) {
        yield(transform(i))
    }
}

/**
 * Creates a ContentValues object from a map of arbitrary value types.
 */
private fun contentValuesFromMap(map: Map<String, Any>): ContentValues {
    val result = ContentValues()
    for ((key, value) in map.entries) {
        when (value) {
            is Boolean -> result.put(key, value)
            is String -> result.put(key, value)
            is Long -> result.put(key, value)
            is Int -> result.put(key, value)
            is Byte -> result.put(key, value)
            is Double -> result.put(key, value)
            is Float -> result.put(key, value)
            is ByteArray -> result.put(key, value)
            else -> {
                Log.e("ASBManager", "UNSUPPORTED TYPE for $value")
            }
        }
    }
    return result
}

class ASBManager internal constructor(
        private val mAppContext: Context,
        private val dataManager: DataManager,
        private val mHelper: MonsterHunterDatabaseHelper
) {
    val TAG = "ASBManager"

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
    fun queryAddASBSet(name: String, rank: Rank, hunterType: Int) {
        mHelper.queryAddASBSet(name, rank, hunterType)
    }

    fun queryDeleteASBSet(setId: Long) {
        mHelper.queryDeleteASBSet(setId)
    }

    fun queryUpdateASBSet(setId: Long, name: String, rank: Rank, hunterType: Int) {
        mHelper.queryUpdateASBSet(setId, name, rank, hunterType)
    }


    /**
     * Updates an existing ASB in place
     */
    fun updateASB(set: ASBSession): Long {
        val values = columnsForASBSession(set)
        val filter = S.COLUMN_ASB_SET_ID + " = " + set.id
        return mHelper.updateRecord(S.TABLE_ASB_SETS, filter, values).toLong()
    }

    /**
     * Copies an existing ASB. Returns the newly generated id.
     */
    fun copyASB(set: ASBSession): Long {
        val values = columnsForASBSession(set)
        return mHelper.insertRecord(S.TABLE_ASB_SETS, values)
    }

    /**
     * Copies an existing ASB using the set ID. Returns the newly generated id.
     */
    fun copyASB(setId: Long): Long {
        val session = getASBSession(setId)
        if (session == null) {
            Log.w(TAG, "Session to copy $setId does not exists")
            return -1
        }
        return copyASB(session)
    }

    /**
     * Internal helper that returns the set of columns used to persist an ASBSession, except for ID.
     */
    private fun columnsForASBSession(set: ASBSession): ContentValues {
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
                S.COLUMN_ASB_SET_RANK to set.rank.value,

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

        return contentValuesFromMap(updatedColumns)
    }

    /**
     * Returns a list of all talismans saved in the talismans.json file
     */
    fun getTalismans(): List<ASBTalisman> {
        try {
            val stream = mAppContext.openFileInput("talismans.json")
            val contents = stream.use {
                it.bufferedReader().use { br -> br.readText() }
            }

            // Micro optimization
            val cachedSkills = mutableMapOf<Long, SkillTree?>()

            val talismanDataObj = JSONObject(contents)
            val talismanItems = talismanDataObj.getJSONArray("talismans")
            val results = mutableListOf<ASBTalisman>()

            for (talismanObj in talismanItems.iter { getJSONObject(it)}) {
                val typeIdx = talismanObj.getInt("type")
                val skills = talismanObj.getJSONArray("skills").iter {
                    val obj = getJSONObject(it)
                    Pair(obj.getLong("id"), obj.getInt("points"))
                }

                val talisman = ASBTalisman(typeIdx)
                talisman.id = talismanObj.getLong("id")
                talisman.numSlots = talismanObj.getInt("slots")
                for ((skillId, points) in skills) {
                    val skillTree = cachedSkills.getOrPut(skillId) { dataManager.getSkillTree(skillId) }
                    if (skillTree != null) {
                        talisman.addSkill(skillTree, points)
                    }
                }

                results.add(talisman)
            }

            return results

        } catch (ex: FileNotFoundException) {
            return emptyList()
        } catch (ex: JSONException) {
            Log.e(javaClass.name, "JSON ERROR", ex)
            return emptyList()
        }
    }

    /**
     * Adds a talisman to the talisman list, and returns the new list
     */
    fun saveTalisman(talisman: ASBTalisman): List<ASBTalisman> {
        val list = getTalismans().toMutableList()

        if (talisman.id == -1L) {
            // Adding talisman
            talisman.id = (list.maxByOrNull { it.id }?.id ?: 0) + 1
            list.add(talisman)
        } else {
            // Editing talisman
            val existingIdx = list.indexOfFirst { it.id == talisman.id }
            if (existingIdx < 0) {
                list.add(talisman)
            } else {
                list[existingIdx] = talisman
            }
        }
        saveTalismans(list)

        return list
    }

    /**
     * Internal function to save talismans. Since talismans are JSON, we need to
     * overwrite all of them every time.
     */
    fun saveTalismans(talismans: List<ASBTalisman>) {
        val talismanListObj = JSONArray(talismans.map {
            JSONObject()
                    .put("id", it.id)
                    .put("type", it.typeIndex)
                    .put("slots", it.numSlots)
                    .put("skills", JSONArray(it.skills.map {s ->
                        JSONObject(mapOf(
                                "id" to s.skillTree.id,
                                "points" to s.points
                        ))
                    }))
        })

        val result = JSONObject(mapOf(
                "talismans" to talismanListObj
        ))

        val resultString = result.toString()
        val stream = mAppContext.openFileOutput("talismans.json", MODE_PRIVATE)
        stream.use {
            stream.bufferedWriter().use { it.write(resultString) }
        }
    }
}