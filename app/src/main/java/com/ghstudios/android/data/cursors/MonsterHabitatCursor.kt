package com.ghstudios.android.data.cursors

import android.database.Cursor
import android.database.CursorWrapper

import com.ghstudios.android.data.classes.Habitat
import com.ghstudios.android.data.classes.Location
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterClassConverter
import com.ghstudios.android.data.database.S
import com.ghstudios.android.data.util.getInt
import com.ghstudios.android.data.util.getLong
import com.ghstudios.android.data.util.getString

/**
 * Created by Mark on 2/22/2015.
 */
class MonsterHabitatCursor(c: Cursor) : CursorWrapper(c) {

    /**
     * Generates a Habitat object after retrieving entries from the database
     * @return The habitat object
     */
    val habitat: Habitat
        get() {
            return Habitat().apply {
                id = getLong(S.COLUMN_HABITAT_ID)
                start = getLong(S.COLUMN_HABITAT_START)
                rest = getLong(S.COLUMN_HABITAT_REST)

                // Assign areas (Split the areas string)
                areas = getString(S.COLUMN_HABITAT_AREAS, "")
                        .split(",")
                        .dropLastWhile { it.isEmpty() }
                        .map { it.toLong() }
                        .toLongArray()

                // Assign location (subset of results)
                location = Location().apply {
                    id = getLong("l"+S.COLUMN_LOCATIONS_ID)
                    name = getString("l" + S.COLUMN_LOCATIONS_NAME)
                    fileLocation = getString("l" + S.COLUMN_LOCATIONS_MAP)
                }

                // Assign monster
                monster = Monster().apply {
                    id = getLong("m" + S.COLUMN_MONSTERS_ID)
                    name = getString("m" + S.COLUMN_MONSTERS_NAME, "")
                    fileLocation = getString("m" + S.COLUMN_MONSTERS_FILE_LOCATION, "")
                    monsterClass = MonsterClassConverter.deserialize(getInt("m" + S.COLUMN_MONSTERS_CLASS))
                }
            }
        }
}
