package com.ghstudios.android.features.quests

import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.Quest
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.features.locations.LocationDetailPagerActivity

import com.ghstudios.android.ClickListeners.MonsterClickListener
import com.ghstudios.android.data.classes.MonsterToQuest
import com.ghstudios.android.mhgendatabase.databinding.FragmentQuestDetailBinding
import com.ghstudios.android.util.applyArguments
import com.ghstudios.android.util.setImageAsset

/**
 * Shows the main quest information.
 * This class's views are binded via Kotlin KTX extensions
 */
class QuestDetailFragment : Fragment() {

    companion object {
        private const val ARG_QUEST_ID = "QUEST_ID"

        @JvmStatic fun newInstance(questId: Long): QuestDetailFragment {
            return QuestDetailFragment().applyArguments {
                putLong(ARG_QUEST_ID, questId)
            }
        }
    }

    private lateinit var binding: FragmentQuestDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentQuestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(activity!!).get(QuestDetailViewModel::class.java)

        viewModel.quest.observe(viewLifecycleOwner, Observer { quest ->
            if (quest != null) updateUI(quest)
        })

        viewModel.monsters.observe(viewLifecycleOwner, Observer { monsters ->
            if (monsters != null) bindMonsters(monsters)
        })

        // Click listener for quest location
        binding.locationLayout.setOnClickListener { v ->
            // The id argument will be the Monster ID; CursorAdapter gives us this
            // for free
            val i = Intent(activity, LocationDetailPagerActivity::class.java)
            var id = v.tag as Long
            if (id > 100) id = id - 100
            i.putExtra(LocationDetailPagerActivity.EXTRA_LOCATION_ID, id)
            startActivity(i)
        }
    }

    /**
     * Renders the list of monsters in the Quest Detail.
     * Internally, it creates an adapter and then uses it to populate a linear layout.
     */
    private fun bindMonsters(monsters: List<MonsterToQuest>) {
        // Use adapter to manually populate a LinearLayout
        val adapter = MonsterToQuestListAdapter(context!!, monsters)
        for (i in 0 until adapter.count) {
            val v = adapter.getView(i, null, binding.monsterHabitatFragment)
            binding.monsterHabitatFragment.addView(v)
        }
    }

    private fun updateUI(mQuest: Quest) {
        // bind title bar
        with (binding.titlebar) {
            setIconDrawable(AssetLoader.loadIconFor(mQuest))
            setTitleText(mQuest.name)
        }

        with(binding) {
            goal.text = mQuest.goal

            hub.text = AssetLoader.localizeHub(mQuest.hub)
            level.text = mQuest.starString
            hrp.setValueText(mQuest.hrp.toString())
            reward.setValueText("" + mQuest.reward + "z")
            fee.setValueText("" + mQuest.fee + "z")

            location.text = mQuest.location?.name
            location.tag = mQuest.location?.id
            locationLayout.tag = mQuest.location?.id
            subquest.text = mQuest.subGoal
            subhrp.text = "" + mQuest.subHrp
            subreward.text = "" + mQuest.subReward + "z"
            description.text = mQuest.flavor

            // Get Location based on ID and set image thumbnail
            locationImage.setImageAsset(mQuest.location)
        }
    }

    /**
     * Internal adapter used to render the list of monsters shown in a quest
     */
    private class MonsterToQuestListAdapter(context: Context,
                                            items: List<MonsterToQuest>) : ArrayAdapter<MonsterToQuest>(context, 0, items) {

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            var view = view
            if (view == null) {
                val inflater = LayoutInflater.from(context)
                view = inflater.inflate(R.layout.fragment_quest_monstertoquest, parent, false)
            }
            val monsterToQuest = getItem(position)

            // Set up the text view
            val itemLayout = view!!.findViewById<LinearLayout>(R.id.listitem)
            val habitatLayout = view.findViewById<LinearLayout>(R.id.habitat_layout)
            val monsterImageView = view.findViewById<ImageView>(R.id.detail_monster_image)
            val monsterTextView = view.findViewById<TextView>(R.id.detail_monster_label)
            val unstableTextView = view.findViewById<TextView>(R.id.detail_monster_unstable)
            val startTextView = view.findViewById<TextView>(R.id.habitat_start)
            val travelTextView = view.findViewById<TextView>(R.id.habitat_travel)
            val endTextView = view.findViewById<TextView>(R.id.habitat_end)
            val hyperTextView = view.findViewById<TextView>(R.id.detail_monster_hyper)

            val cellMonsterText = monsterToQuest!!.monster!!.name

            if (monsterToQuest.isUnstable) {
                unstableTextView.visibility = View.VISIBLE
                unstableTextView.setText(R.string.unstable)
            } else
                unstableTextView.visibility = View.GONE

            if (monsterToQuest.isHyper) {
                hyperTextView.visibility = View.VISIBLE
                hyperTextView.setText(R.string.hyper)
            } else
                hyperTextView.visibility = View.GONE

            monsterTextView.text = cellMonsterText

            AssetLoader.setIcon(monsterImageView, monsterToQuest.monster!!)

            val habitat = monsterToQuest.habitat
            if (habitat != null) {
                val start = habitat.start
                val area = habitat.areas
                val rest = habitat.rest

                var areas = ""
                for (j in area!!.indices) {
                    areas += java.lang.Long.toString(area[j])
                    if (j != area.size - 1) {
                        areas += ", "
                    }
                }

                startTextView.text = java.lang.Long.toString(start)
                travelTextView.text = areas
                endTextView.text = java.lang.Long.toString(rest)
                habitatLayout.visibility = View.VISIBLE
            } else
                habitatLayout.visibility = View.GONE

            itemLayout.tag = monsterToQuest.monster!!.id
            itemLayout.setOnClickListener(MonsterClickListener(context,
                    monsterToQuest.monster!!.id))
            return view
        }
    }
}
