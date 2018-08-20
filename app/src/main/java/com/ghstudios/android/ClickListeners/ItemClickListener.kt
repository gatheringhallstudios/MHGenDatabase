package com.ghstudios.android.ClickListeners

import android.content.Context
import android.view.View

import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.ItemType

/**
 * A proxy listener that internally uses an actual listener for general item pages.
 * Created by Carlos on 1/22/2017.
 */
class ItemClickListener(context: Context, type: ItemType, id: Long?) : View.OnClickListener {
    internal var innerListener: View.OnClickListener

    init {
        innerListener = constructTrueListener(context, type, id)
    }

    constructor(context: Context, item: Item) : this(context, item.type, item.id) {}

    private fun constructTrueListener(c: Context, type: ItemType, id: Long?): View.OnClickListener {
        return when (type) {
            ItemType.WEAPON -> WeaponClickListener(c, id)
            ItemType.ARMOR -> ArmorClickListener(c, id, false)
            ItemType.DECORATION -> DecorationClickListener(c, id)
            ItemType.MATERIAL -> MaterialClickListener(c, id)
            ItemType.PALICO_WEAPON -> PalicoWeaponClickListener(c, id)

            // todo: add a page for palico armor
            ItemType.PALICO_ARMOR -> BasicItemClickListener(c, id)

            ItemType.ITEM -> BasicItemClickListener(c, id)
        }
    }

    override fun onClick(v: View) {
        innerListener.onClick(v)
    }
}
