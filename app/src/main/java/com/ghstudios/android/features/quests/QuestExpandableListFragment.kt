package com.ghstudios.android.features.quests

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView

import com.ghstudios.android.data.database.DataManager
import com.ghstudios.android.mhgendatabase.R

// collection of all possible stars
private val guildStars = arrayOf("1", "2", "3", "4", "5", "6", "7", "11", "12", "13", "14")

private val village = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
private val guild = arrayOf("1", "2", "3", "4", "5", "6", "7", "G1", "G2", "G3", "G4")
private val event = arrayOf("1", "2", "3", "4", "5", "6", "7", "G1", "G2", "G3", "G4")

/**
 * Pieced together from: Android samples:
 * com.example.android.apis.view.ExpandableList1
 * http://androidword.blogspot.com/2012/01/how-to-use-expandablelistview.html
 * http://stackoverflow.com/questions/6938560/android-fragments-setcontentview-
 * alternative
 * http://stackoverflow.com/questions/6495898/findviewbyid-in-fragment-android
 */
class QuestExpandableListFragment : Fragment() {
    companion object {
        private val ARG_HUB = "QUEST_HUB"

        @JvmStatic fun newInstance(hub: String): QuestExpandableListFragment {
            val args = Bundle()
            args.putString(ARG_HUB, hub)
            val f = QuestExpandableListFragment()
            f.arguments = args
            return f
        }
    }


    private var mHub: String? = null
    private lateinit var groups: List<QuestGroup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHub = arguments?.getString(ARG_HUB)
        populateList()
    }

    private fun populateList() {
        val dataManager = DataManager.get()
        val allQuests = dataManager.queryQuestArrayHub(mHub!!).filter {
            it.stars != "0" // no zero stars (todo: filter in data manager?)
        }

        if (mHub == "Permit") {
            // Permit quests group by monster instead
            val groupedQuests = allQuests.groupBy { it.permitMonsterId }
            val monsters = dataManager.questDeviantMonsterNames()
            groups = groupedQuests.values.withIndex().map {
                val idx = it.index
                val quests = it.value
                QuestGroup(monsters[idx], quests)
            }
        } else {
            val labelMap = when (mHub) {
                "Village" -> village.zip(village).toMap() // village maps to self
                "Guild" -> guildStars.zip(guild).toMap()
                else -> guildStars.zip(event).toMap()
            }

            val groupedQuests = allQuests.groupBy { it.stars }

            groups = groupedQuests.map {
                val stars = it.key
                val quests = it.value
                QuestGroup(labelMap[stars] ?: "", quests)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_expandable_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val elv = view.findViewById<ExpandableListView>(R.id.expandableListView)

        val type = when (mHub) {
            "Village" -> QuestAdapterType.VILLAGE
            "Permit" -> QuestAdapterType.PERMIT
            else -> QuestAdapterType.GUILD
        }

        elv.setAdapter(QuestListExpandableAdapter(groups, type))
    }

}