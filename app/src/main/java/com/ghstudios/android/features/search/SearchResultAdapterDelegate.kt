package com.ghstudios.android.features.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ghstudios.android.ClickListeners.ItemClickListener
import com.ghstudios.android.ClickListeners.MonsterClickListener
import com.ghstudios.android.ClickListeners.QuestClickListener
import com.ghstudios.android.ClickListeners.SkillClickListener
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.getAssetDrawable
import com.ghstudios.android.getDrawableCompat
import com.ghstudios.android.mhgendatabase.R
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate


/**
 * A simple handler class used to create mappings for the Universal Search results.
 * Override EITHER getImagePath or getImageResource so that getImage() will handle the rest.
 * @param <T>
 */
abstract class ResultHandler<in T> {
    open fun getImagePath(obj: T): String? {
        return null
    }

    open fun getImageResource(obj: T): Int {
        return -1
    }

    abstract fun getName(obj: T): String
    abstract fun getType(obj: T): String
    abstract fun createListener(ctx: Context, obj: T): View.OnClickListener

    fun getImage(obj: T, ctx: Context): Drawable? {
        val imagePath = this.getImagePath(obj)
        if (imagePath != null) {
            return ctx.getAssetDrawable(imagePath)
        } else {
            return ctx.getDrawableCompat(this.getImageResource(obj))
        }
    }
}

private val handlers = mapOf(
        Monster::class.java to object : ResultHandler<Monster>() {
            override fun getImagePath(obj: Monster): String? {
                return "icons_monster/" + obj.fileLocation
            }

            override fun getName(obj: Monster) = obj.name

            override fun getType(obj: Monster): String {
                return "Monster"
            }

            override fun createListener(ctx: Context, obj: Monster): View.OnClickListener {
                return MonsterClickListener(ctx, obj.id)
            }
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

            override fun getType(obj: Quest): String {
                return "Quest"
            }

            override fun createListener(ctx: Context, obj: Quest): View.OnClickListener {
                return QuestClickListener(ctx, obj.id)
            }
        },

        SkillTree::class.java to object : ResultHandler<SkillTree>() {
            override fun getImagePath(skill: SkillTree) = "icons_items/Bomb-White.png"
            override fun getName(obj: SkillTree) = obj.name

            override fun getType(obj: SkillTree): String {
                return "Skill Tree"
            }

            override fun createListener(ctx: Context, obj: SkillTree): View.OnClickListener {
                return SkillClickListener(ctx, obj.id)
            }
        },

        Item::class.java to  object : ResultHandler<Item>() {
            override fun getImagePath(item: Item) =item.itemImage
            override fun getName(obj: Item) = obj.name

            override fun getType(obj: Item): String {
                var type: String? = obj.type
                if (type == null || type == "") {
                    // todo: localize, but item types should be localized too
                    type = "Item"
                }
                return type
            }

            override fun createListener(ctx: Context, obj: Item): View.OnClickListener {
                return ItemClickListener(ctx, obj)
            }
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
        val imageView = itemView.findViewById<ImageView>(R.id.result_image)
        val nameView = itemView.findViewById<TextView>(R.id.result_name)
        val typeView = itemView.findViewById<TextView>(R.id.result_type)

        fun bindView(result: Any) {
            val originalClass = result!!.javaClass

            if (!handlers.containsKey(originalClass)) {
                // Not expected, so marked as a runtime exception
                throw RuntimeException(
                        "Could not find handler for class " + originalClass.name)
            }

            val handler = handlers[originalClass] as ResultHandler<Any>

            val image = handler.getImage(result, itemView.context)

            imageView.setImageDrawable(image)
            nameView.text = handler.getName(result)
            typeView.text = handler.getType(result)

            itemView.setOnClickListener(handler.createListener(itemView.context, result))
        }
    }
}