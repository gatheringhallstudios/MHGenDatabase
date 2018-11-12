package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.data.classes.Rank
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

class ASBSetCursor(c: Cursor) : CursorWrapper(c) {

    val asbSet: ASBSet
        get() {
           return ASBSet(
                    id = getLong(S.COLUMN_ASB_SET_ID),
                    name = getString(S.COLUMN_ASB_SET_NAME, ""),
                    rank = Rank.from(getInt(S.COLUMN_ASB_SET_RANK)),
                    hunterType = getInt(S.COLUMN_ASB_SET_HUNTER_TYPE)
            )
        }
}
