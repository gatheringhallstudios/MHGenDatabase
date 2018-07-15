package com.ghstudios.android.data.classes.meta

/**
 * Extremely basic data about a monster.
 * Loads the first run of data that can be used to guide additional data loading
 * and initial setup
 */
class MonsterMetadata(
        val id: Long,
        val name: String,
        val hasDamageData: Boolean,
        val hasStatusData: Boolean,
        val hasLowRank:Boolean,
        val hasHighRank:Boolean,
        val hasGRank:Boolean
)