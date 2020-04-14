package com.ghstudios.android

import android.app.Application
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.util.Log
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

    private const val TAG = "MHAssetLoader"

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
     * Returns a localized string of the gathering site for the given Gathering object.
     */
    @JvmStatic fun localizeGatherSite(gather: Gathering): String {
        val resId = when (gather.site) {
            "Bug" -> R.string.gather_site_bug
            "Fishing (Burst Bait)" -> R.string.gather_site_fishing_burst
            "Fishing (Goldenfish Bait)" -> R.string.gather_site_fishing_goldenfish
            "Fishing (Mega Fishing Fly)" -> R.string.gather_site_fishing_megafly
            "Fishing (No Bait)" -> R.string.gather_site_fishing_none
            "Fishing (Sushifish Bait)" -> R.string.gather_site_fishing_sushi
            "Gather" -> R.string.gather_site_gather
            "Mine" -> R.string.gather_site_mine
            else -> 0
        }

        if (resId == 0) {
            Log.e(TAG, "Unknown gathering site ${gather.site}, not localized")
            return gather.site ?: ""
        }

        return ctx.getString(resId)
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
     * For example, [Fixed] Mine and [Rare] Gather.
     */
    @JvmStatic fun localizeGatherNodeFull(gather: Gathering): String {
        if ("Fishing" in (gather.site ?: "")) {
            return localizeGatherSite(gather)
        } else {
            val site = AssetLoader.localizeGatherSite(gather)
            val modifier = localizeGatherModifier(gather)
            return ctx.getString(R.string.item_gather_full, site, modifier)
        }
    }

    @JvmStatic fun localizeRank(rank: Rank) = when (rank) {
        Rank.LOW -> ctx.getString(R.string.rank_lr)
        Rank.HIGH -> ctx.getString(R.string.rank_hr)
        Rank.G -> ctx.getString(R.string.rank_g)
        Rank.ANY -> ctx.getString(R.string.rank_any)
    }

    /**
     * Localizes a gunlance's shelling type string
     */
    @JvmStatic fun localizeWeaponShelling(type: String?): String {
        if (type == null) return ""

        val parts = type.split(' ')
        val name = when(parts.firstOrNull()) {
            "Wide" -> ctx.getString(R.string.shelling_wide)
            "Normal" -> ctx.getString(R.string.shelling_normal)
            "Long" -> ctx.getString(R.string.shelling_long)
            else -> parts.firstOrNull() ?: ""
        }

        return name + " " + parts.subList(1, parts.size).joinToString(" ")
    }

    /**
     * Localizes a weapon's phial type
     */
    @JvmStatic fun localizeWeaponPhialType(type: String?) = when (type) {
        "Power" -> ctx.getString(R.string.phial_power)
        "Poison" -> ctx.getString(R.string.phial_poison)
        "Paralysis" -> ctx.getString(R.string.phial_paralysis)
        "Exhaust" -> ctx.getString(R.string.phial_exhaust)
        "Dragon" -> ctx.getString(R.string.phial_dragon)
        "Impact" -> ctx.getString(R.string.phial_impact)
        "Element" -> ctx.getString(R.string.phial_element)
        else -> type ?: ""
    }

    /**
     * Localizes a bow's weapon charge level, returning a string in the form Rapid 2, with
     * the name localized to the specific language.
     */
    @JvmStatic fun localizeChargeLevel(level: WeaponChargeLevel): String {
        val name = when (level.name) {
            "Rapid" -> ctx.getString(R.string.bow_charge_rapid)
            "Pierce" -> ctx.getString(R.string.bow_charge_pierce)
            "Spread" -> ctx.getString(R.string.bow_charge_spread)
            "Heavy" -> ctx.getString(R.string.bow_charge_heavy)
            else -> level.name
        }

        return if (level.locked) {
            ctx.getString(R.string.bow_charge_format_locked, name, level.level)
        } else {
            ctx.getString(R.string.bow_charge_format, name, level.level)
        }
    }
}