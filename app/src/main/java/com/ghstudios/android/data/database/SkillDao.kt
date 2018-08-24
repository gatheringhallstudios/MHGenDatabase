package com.ghstudios.android.data.database

import android.database.sqlite.SQLiteOpenHelper
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.cursors.ItemToSkillTreeCursor
import com.ghstudios.android.data.util.localizeColumn
import com.ghstudios.android.util.toList

class SkillDao(val dbMainHelper: SQLiteOpenHelper) {
    val db get() = dbMainHelper.writableDatabase

    private val column_name
        get() = localizeColumn("name")

    fun queryItemToSkillTreeForArmorFamily(familyId: Long): List<ItemToSkillTree> {
        val cursor = db.rawQuery("""
            SELECT its._id,its.skill_tree_id,st.$column_name AS sname,its.point_value,a._id AS item_id,
                i.$column_name AS iname,i.rarity,i.type,i.icon_name,i.icon_color
            FROM armor a
                JOIN items i on a._id=i._id
                JOIN item_to_skill_tree its on a._id=its.item_id
                JOIN skill_trees st on st._id=its.skill_tree_id
            WHERE a.family=?
        """, arrayOf(familyId.toString()))

        return ItemToSkillTreeCursor(cursor).toList { it.itemToSkillTree }
    }

}