package com.ghstudios.android

import android.app.Application
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.mhgendatabase.R
import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.util.getDrawableCompat

/**
 * A static class used to load icons for various database objects.
 * Initialized when the application is loaded.
 */
object AssetLoader {
    private lateinit var application: Application

    private val ctx get() = application.applicationContext

    fun bindApplication(app: Application) {
        application = app
    }

    /**
     * Loads a tinted icon using an ITintedIcon, returning it as a Drawable
     */
    @JvmStatic
    fun loadIconFor(item: ITintedIcon): Drawable? {
        var resId = MHUtils.getDrawableId(ctx, item.getIconResourceString())
        if (resId <= 0) {
            resId = R.drawable.icon_quest_mark
        }

        val image = ContextCompat.getDrawable(ctx, resId)

        val arrId = item.getColorArrayId()
        if (arrId == 0) {
            return image
        }

        // Tint the icon - we have an array id
        val arr = MHUtils.getIntArray(ctx, arrId)
        val color = arr[item.getIconColorIndex()]
        return image?.mutate()?.apply {
            setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    /**
     * Loads an icon for the given ElementStatus.
     * todo: is it nonsensical to make an enum into an ITintedIcon?
     */
    @JvmStatic
    fun loadIconFor(element: ElementStatus): Drawable? {
        val resId = ElementRegistry.get(element, default=R.color.transparent)
        return ctx.getDrawableCompat(resId)
    }

    @JvmStatic
    fun setIcon(iv: ImageView, item: ITintedIcon?) {
        if (item == null) {
            iv.setImageDrawable(null)
        } else {
            iv.setImageDrawable(loadIconFor(item))
        }
    }

    /**
     * Returns the rarity as a display string.
     * Rarity of value 11 is turned into "X", everything else is just the string version
     */
    @JvmStatic fun localizeRarity(rarity: Int) = when (rarity) {
        11 -> "X"
        else -> rarity.toString()
    }

    /**
     * Returns the rarity label name, in the form of "Rare X".
     * If rarity is 11, it is rendered as "Rare X"
     */
    @JvmStatic fun localizeRarityLabel(rarity: Int)
            = application.getString(R.string.value_rare, localizeRarity(rarity))

    /**
     * Returns a localized human readable weapon name for a weapon type
     */
    @JvmStatic fun localizeWeaponType(type: String) = ctx.getString(when (type) {
        Weapon.GREAT_SWORD -> R.string.type_weapon_greatsword
        Weapon.LONG_SWORD -> R.string.type_weapon_longsword
        Weapon.SWORD_AND_SHIELD -> R.string.type_weapon_swordandshield
        Weapon.DUAL_BLADES -> R.string.type_weapon_dualblades
        Weapon.HAMMER -> R.string.type_weapon_hammer
        Weapon.HUNTING_HORN -> R.string.type_weapon_huntinghorn
        Weapon.LANCE -> R.string.type_weapon_lance
        Weapon.GUNLANCE -> R.string.type_weapon_gunlance
        Weapon.SWITCH_AXE -> R.string.type_weapon_switchaxe
        Weapon.CHARGE_BLADE -> R.string.type_weapon_chargeblade
        Weapon.INSECT_GLAIVE -> R.string.type_weapon_insectglaive
        Weapon.LIGHT_BOWGUN -> R.string.type_weapon_lightbowgun
        Weapon.HEAVY_BOWGUN -> R.string.type_weapon_heavybowgun
        Weapon.BOW -> R.string.type_weapon_bow
        else -> R.string.type_weapon
    })

    /**
     * Returns a localized string that represents the hub type,
     * aka Village/Guild/Event/Permit
     */
    @JvmStatic fun localizeHub(hub: QuestHub?) = when (hub) {
        QuestHub.VILLAGE -> ctx.getString(R.string.type_hub_village)
        QuestHub.GUILD -> ctx.getString(R.string.type_hub_guild)
        QuestHub.EVENT -> ctx.getString(R.string.type_hub_event)
        QuestHub.ARENA -> ctx.getString(R.string.type_hub_arena)
        QuestHub.PERMIT -> ctx.getString(R.string.type_hub_permit)
        null -> "NULL"
    }

    /**
     * Returns a localized string that represents the gathering node's site modifier.
     * For example, Fixed, Rare, Common
     */
    @JvmStatic fun localizeGatherModifier(gather: Gathering) = when {
        gather.isFixed -> ctx.getString(R.string.item_gather_fixed)
        gather.isRare -> ctx.getString(R.string.item_gather_rare)
        else -> ctx.getString(R.string.item_gather_common)
    }

    /**
     * Returns a localized string that represents the gathering type.
     * For example, Mine [Fixed] and Gather [Rare].
     * TODO: Localize the first part...
     */
    @JvmStatic fun localizeGatherNodeFull(gather: Gathering): String {
        val modifier = localizeGatherModifier(gather)
        return ctx.getString(R.string.item_gather_full, gather.site, modifier)
    }

    @JvmStatic fun localizeRank(rank: Rank) = when (rank) {
        Rank.LOW -> ctx.getString(R.string.rank_lr)
        Rank.HIGH -> ctx.getString(R.string.rank_hr)
        Rank.G -> ctx.getString(R.string.rank_g)
        Rank.ANY -> ctx.getString(R.string.rank_any)
    }
}