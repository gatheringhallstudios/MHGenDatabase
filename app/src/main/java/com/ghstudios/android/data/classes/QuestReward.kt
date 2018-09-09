package com.ghstudios.android.data.classes

/*
 * Class for QuestReward
 */
class QuestReward {

    var id: Long = -1
    var quest: Quest? = null
    var item: Item? = null
    var rewardSlot: String? = ""       // Slot A or Slot B
    var percentage: Int = -1           // Percentage to obtain item
    var stackSize: Int = -1            // Amount of item obtained
}
