package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.Gathering
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.Location
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getBoolean
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

/**
 * A convenience class to wrap a cursor that returns rows from the "gathering"
 * table. The getGathering() method will give you a Gathering instance
 * representing the current row.
 */
class GatheringCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Returns a Gathering object configured for the current row, or null if the
     * current row is invalid.
     */
    // Get the Item
    // Get the Location
    val gathering: Gathering
        get() {
            val gathering = Gathering().apply {
                area = getString(S.COLUMN_GATHERING_AREA)
                site = getString(S.COLUMN_GATHERING_SITE)
                rank = getString(S.COLUMN_GATHERING_RANK)
                rate = getInt(S.COLUMN_GATHERING_RATE).toFloat()
                group = getInt(S.COLUMN_GATHERING_GROUP)
                isFixed = getBoolean(S.COLUMN_GATHERING_FIXED)
                isRare = getBoolean(S.COLUMN_GATHERING_RARE)
                quantity = getInt(S.COLUMN_GATHERING_QUANTITY)
            }

            gathering.item = Item().apply {
                id = getLong(S.COLUMN_GATHERING_ITEM_ID)
                name = getString("i" + S.COLUMN_ITEMS_NAME)
                fileLocation = getString(S.COLUMN_ITEMS_ICON_NAME)
            }

            gathering.location = Location().apply {
                id = getLong(S.COLUMN_GATHERING_LOCATION_ID)
                name = getString("l" + S.COLUMN_LOCATIONS_NAME)
                fileLocation = getString("l" + S.COLUMN_LOCATIONS_MAP)
            }

            return gathering
        }
}