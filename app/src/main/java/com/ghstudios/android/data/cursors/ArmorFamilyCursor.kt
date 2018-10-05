package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper
import com.ghstudios.android.data.classes.ArmorFamily
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

class ArmorFamilyCursor(c:Cursor) : CursorWrapper(c){
    val armor: ArmorFamily
        get() {
            return ArmorFamily().apply {
                id = getLong("_id")
                minDef = getInt("min")
                maxDef = getInt("max")
                name = getString("name")
                rarity = getInt("rarity")
                hunterType = getInt("hunter_type")
                val pv = getInt("point_value")
                skills.add(getString("st_name")+( if(pv>0) "+" else "")+pv.toString())
            }
        }
}