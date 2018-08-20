package com.ghstudios.android.features.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ghstudios.android.*
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.ClickListeners.MonsterClickListener
import com.ghstudios.android.ClickListeners.QuestClickListener
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.mhgendatabase.R
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate


/**
 * A simple handler class used to create mappings for the Universal Search results.
 * Handler should override getImageResource or the T data object should implement the ITintedIcon
 * interface so that getImage() will handle the rest.
 * @param <T>
 */
abstract class ResultHandler<in T> {
    open fun getImageResource(obj: T): Int {
        return -1
    }

    abstract fun getName(obj: T): String
    abstract fun getType(obj: T): String
    abstract fun createListener(ctx: Context, obj: T): View.OnClickListener

    fun setImage(imgView:ImageView, obj: T) {
        val resource = this.getImageResource(obj)
        if(resource == -1){
            if(obj is ITintedIcon)
                AssetLoader.setIcon(imgView,obj)
        }
        else {
            imgView.setImageResource(getImageResource(obj))
            imgView.setColorFilter(0xFFFFFF)
        }
    }
}

private val handlers = mapOf(
        Monster::class.java to object : ResultHandler<Monster>() {
            override fun getName(obj: Monster) = obj.name
            override fun getType(obj: Monster) = "Monster"
            override fun createListener(ctx: Context, obj: Monster) = MonsterClickListener(ctx, obj.id)
        },

        Quest::class.java to object : ResultHandler<Quest>() {
            override fun getImageResource(q: Quest): Int {
                return when {
                    q.hunterType == 1 -> R.drawable.quest_cat
                    q.goalType == Quest.QUEST_GOAL_DELIVER -> R.drawable.quest_icon_green
                    q.goalType == Quest.QUEST_GOAL_CAPTURE -> R.drawable.quest_icon_grey
                    else -> R.drawable.quest_icon_red
                }
            }

            override fun getName(obj: Quest) = obj.name
            override fun getType(obj: Quest) = "Quest"
            override fun createListener(ctx: Context, obj: Quest) = QuestClickListener(ctx, obj.id)
        },

        SkillTree::class.java to object : ResultHandler<SkillTree>() {
            override fun getImageResource(obj: SkillTree) = R.drawable.icon_bomb
            override fun getName(obj: SkillTree) = obj.name
            override fun getType(obj: SkillTree) = "Skill Tree"
            override fun createListener(ctx: Context, obj: SkillTree) = SkillClickListener(ctx, obj.id)
        },

        Item::class.java to  object : ResultHandler<Item>() {
            override fun getName(obj: Item) = obj.name ?: ""
            override fun getType(obj: Item) = obj.type ?: "Item"
            override fun createListener(ctx: Context, obj: Item) = ItemClickListener(ctx, obj)
        }
)

class SearchResultAdapterDelegate: AbsListItemAdapterDelegate<Any, Any, SearchResultAdapterDelegate.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        // todo: IconLabelTextView instead? We're using the old one here
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.fragment_searchresult_listitem, parent, false)
        return ViewHolder(v)
    }

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return true
    }

    override fun onBindViewHolder(item: Any, viewHolder: ViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.result_image)
        private val nameView = itemView.findViewById<TextView>(R.id.result_name)
        private val typeView = itemView.findViewById<TextView>(R.id.result_type)

        fun bindView(result: Any) {
            val originalClass = result!!.javaClass

            if (!handlers.containsKey(originalClass)) {
                // Not expected, so marked as a runtime exception
                throw RuntimeException(
                        "Could not find handler for class " + originalClass.name)
            }

            val handler = handlers[originalClass] as ResultHandler<Any>

            handler.setImage(imageView, result)

            nameView.text = handler.getName(result)
            typeView.text = handler.getType(result)

            itemView.setOnClickListener(handler.createListener(itemView.context, result))
        }
    }
}