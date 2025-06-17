@file:JvmName("AssetRegistry")
package com.ghstudios.android

import android.content.res.Resources
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ghstudios.android.data.classes.ElementStatus
import com.ghstudios.android.mhgendatabase.R


/**
 * The AssetRegistry is used to create key-value maps between any specified types
 * @param T Type for Key
 * @param K Type for Value
 */

typealias AdderFun<T, K> = (name: T, value: K) -> Unit

class Registry<T, K>(val source: Map<T, K>) {
    operator fun get(key: T): K? {
        if (!source.containsKey(key)) {
            Log.w("AssetRegistry", "Value $key not found in registry")
        }
        return source[key]
    }

    fun get(key: T, default: K): K {
        return get(key) ?: default
    }
}

private fun <T, K> createRegistry(initLambda: (AdderFun<T, K>) -> Unit): Registry<T, K> {
    val mutableRegistry = HashMap<T, K>()
    initLambda { name: T, resId: K ->
        mutableRegistry[name] = resId
    }
    return Registry(mutableRegistry)
}

data class ElementStatusInfo(
    val name: String,
    @StringRes val tooltipText: Int,
    @DrawableRes val icon: Int
)

val ElementStatusInfoNone = ElementStatusInfo(ElementStatus.NONE.name, 0, R.color.transparent)

/**
 * A mapping from an element/status enumeration to a drawable resource
 */
val ElementRegistry = createRegistry<ElementStatus, ElementStatusInfo> { register ->
    fun registerElement(elementStatus: ElementStatus, @DrawableRes icon: Int, @StringRes tooltipText: Int) {
        register(elementStatus, ElementStatusInfo(elementStatus.name, tooltipText, icon))
    }
    register(ElementStatus.NONE, ElementStatusInfoNone)

    registerElement(ElementStatus.FIRE, R.drawable.element_fire, R.string.element_status_tooltip_fire)
    registerElement(ElementStatus.WATER, R.drawable.element_water, R.string.element_status_tooltip_water)
    registerElement(ElementStatus.THUNDER, R.drawable.element_thunder, R.string.element_status_tooltip_thunder)
    registerElement(ElementStatus.ICE, R.drawable.element_ice, R.string.element_status_tooltip_ice)
    registerElement(ElementStatus.DRAGON, R.drawable.element_dragon, R.string.element_status_tooltip_dragon)

    registerElement(ElementStatus.POISON, R.drawable.status_poison, R.string.element_status_tooltip_poison)
    registerElement(ElementStatus.PARALYSIS, R.drawable.status_paralysis, R.string.element_status_tooltip_paralysis)
    registerElement(ElementStatus.SLEEP, R.drawable.status_sleep, R.string.element_status_tooltip_sleep)
    registerElement(ElementStatus.BLAST, R.drawable.status_blastblight, R.string.element_status_tooltip_blastblight)
    registerElement(ElementStatus.MOUNT, R.drawable.status_mount, R.string.element_status_tooltip_mount)
    registerElement(ElementStatus.EXHAUST, R.drawable.status_exhaust, R.string.element_status_tooltip_exhaust)
    registerElement(ElementStatus.JUMP, R.drawable.status_jump, R.string.element_status_tooltip_jump)
    registerElement(ElementStatus.STUN, R.drawable.status_stun, R.string.element_status_tooltip_stun)
}
