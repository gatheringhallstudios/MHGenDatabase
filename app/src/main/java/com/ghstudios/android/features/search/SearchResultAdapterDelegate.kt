package com.ghstudios.android.features.search

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ghstudios.android.*
import com.ghstudios.android.ClickListeners.*
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
        if (resource == -1) {
            if (obj is ITintedIcon)
                AssetLoader.setIcon(imgView, obj)
        } else {
            imgView.setImageResource(getImageResource(obj))
        }
    }
}

/**
 * Creates a mapping of data type -> handler. Context is used to retrieve the localized type names
 */
private fun createHandlers(ctx: Context) = mapOf(
        Location::class.java to object : ResultHandler<Location>() {
            override fun getName(obj: Location) = obj.name ?: ""
            override fun getType(obj : Location) = ctx.getString(R.string.type_location)
            override fun createListener(ctx: Context, obj: Location) = LocationClickListener(ctx, obj.id)
        },

        Monster::class.java to object : ResultHandler<Monster>() {
            override fun getName(obj: Monster) = obj.name
            override fun getType(obj: Monster) = ctx.getString(R.string.type_monster)
            override fun createListener(ctx: Context, obj: Monster) = MonsterClickListener(ctx, obj.id)
        },

        Quest::class.java to object : ResultHandler<Quest>() {
            override fun getName(obj: Quest) = obj.name
            override fun getType(obj: Quest) = AssetLoader.localizeHub(obj.hub)
            override fun createListener(ctx: Context, obj: Quest) = QuestClickListener(ctx, obj.id)
        },

        SkillTree::class.java to object : ResultHandler<SkillTree>() {
            override fun getImageResource(obj: SkillTree) = R.drawable.icon_bomb
            override fun getName(obj: SkillTree) = obj.name ?: ""
            override fun getType(obj: SkillTree) = ctx.getString(R.string.type_skill_tree)
            override fun createListener(ctx: Context, obj: SkillTree) = SkillClickListener(ctx, obj.id)
        },

        Decoration::class.java to  object : ResultHandler<Decoration>() {
            override fun getName(obj: Decoration) = obj.name ?: ""
            override fun getType(obj: Decoration) = ctx.getString(R.string.type_decoration)
            override fun createListener(ctx: Context, obj: Decoration) = ItemClickListener(ctx, obj)
        },
        
        ArmorFamilyBase::class.java to object : ResultHandler<ArmorFamilyBase>() {
            override fun getName(obj: ArmorFamilyBase) = obj.name ?: ""
            override fun getType(obj: ArmorFamilyBase) = when (obj.hunterType) {
                Armor.ARMOR_TYPE_BLADEMASTER -> ctx.getString(R.string.type_armor_set_blade)
                Armor.ARMOR_TYPE_GUNNER -> ctx.getString(R.string.type_armor_set_gunner)
                else -> ctx.getString(R.string.type_armor_set)
            }
            override fun createListener(ctx: Context, obj: ArmorFamilyBase) = ArmorClickListener(ctx, obj)
        },
        
        Armor::class.java to object : ResultHandler<Armor>() {
            override fun getName(obj: Armor) = obj.name ?: ""
            override fun getType(obj: Armor) = ctx.getString(R.string.type_armor)
            override fun createListener(ctx: Context, obj: Armor) = ArmorClickListener(ctx, obj)
        },

        Item::class.java to  object : ResultHandler<Item>() {
            override fun getName(obj: Item) = obj.name ?: ""
            override fun getType(obj: Item) = when (obj.type) {
                ItemType.ITEM -> ctx.getString(R.string.type_item)
                ItemType.WEAPON -> ctx.getString(R.string.type_weapon)
                ItemType.ARMOR -> ctx.getString(R.string.type_armor)
                ItemType.PALICO_WEAPON -> ctx.getString(R.string.type_palico_weapon)
                ItemType.PALICO_ARMOR -> ctx.getString(R.string.type_palico_armor)
                ItemType.DECORATION -> ctx.getString(R.string.type_decoration)
                ItemType.MATERIAL -> ctx.getString(R.string.type_material)
            }
            override fun createListener(ctx: Context, obj: Item) = ItemClickListener(ctx, obj)
        }
)

/**
 * Creates a new adapter delegate to display search results.
 * Context is used to cache and retrieve translations.
 */
class SearchResultAdapterDelegate(ctx: Context): AbsListItemAdapterDelegate<Any, Any, SearchResultAdapterDelegate.ViewHolder>() {
    private val handlers = createHandlers(ctx.applicationContext)

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
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


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView.findViewById<ImageView>(R.id.result_image)
        private val nameView = itemView.findViewById<TextView>(R.id.result_name)
        private val typeView = itemView.findViewById<TextView>(R.id.result_type)

        fun bindView(result: Any) {
            val originalClass = result.javaClass

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