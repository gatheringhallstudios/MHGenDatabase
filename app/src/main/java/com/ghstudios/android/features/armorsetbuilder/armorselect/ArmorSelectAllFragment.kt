package com.ghstudios.android.features.armorsetbuilder.armorselect

import android.app.Activity
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ghstudios.android.features.armor.detail.ArmorSetDetailPagerActivity
import com.ghstudios.android.mhgendatabase.databinding.FragmentGenericExpandableListBinding

/**
 * Creates a fragment used to display the list of all armor
 * available to insert to an ArmorSetBuilder
 */
class ArmorSelectAllFragment : Fragment() {
    private lateinit var binding: FragmentGenericExpandableListBinding

    /**
     * ViewModel (anchored to parent)
     */
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(ArmorSelectViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGenericExpandableListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterItems = mutableListOf<ArmorGroup>()
        val adapter = ArmorExpandableListAdapter(adapterItems)
        binding.expandableListView.setAdapter(adapter)

        enableFilter {
            viewModel.setFilter(it)
        }

        viewModel.filteredArmor.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            adapterItems.clear()
            adapterItems.addAll(it)
            adapter.notifyDataSetChanged()

            adapter.onArmorSelected = {
                val intent = activity!!.intent
                intent.putExtra(ArmorSetDetailPagerActivity.EXTRA_ARMOR_ID, it.id)
                activity?.setResult(Activity.RESULT_OK, intent)
                activity?.finish()
            }
        })
    }


    private var previousListener: TextWatcher? = null

    fun enableFilter(onUpdate: (String) -> Unit) {
        val textField = binding.inputSearch

        textField.visibility = View.VISIBLE

        if (previousListener != null) {
            textField.removeTextChangedListener(previousListener)
        }

        previousListener = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = (s?.toString() ?: "").trim()
                onUpdate(value)
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        }
        textField.addTextChangedListener(previousListener)
    }
}
