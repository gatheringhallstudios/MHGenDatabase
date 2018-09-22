package com.ghstudios.android.data.cursors

import android.content.Context
import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.mhgendatabase.*

class ASBSessionCursor(c: Cursor) : CursorWrapper(c) {

    fun getASBSession(context: Context): ASBSession? {
        if (isBeforeFirst || isAfterLast) {
            return null
        }

        val session = ASBSession()

        val id = getLong(S.COLUMN_ASB_SET_ID)

        val set = DataManager.get().getASBSet(id)
        session.setASBSet(set!!)

        val headId = getLong(S.COLUMN_HEAD_ARMOR_ID)
        val headDecoration1Id = getLong(S.COLUMN_HEAD_DECORATION_1_ID)
        val headDecoration2Id = getLong(S.COLUMN_HEAD_DECORATION_2_ID)
        val headDecoration3Id = getLong(S.COLUMN_HEAD_DECORATION_3_ID)
        val headArmor = getArmorById(context, headId)
        val headDecoration1 = getDecorationById(context, headDecoration1Id)
        val headDecoration2 = getDecorationById(context, headDecoration2Id)
        val headDecoration3 = getDecorationById(context, headDecoration3Id)

        val bodyId = getLong(S.COLUMN_BODY_ARMOR_ID)
        val bodyDecoration1Id = getLong(S.COLUMN_BODY_DECORATION_1_ID)
        val bodyDecoration2Id = getLong(S.COLUMN_BODY_DECORATION_2_ID)
        val bodyDecoration3Id = getLong(S.COLUMN_BODY_DECORATION_3_ID)
        val bodyArmor = getArmorById(context, bodyId)
        val bodyDecoration1 = getDecorationById(context, bodyDecoration1Id)
        val bodyDecoration2 = getDecorationById(context, bodyDecoration2Id)
        val bodyDecoration3 = getDecorationById(context, bodyDecoration3Id)

        val armsId = getLong(S.COLUMN_ARMS_ARMOR_ID)
        val armsDecoration1Id = getLong(S.COLUMN_ARMS_DECORATION_1_ID)
        val armsDecoration2Id = getLong(S.COLUMN_ARMS_DECORATION_2_ID)
        val armsDecoration3Id = getLong(S.COLUMN_ARMS_DECORATION_3_ID)
        val armsArmor = getArmorById(context, armsId)
        val armsDecoration1 = getDecorationById(context, armsDecoration1Id)
        val armsDecoration2 = getDecorationById(context, armsDecoration2Id)
        val armsDecoration3 = getDecorationById(context, armsDecoration3Id)

        val waistId = getLong(S.COLUMN_WAIST_ARMOR_ID)
        val waistDecoration1Id = getLong(S.COLUMN_WAIST_DECORATION_1_ID)
        val waistDecoration2Id = getLong(S.COLUMN_WAIST_DECORATION_2_ID)
        val waistDecoration3Id = getLong(S.COLUMN_WAIST_DECORATION_3_ID)
        val waistArmor = getArmorById(context, waistId)
        val waistDecoration1 = getDecorationById(context, waistDecoration1Id)
        val waistDecoration2 = getDecorationById(context, waistDecoration2Id)
        val waistDecoration3 = getDecorationById(context, waistDecoration3Id)

        val legsId = getLong(S.COLUMN_LEGS_ARMOR_ID)
        val legsDecoration1Id = getLong(S.COLUMN_LEGS_DECORATION_1_ID)
        val legsDecoration2Id = getLong(S.COLUMN_LEGS_DECORATION_2_ID)
        val legsDecoration3Id = getLong(S.COLUMN_LEGS_DECORATION_3_ID)
        val legsArmor = getArmorById(context, legsId)
        val legsDecoration1 = getDecorationById(context, legsDecoration1Id)
        val legsDecoration2 = getDecorationById(context, legsDecoration2Id)
        val legsDecoration3 = getDecorationById(context, legsDecoration3Id)

        val talismanExists = getInt(S.COLUMN_TALISMAN_EXISTS)
        val talismanSkill1Id = getLong(S.COLUMN_TALISMAN_SKILL_1_ID)
        val talismanSkill1Points = getInt(S.COLUMN_TALISMAN_SKILL_1_POINTS)
        val talismanSkill2Id = getLong(S.COLUMN_TALISMAN_SKILL_2_ID)
        val talismanSkill2Points = getInt(S.COLUMN_TALISMAN_SKILL_2_POINTS)
        val talismanType = getInt(S.COLUMN_TALISMAN_TYPE)
        val talismanSlots = getInt(S.COLUMN_TALISMAN_SLOTS)
        val talismanDecoration1Id = getLong(S.COLUMN_TALISMAN_DECORATION_1_ID)
        val talismanDecoration2Id = getLong(S.COLUMN_TALISMAN_DECORATION_2_ID)
        val talismanDecoration3Id = getLong(S.COLUMN_TALISMAN_DECORATION_3_ID)
        val talismanDecoration1 = getDecorationById(context, talismanDecoration1Id)
        val talismanDecoration2 = getDecorationById(context, talismanDecoration2Id)
        val talismanDecoration3 = getDecorationById(context, talismanDecoration3Id)

        if (headArmor != null) {
            session.setEquipment(ArmorSet.HEAD, headArmor)
        }
        if (headDecoration1 != null) {
            session.addDecoration(ArmorSet.HEAD, headDecoration1)
        }
        if (headDecoration2 != null) {
            session.addDecoration(ArmorSet.HEAD, headDecoration2)
        }
        if (headDecoration3 != null) {
            session.addDecoration(ArmorSet.HEAD, headDecoration3)
        }

        if (bodyArmor != null) {
            session.setEquipment(ArmorSet.BODY, bodyArmor)
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

        if (armsArmor != null) {
            session.setEquipment(ArmorSet.ARMS, armsArmor)
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

        if (waistArmor != null) {
            session.setEquipment(ArmorSet.WAIST, waistArmor)
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

        if (legsArmor != null) {
            session.setEquipment(ArmorSet.LEGS, legsArmor)
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
            val talismanType = Math.min(talismanNames.size - 1, talismanType)

            val talisman = ASBTalisman(talismanType)

            val typeName = talismanNames[talismanType]
            talisman.name = context.getString(R.string.talisman_full_name, typeName)
            talisman.numSlots = talismanSlots
            talisman.setFirstSkill(getSkillTreeById(context, talismanSkill1Id)!!, talismanSkill1Points)

            if (talismanSkill2Id != -1L) {
                talisman.setSecondSkill(getSkillTreeById(context, talismanSkill2Id), talismanSkill2Points)
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

    private fun getArmorById(context: Context, id: Long): Armor? {
        return if (id != 0L && id != -1L) {
            DataManager.get().getArmor(id)
        } else
            null
    }

    private fun getDecorationById(context: Context, id: Long): Decoration? {
        return if (id != 0L && id != -1L) {
            DataManager.get().getDecoration(id)
        } else
            null
    }

    private fun getSkillTreeById(context: Context, id: Long): SkillTree? {
        return if (id != 0L && id != -1L) {
            DataManager.get().getSkillTree(id)
        } else
            null
    }
}
