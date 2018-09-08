package com.ghstudios.android.features.quests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ghstudios.android.ClickListeners.QuestClickListener
import com.ghstudios.android.data.classes.Quest
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.setImageAsset

enum class QuestAdapterType {
    VILLAGE,
    GUILD,
    PERMIT
}

class QuestGroup(
        val name: String,
        val stars: Int,
        val quests: List<Quest>
)

class QuestListExpandableAdapter(
        private val questGroups: List<QuestGroup>,
        private val type: QuestAdapterType
) : BaseExpandableListAdapter() {

    override fun hasStableIds() = true
    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun getGroupCount() = questGroups.size
    override fun getChildrenCount(i: Int) = questGroups[i].quests.size

    override fun getGroupId(i: Int) = i.toLong()
    override fun getChildId(groupPosition: Int, childPosition: Int) = groupPosition * 200L + childPosition

    override fun getGroup(i: Int) = questGroups[i]

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return questGroups[groupPosition].quests[childPosition]
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?,
                              viewGroup: ViewGroup?): View {
        val context = viewGroup?.context
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(
                R.layout.fragment_quest_expandablelist_group_item,
                viewGroup, false)

        val questGroupTextView = v.findViewById<TextView>(R.id.numstars)
        val stars = arrayOfNulls<ImageView>(10)

        stars[0] = v.findViewById(R.id.star1)
        stars[1] = v.findViewById(R.id.star2)
        stars[2] = v.findViewById(R.id.star3)
        stars[3] = v.findViewById(R.id.star4)
        stars[4] = v.findViewById(R.id.star5)
        stars[5] = v.findViewById(R.id.star6)
        stars[6] = v.findViewById(R.id.star7)
        stars[7] = v.findViewById(R.id.star8)
        stars[8] = v.findViewById(R.id.star9)
        stars[9] = v.findViewById(R.id.star10)

        val group = getGroup(groupPosition)

        if (type == QuestAdapterType.PERMIT) {
            for (j in 0..9) {
                stars[j]?.visibility = View.INVISIBLE
            }
        } else if (type == QuestAdapterType.VILLAGE) {
            for (j in 0 until group.stars) {
                stars[j]?.visibility = View.VISIBLE
            }
        } else {
            if (group.stars <= 10) {
                for (j in 0 until group.stars) {
                    stars[j]?.visibility = View.VISIBLE
                }
            } else {
                stars[0]?.visibility = View.VISIBLE
            }
        }

        questGroupTextView.text = group.name

        return v
    }

    override fun getChildView(i: Int, i1: Int, b: Boolean, view: View?,
                              viewGroup: ViewGroup?): View {
        val context = viewGroup?.context
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(
                R.layout.fragment_quest_expandablelist_child_item,
                viewGroup, false)

        val iv = v.findViewById<ImageView>(R.id.item_image)
        val questChildTextView = v.findViewById<TextView>(R.id.name_text)
        val goalTextView = v.findViewById<TextView>(R.id.goal)
        val keyTextView = v.findViewById<TextView>(R.id.key)
        val urgentTextView = v.findViewById<TextView>(R.id.urgent)

        val quest = getChild(i, i1) as Quest
        iv.setImageAsset(quest)

        questChildTextView.text = quest.name
        goalTextView.text = quest.goal

        if (quest.type == Quest.QUEST_TYPE_NONE) {
            keyTextView.visibility = View.GONE
            urgentTextView.visibility = View.GONE
        } else if (quest.type == Quest.QUEST_TYPE_KEY) {
            urgentTextView.visibility = View.GONE
            keyTextView.visibility = View.VISIBLE
        } else {
            urgentTextView.visibility = View.VISIBLE
            keyTextView.visibility = View.GONE
        }

        val questId = quest.id

        v.tag = questId
        v.setOnClickListener(QuestClickListener(context, questId))
        return v
    }
}