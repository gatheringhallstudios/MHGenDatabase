package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.MonsterWeakness
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getBoolean
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getString

/**
 * Created by Jayson on 4/6/2015.
 * Cursor for a Monster Status Query
 */
class MonsterWeaknessCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Get next status object of the cursor
     * @return A MonsterWeakness object
     */
    // This isn't in the current version of the DB yet.
    //String monstername = getString(S.COLUMN_WEAKNESS_MONSTER_NAME));
    //weakness.setMonstername(monstername);
    val weakness: MonsterWeakness
        get() {
            val weakness = MonsterWeakness()

            with (weakness) {
                state = getString(S.COLUMN_WEAKNESS_STATE)
                fire = getInt(S.COLUMN_WEAKNESS_FIRE)
                water = getInt(S.COLUMN_WEAKNESS_WATER)
                thunder = getInt(S.COLUMN_WEAKNESS_THUNDER)
                ice = getInt(S.COLUMN_WEAKNESS_ICE)
                dragon = getInt(S.COLUMN_WEAKNESS_DRAGON)
                poison = getInt(S.COLUMN_WEAKNESS_POISON)
                paralysis = getInt(S.COLUMN_WEAKNESS_PARALYSIS)
                sleep = getInt(S.COLUMN_WEAKNESS_SLEEP)
                pitfalltrap = getBoolean(S.COLUMN_WEAKNESS_PITFALL_TRAP)
                shocktrap = getBoolean(S.COLUMN_WEAKNESS_SHOCK_TRAP)
                flashbomb = getBoolean(S.COLUMN_WEAKNESS_FLASH_BOMB)
                sonicbomb = getBoolean(S.COLUMN_WEAKNESS_SONIC_BOMB)
                dungbomb = getBoolean(S.COLUMN_WEAKNESS_DUNG_BOMB)
                meat = getBoolean(S.COLUMN_WEAKNESS_MEAT)
            }

            return weakness
        }

}
