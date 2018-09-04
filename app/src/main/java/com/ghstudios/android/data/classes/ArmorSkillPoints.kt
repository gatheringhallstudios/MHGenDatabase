package com.ghstudios.android.data.classes

/**
 * A pairing for armor and a list of skills related to that armor
 */
class ArmorSkillPoints(
        val armor: Armor,
        val skills: List<SkillTreePoints>
)