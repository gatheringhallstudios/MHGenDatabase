package com.ghstudios.android.features.items.detail

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.AssetLoader

import com.ghstudios.android.data.classes.Gathering
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.LocationClickListener
import com.ghstudios.android.SectionArrayAdapter

/**
 * Fragment used to display locations where you can gather a specific item
 */
class ItemLocationFragment : ListFragment() {
    /**
     * Returns the viewmodel of this subfragment, anchored to the parent activity
     */
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(ItemDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo: Refactor this class not to use a cursor. Returning a cursor from a viewmodel can be a source of bugs
        viewModel.gatherData.observe(viewLifecycleOwner, Observer { data ->
            if (data != null) {
                val adapter = GatheringListCursorAdapter(this.context!!, data)
                listAdapter = adapter
            }
        })
    }

    /**
     * Internal adapter to render the list of item gather locations
     */
    private class GatheringListCursorAdapter(
            context: Context,
            gatheringData: List<Gathering>
    ) : SectionArrayAdapter<Gathering>(context, gatheringData, R.layout.listview_generic_header) {

        override fun getGroupName(item: Gathering): String {
            return item.rank + " " + item.location?.name
        }

        override fun newView(context: Context, item: Gathering, parent: ViewGroup): View {
            // Use a layout inflater to get a row view
            val inflater = LayoutInflater.from(context)
            return inflater.inflate(R.layout.fragment_item_location_listitem,
                    parent, false)
        }

        override fun bindView(view: View, context: Context, gathering: Gathering) {
            val itemLayout = view.findViewById<View>(R.id.listitem)

            val mapTextView = view.findViewById<TextView>(R.id.map)
            val methodTextView = view.findViewById<TextView>(R.id.method)
            val rateTextView = view.findViewById<TextView>(R.id.rate)
            val amountTextView = view.findViewById<TextView>(R.id.amount)

            mapTextView.text = gathering.area
            methodTextView.text = AssetLoader.localizeGatherNodeFull(gathering)
            rateTextView.text = gathering.rate.toInt().toString() + "%"
            amountTextView.text = "x" + gathering.quantity

            itemLayout.tag = gathering.location!!.id
            itemLayout.setOnClickListener(LocationClickListener(context,
                    gathering.location!!.id))
        }
    }

}
