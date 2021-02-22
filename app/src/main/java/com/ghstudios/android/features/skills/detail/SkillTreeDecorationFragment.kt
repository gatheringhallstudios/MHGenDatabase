package com.ghstudios.android.features.skills.detail

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import com.ghstudios.android.AssetLoader
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.ClickListeners.DecorationClickListener
import com.ghstudios.android.util.applyArguments

/**
 * Fragment used to display a list of decorations that have at least one point in a particular skill.
 */
class SkillTreeDecorationFragment : ListFragment() {
    companion object {
        private const val ARG_SKILL = "SKILLTREE_SKILL"

        @JvmStatic fun newInstance(skill: Long): SkillTreeDecorationFragment {
            return SkillTreeDecorationFragment().applyArguments {
                putLong(ARG_SKILL, skill)
            }
        }
    }

    /**
     * ViewModel belonging to the parent activity
     */
    private val parentViewModel by lazy {
        ViewModelProvider(activity!!).get(SkillDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // required for ListFragment

        val adapter = ItemToSkillTreeListAdapter(context!!)
        listAdapter = adapter

        parentViewModel.decorationSkillPoints.observe(this, Observer {
            if (it != null) adapter.updateItems(it)
        })
    }

    /**
     * Internal adapter used to render decoration list items.
     */
    private class ItemToSkillTreeListAdapter(context: Context)
        : ArrayAdapter<ItemToSkillTree>(context, 0) {

        fun updateItems(items: List<ItemToSkillTree>) {
            setNotifyOnChange(false)
            clear()
            addAll(items)
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = when (convertView) {
                null -> {
                    val inflater = LayoutInflater.from(context)
                    inflater.inflate(R.layout.listitem_skill_item, parent, false)
                }
                else -> convertView
            }

            // Get the skill for the current row
            val skill = getItem(position)

            // Set up the text view
            val root = view.findViewById<View>(R.id.listitem) as LinearLayout
            val skillItemImageView = view.findViewById<View>(R.id.item_image) as ImageView
            val skillItemTextView = view.findViewById<View>(R.id.item) as TextView
            val skillAmtTextView = view.findViewById<View>(R.id.amt) as TextView

            val nameText = skill!!.item!!.name
            val amtText = "" + skill.points

            skillItemTextView.text = nameText
            skillAmtTextView.text = amtText

            AssetLoader.setIcon(skillItemImageView, skill.item)

            root.tag = skill.item!!.id
            root.setOnClickListener(DecorationClickListener(context, skill.item!!.id))

            return view
        }
    }
}
