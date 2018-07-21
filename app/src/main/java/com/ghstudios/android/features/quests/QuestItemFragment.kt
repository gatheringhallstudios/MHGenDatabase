package com.ghstudios.android.features.quests

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghstudios.android.SectionArrayAdapter
import com.ghstudios.android.data.classes.Gathering
import com.ghstudios.android.mhgendatabase.R

class QuestItemFragment : ListFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(activity!!).get(QuestDetailViewModel::class.java)
        viewModel.gatherings.observe(this,Observer<List<Gathering>>{this.populateGatherings(it)})
    }

    private fun populateGatherings(gatherings:List<Gathering>?){
        listAdapter = GatheringAdapter(this.context!!,gatherings!!)
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
            val rate = gathering.rate.toLong()

            mapTextView.text = gathering.area
            methodTextView.text = "x" + gathering.quantity.toString()
            rateTextView.text = java.lang.Long.toString(rate) + "%"
        }
    }

}