package com.ghstudios.android.features.palicos

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.PalicoArmor

import com.ghstudios.android.mhgendatabase.R


/**
 * Created by Joseph on 7/9/2016.
 */
class PalicoArmorListFragment : ListFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this).get(PalicoArmorListViewModel::class.java)
        viewModel.armors.observe(viewLifecycleOwner, Observer { setupAdapter(it) })
        viewModel.loadList()
    }

    fun setupAdapter(armors: List<PalicoArmor>?){
        listAdapter = ArmorAdapter(context!!,armors!!)
    }

    class ArmorAdapter(c: Context,items: List<PalicoArmor>) : ArrayAdapter<PalicoArmor>(c,android.R.layout.simple_list_item_1,items){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v:View
            if(convertView == null)
                v = LayoutInflater.from(context).inflate(R.layout.fragment_palico_armor_listitem,parent,false)
            else
                v = convertView
            val nameTextview:TextView = v.findViewById(R.id.name_text)
            val defenseTextView:TextView = v.findViewById(R.id.defense)
            val imageView: ImageView = v.findViewById(R.id.item_image)

            nameTextview.text = getItem(position)?.item?.name
            defenseTextView.text = getItem(position)?.defense?.toString()

            AssetLoader.setIcon(imageView,getItem(position)?.item!!)

            return v
        }
    }

}
