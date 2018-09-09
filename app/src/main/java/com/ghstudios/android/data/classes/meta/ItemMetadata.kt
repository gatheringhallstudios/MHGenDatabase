package com.ghstudios.android.data.classes.meta

/**
 * Extremely basic data about an item used to drive initial item detail loading.
 */
data class ItemMetadata(
        val id: Long,
        val name: String,
        val usedInCombining: Boolean,
        val usedInCrafting: Boolean,
        val isMonsterReward: Boolean,
        val isQuestReward: Boolean,
        val isGatherable: Boolean
)