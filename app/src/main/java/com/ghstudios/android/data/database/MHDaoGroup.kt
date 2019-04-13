package com.ghstudios.android.data.database

import android.content.Context

/**
 * Collection of all monster hunter daos. Allows for cross referencing
 */
class MHDaoGroup(
        appContext: Context,
        dbMainHelper: MonsterHunterDatabaseHelper
) {
    val metadataDao = MetadataDao(dbMainHelper)
    val monsterDao = MonsterDao(dbMainHelper)
    val itemDao = ItemDao(this, appContext, dbMainHelper)
    val skillDao = SkillDao(dbMainHelper)
}