package com.ghstudios.android.features.quests

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.ClickListeners.MonsterClickListener
import com.ghstudios.android.SectionArrayAdapter
import com.ghstudios.android.data.classes.Gathering
import com.ghstudios.android.data.classes.HuntingReward
import com.ghstudios.android.mhgendatabase.R

class QuestItemFragment : ListFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(activity!!).get(QuestDetailViewModel::class.java)
        viewModel.gatherings.observe(viewLifecycleOwner, Observer<List<Gathering>>{this.populateGatherings(it)})
        viewModel.huntingRewards.observe(viewLifecycleOwner, Observer<List<HuntingReward>>{this.populateHuntingRewards(it)})
    }

    private fun populateGatherings(gatherings:List<Gathering>?){
        listAdapter = GatheringAdapter(this.context!!,gatherings!!)
    }

    private fun populateHuntingRewards(rewards:List<HuntingReward>?){
        listAdapter = HuntingRewardAdapter(this.context!!,rewards!!)
    }

    private class GatheringAdapter(context: Context, items:List<Gathering>) : SectionArrayAdapter<Gathering>(context,items,R.layout.listview_generic_header){

        override fun getGroupName(item: Gathering?): String {
            return item?.item?.name ?: ""
        }

        override fun newView(context: Context?, item: Gathering?, parent: ViewGroup?): View {
            return LayoutInflater.from(context!!).inflate(R.layout.fragment_item_location_listitem,parent,false)
        }

        override fun bindView(view: View?, context: Context?, gathering: Gathering?) {
            if(gathering == null || view == null) return

            val mapTextView = view.findViewById<TextView>(R.id.map)
            val methodTextView = view.findViewById<TextView>(R.id.method)
            val rateTextView = view.findViewById<TextView>(R.id.rate)
            val amountTextView = view.findViewById<TextView>(R.id.amount)
            val rate = gathering.rate.toLong()

            mapTextView.text = gathering.area
            methodTextView.text = AssetLoader.localizeGatherNodeFull(gathering)
            amountTextView.text = "x" + gathering.quantity.toString()
            rateTextView.text = rate.toString() + "%"
        }
    }
    private class HuntingRewardAdapter(context: Context, items:List<HuntingReward>) : SectionArrayAdapter<HuntingReward>(context,items,R.layout.listview_generic_header){

        override fun getGroupName(item: HuntingReward?): String {
            return item?.item?.name ?: ""
        }

        override fun newView(context: Context?, item: HuntingReward?, parent: ViewGroup?): View {
            return LayoutInflater.from(context!!).inflate(R.layout.fragment_item_monster_listitem,parent,false)
        }

        override fun bindView(view: View?, context: Context?, hr: HuntingReward?) {
            if(hr == null || view == null) return

            val itemLayout = view.findViewById<RelativeLayout>(R.id.listitem)

            val rankTextView = view.findViewById<TextView>(R.id.rank)
            val monsterTextView = view.findViewById<TextView>(R.id.monster)
            val methodTextView = view.findViewById<TextView>(R.id.method)
            val amountTextView = view.findViewById<TextView>(R.id.amount)
            val percentageTextView = view.findViewById<TextView>(R.id.percentage)
            val monsterImageView = view.findViewById<ImageView>(R.id.monster_image)

            val cellAmountText = hr.stackSize
            val cellPercentageText = hr.percentage

            rankTextView.text = hr.rank
            monsterTextView.text = hr.monster?.name
            methodTextView.text = hr.condition
            amountTextView.text = "x$cellAmountText"

            val percent = "$cellPercentageText%"
            percentageTextView.text = percent

            itemLayout.tag = hr.monster?.id
            itemLayout.setOnClickListener(MonsterClickListener(context,
                    hr.monster!!.id))

            AssetLoader.setIcon(monsterImageView, hr.monster!!)
        }
    }
}