package com.ghstudios.android.data.cursors

import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString
import com.ghstudios.android.mhgendatabase.*
import kotlin.math.min

class ASBSessionCursor(c: Cursor) : CursorWrapper(c) {

    fun getASBSession(context: Context): ASBSession? {
        if (isBeforeFirst || isAfterLast) {
            return null
        }

        val session = ASBSession(
                id = getLong(S.COLUMN_ASB_SET_ID),
                name = getString(S.COLUMN_ASB_SET_NAME, ""),
                rank = Rank.from(getInt(S.COLUMN_ASB_SET_RANK)),
                hunterType = getInt(S.COLUMN_ASB_SET_HUNTER_TYPE)
        )

        val weaponSlots = getInt(S.COLUMN_ASB_WEAPON_SLOTS)
        val weaponDecoration1 = getDecorationById(getLong(S.COLUMN_ASB_WEAPON_DECORATION_1_ID))
        val weaponDecoration2 = getDecorationById(getLong(S.COLUMN_ASB_WEAPON_DECORATION_2_ID))
        val weaponDecoration3 = getDecorationById(getLong(S.COLUMN_ASB_WEAPON_DECORATION_3_ID))

        val headId = getLong(S.COLUMN_HEAD_ARMOR_ID)
        val headArmor = getArmorById(headId)
        val headDecoration1Id = getLong(S.COLUMN_HEAD_DECORATION_1_ID)
        val headDecoration2Id = getLong(S.COLUMN_HEAD_DECORATION_2_ID)
        val headDecoration3Id = getLong(S.COLUMN_HEAD_DECORATION_3_ID)
        val headDecoration1 = getDecorationById(headDecoration1Id)
        val headDecoration2 = getDecorationById(headDecoration2Id)
        val headDecoration3 = getDecorationById(headDecoration3Id)

        val bodyId = getLong(S.COLUMN_BODY_ARMOR_ID)
        val bodyArmor = getArmorById(bodyId)
        val bodyDecoration1Id = getLong(S.COLUMN_BODY_DECORATION_1_ID)
        val bodyDecoration2Id = getLong(S.COLUMN_BODY_DECORATION_2_ID)
        val bodyDecoration3Id = getLong(S.COLUMN_BODY_DECORATION_3_ID)
        val bodyDecoration1 = getDecorationById(bodyDecoration1Id)
        val bodyDecoration2 = getDecorationById(bodyDecoration2Id)
        val bodyDecoration3 = getDecorationById(bodyDecoration3Id)

        val armsId = getLong(S.COLUMN_ARMS_ARMOR_ID)
        val armsArmor = getArmorById(armsId)
        val armsDecoration1Id = getLong(S.COLUMN_ARMS_DECORATION_1_ID)
        val armsDecoration2Id = getLong(S.COLUMN_ARMS_DECORATION_2_ID)
        val armsDecoration3Id = getLong(S.COLUMN_ARMS_DECORATION_3_ID)
        val armsDecoration1 = getDecorationById(armsDecoration1Id)
        val armsDecoration2 = getDecorationById(armsDecoration2Id)
        val armsDecoration3 = getDecorationById(armsDecoration3Id)

        val waistId = getLong(S.COLUMN_WAIST_ARMOR_ID)
        val waistArmor = getArmorById(waistId)
        val waistDecoration1Id = getLong(S.COLUMN_WAIST_DECORATION_1_ID)
        val waistDecoration2Id = getLong(S.COLUMN_WAIST_DECORATION_2_ID)
        val waistDecoration3Id = getLong(S.COLUMN_WAIST_DECORATION_3_ID)
        val waistDecoration1 = getDecorationById(waistDecoration1Id)
        val waistDecoration2 = getDecorationById(waistDecoration2Id)
        val waistDecoration3 = getDecorationById(waistDecoration3Id)

        val legsId = getLong(S.COLUMN_LEGS_ARMOR_ID)
        val legsArmor = getArmorById(legsId)
        val legsDecoration1Id = getLong(S.COLUMN_LEGS_DECORATION_1_ID)
        val legsDecoration2Id = getLong(S.COLUMN_LEGS_DECORATION_2_ID)
        val legsDecoration3Id = getLong(S.COLUMN_LEGS_DECORATION_3_ID)
        val legsDecoration1 = getDecorationById(legsDecoration1Id)
        val legsDecoration2 = getDecorationById(legsDecoration2Id)
        val legsDecoration3 = getDecorationById(legsDecoration3Id)

        val talismanExists = getInt(S.COLUMN_TALISMAN_EXISTS)
        val talismanSkill1Id = getLong(S.COLUMN_TALISMAN_SKILL_1_ID)
        val talismanSkill1Points = getInt(S.COLUMN_TALISMAN_SKILL_1_POINTS)
        val talismanSkill2Id = getLong(S.COLUMN_TALISMAN_SKILL_2_ID)
        val talismanSkill2Points = getInt(S.COLUMN_TALISMAN_SKILL_2_POINTS)
        var talismanType = getInt(S.COLUMN_TALISMAN_TYPE)
        val talismanSlots = getInt(S.COLUMN_TALISMAN_SLOTS)
        val talismanDecoration1Id = getLong(S.COLUMN_TALISMAN_DECORATION_1_ID)
        val talismanDecoration2Id = getLong(S.COLUMN_TALISMAN_DECORATION_2_ID)
        val talismanDecoration3Id = getLong(S.COLUMN_TALISMAN_DECORATION_3_ID)
        val talismanDecoration1 = getDecorationById(talismanDecoration1Id)
        val talismanDecoration2 = getDecorationById(talismanDecoration2Id)
        val talismanDecoration3 = getDecorationById(talismanDecoration3Id)

        // Set armor pieces
        session.numWeaponSlots = weaponSlots
        headArmor?.let { session.setEquipment(ArmorSet.HEAD, it) }
        bodyArmor?.let { session.setEquipment(ArmorSet.BODY, it) }
        armsArmor?.let { session.setEquipment(ArmorSet.ARMS, it) }
        waistArmor?.let { session.setEquipment(ArmorSet.WAIST, it) }
        legsArmor?.let { session.setEquipment(ArmorSet.LEGS, it) }

        // Set Weapon decorations
        weaponDecoration1?.let { session.addDecoration(ArmorSet.WEAPON, it) }
        weaponDecoration2?.let { session.addDecoration(ArmorSet.WEAPON, it) }
        weaponDecoration3?.let { session.addDecoration(ArmorSet.WEAPON, it) }

        if (headDecoration1 != null) {
            session.addDecoration(ArmorSet.HEAD, headDecoration1)
        }
        if (headDecoration2 != null) {
            session.addDecoration(ArmorSet.HEAD, headDecoration2)
        }
        if (headDecoration3 != null) {
            session.addDecoration(ArmorSet.HEAD, headDecoration3)
        }

        if (bodyDecoration1 != null) {
            session.addDecoration(ArmorSet.BODY, bodyDecoration1)
        }
        if (bodyDecoration2 != null) {
            session.addDecoration(ArmorSet.BODY, bodyDecoration2)
        }
        if (bodyDecoration3 != null) {
            session.addDecoration(ArmorSet.BODY, bodyDecoration3)
        }

        if (armsDecoration1 != null) {
            session.addDecoration(ArmorSet.ARMS, armsDecoration1)
        }
        if (armsDecoration2 != null) {
            session.addDecoration(ArmorSet.ARMS, armsDecoration2)
        }
        if (armsDecoration3 != null) {
            session.addDecoration(ArmorSet.ARMS, armsDecoration3)
        }

        if (waistDecoration1 != null) {
            session.addDecoration(ArmorSet.WAIST, waistDecoration1)
        }
        if (waistDecoration2 != null) {
            session.addDecoration(ArmorSet.WAIST, waistDecoration2)
        }
        if (waistDecoration3 != null) {
            session.addDecoration(ArmorSet.WAIST, waistDecoration3)
        }

        if (legsDecoration1 != null) {
            session.addDecoration(ArmorSet.LEGS, legsDecoration1)
        }
        if (legsDecoration2 != null) {
            session.addDecoration(ArmorSet.LEGS, legsDecoration2)
        }
        if (legsDecoration3 != null) {
            session.addDecoration(ArmorSet.LEGS, legsDecoration3)
        }

        if (talismanExists == 1) {
            val talismanNames = context.resources.getStringArray(R.array.talisman_names)
            talismanType = min(talismanNames.size - 1, talismanType)

            val talisman = ASBTalisman(talismanType)

            val typeName = talismanNames[talismanType]
            talisman.name = context.getString(R.string.talisman_full_name, typeName)
            talisman.numSlots = talismanSlots
            talisman.setFirstSkill(getSkillTreeById(talismanSkill1Id)!!, talismanSkill1Points)

            if (talismanSkill2Id != -1L) {
                talisman.setSecondSkill(getSkillTreeById(talismanSkill2Id), talismanSkill2Points)
            }

            session.setEquipment(ArmorSet.TALISMAN, talisman)

            if (talismanDecoration1 != null) {
                session.addDecoration(ArmorSet.TALISMAN, talismanDecoration1)
            }
            if (talismanDecoration2 != null) {
                session.addDecoration(ArmorSet.TALISMAN, talismanDecoration2)
            }
            if (talismanDecoration3 != null) {
                session.addDecoration(ArmorSet.TALISMAN, talismanDecoration3)
            }
        }

        return session
    }

    private fun getArmorById(id: Long): Armor? {
        return if (id != 0L && id != -1L) {
            DataManager.get().getArmor(id)
        } else
            null
    }

    private fun getDecorationById(id: Long): Decoration? {
        return if (id != 0L && id != -1L) {
            DataManager.get().getDecoration(id)
        } else
            null
    }

    private fun getSkillTreeById(id: Long): SkillTree? {
        return if (id != 0L && id != -1L) {
            DataManager.get().getSkillTree(id)
        } else
            null
    }
}
