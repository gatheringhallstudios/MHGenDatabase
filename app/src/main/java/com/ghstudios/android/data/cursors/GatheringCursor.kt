package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.Gathering
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.Location
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.*

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
                site = if(hasColumn(S.COLUMN_GATHERING_SITE)) getString(S.COLUMN_GATHERING_SITE) else null
                rank = if(hasColumn(S.COLUMN_GATHERING_RANK)) getString(S.COLUMN_GATHERING_RANK) else null
                rate = getInt(S.COLUMN_GATHERING_RATE).toFloat()
                group = if(hasColumn(S.COLUMN_GATHERING_GROUP)) getInt(S.COLUMN_GATHERING_GROUP) else 0
                isFixed = getBoolean(S.COLUMN_GATHERING_FIXED)
                isRare = getBoolean(S.COLUMN_GATHERING_RARE)
                quantity = getInt(S.COLUMN_GATHERING_QUANTITY)
            }

            gathering.item = Item().apply {
                id = if(hasColumn(S.COLUMN_GATHERING_ITEM_ID)) getLong(S.COLUMN_GATHERING_ITEM_ID) else -1
                name = if(hasColumn("i" + S.COLUMN_ITEMS_NAME))getString("i" + S.COLUMN_ITEMS_NAME) else null
                iconColor = if(hasColumn(S.COLUMN_ITEMS_ICON_COLOR)) getInt(S.COLUMN_ITEMS_ICON_COLOR) else 0
                fileLocation = if(hasColumn(S.COLUMN_ITEMS_ICON_NAME)) getString(S.COLUMN_ITEMS_ICON_NAME) else null
            }

            if(hasColumn(S.COLUMN_GATHERING_LOCATION_ID)) {
                gathering.location = Location().apply {
                    id = getLong(S.COLUMN_GATHERING_LOCATION_ID)
                    name = getString("l" + S.COLUMN_LOCATIONS_NAME)
                    fileLocation = getString("l" + S.COLUMN_LOCATIONS_MAP)
                }
            }

            return gathering
        }
}