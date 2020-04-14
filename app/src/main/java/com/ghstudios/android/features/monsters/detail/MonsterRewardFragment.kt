package com.ghstudios.android.features.monsters.detail

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
import com.ghstudios.android.ClickListeners.BasicItemClickListener
import com.ghstudios.android.SectionArrayAdapter
import com.ghstudios.android.data.classes.HuntingReward
import com.ghstudios.android.loader.HuntingRewardListCursorLoader
import com.ghstudios.android.mhgendatabase.R
import java.io.IOException

class MonsterRewardFragment : ListFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(activity!!).get(MonsterDetailViewModel::class.java)

        val rank = this.arguments?.get(ARG_RANK)
        if(rank == HuntingRewardListCursorLoader.RANK_LR) viewModel.rewardLRData.observe(this, Observer<List<HuntingReward>>{this.populateRewards(it)})
        if(rank == HuntingRewardListCursorLoader.RANK_HR) viewModel.rewardHRData.observe(this, Observer<List<HuntingReward>>{this.populateRewards(it)})
        if(rank == HuntingRewardListCursorLoader.RANK_G) viewModel.rewardGData.observe(this, Observer<List<HuntingReward>>{this.populateRewards(it)})
    }

    private fun populateRewards(rewards:List<HuntingReward>?){
        listAdapter = RewardAdapter(this.context!!,rewards!!)
    }


    private class RewardAdapter(context: Context, items:List<HuntingReward>) : SectionArrayAdapter<HuntingReward>(context,items,R.layout.listview_generic_header){

        override fun getGroupName(item: HuntingReward?): String {
            return item?.condition ?: ""
        }

        override fun newView(context: Context?, item: HuntingReward?, parent: ViewGroup?): View {
            return LayoutInflater.from(context!!).inflate(R.layout.fragment_monster_reward_listitem,parent,false)
        }

        override fun bindView(view: View?, context: Context?, huntingReward: HuntingReward?) {
            if(view == null || huntingReward == null) return

            val itemLayout = view.findViewById<View>(R.id.listitem) as RelativeLayout
            val itemImageView = view.findViewById<View>(R.id.item_image) as ImageView

            val itemTextView = view.findViewById<View>(R.id.item) as TextView
            val amountTextView = view.findViewById<View>(R.id.amount) as TextView
            val percentageTextView = view.findViewById<View>(R.id.percentage) as TextView

            val cellItemText = huntingReward.item!!.name
            val cellAmountText = huntingReward.stackSize
            val cellPercentageText = huntingReward.percentage


            itemTextView.text = cellItemText
            amountTextView.text = "x$cellAmountText"

            val percent = "$cellPercentageText%"
            percentageTextView.text = percent

            AssetLoader.setIcon(itemImageView,huntingReward.item!!)

            itemLayout.tag = huntingReward.item!!.id
            itemLayout.setOnClickListener(BasicItemClickListener(context, huntingReward.item!!.id))
        }
    }

    companion object {
        private val ARG_MONSTER_ID = "MONSTER_ID"
        private val ARG_RANK = "RANK"

        @JvmStatic fun newInstance(monsterId: Long, rank: String): MonsterRewardFragment {
            val args = Bundle()
            args.putLong(ARG_MONSTER_ID, monsterId)
            args.putString(ARG_RANK, rank)
            val f = MonsterRewardFragment()
            f.arguments = args
            return f
        }
    }

}
