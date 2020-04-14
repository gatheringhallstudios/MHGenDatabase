package com.ghstudios.android.data

import android.app.Application
import android.content.Context

import com.ghstudios.android.components.WeaponListEntry
import com.ghstudios.android.data.classes.*
import com.ghstudios.android.data.classes.meta.ArmorMetadata
import com.ghstudios.android.data.classes.meta.ItemMetadata
import com.ghstudios.android.data.classes.meta.MonsterMetadata
import com.ghstudios.android.data.cursors.ArmorCursor
import com.ghstudios.android.data.cursors.CombiningCursor
import com.ghstudios.android.data.cursors.ComponentCursor
import com.ghstudios.android.data.cursors.DecorationCursor
import com.ghstudios.android.data.cursors.GatheringCursor
import com.ghstudios.android.data.cursors.HornMelodiesCursor
import com.ghstudios.android.data.cursors.HuntingRewardCursor
import com.ghstudios.android.data.cursors.ItemCursor
import com.ghstudios.android.data.cursors.ItemToMaterialCursor
import com.ghstudios.android.data.cursors.ItemToSkillTreeCursor
import com.ghstudios.android.data.cursors.LocationCursor
import com.ghstudios.android.data.cursors.MonsterAilmentCursor
import com.ghstudios.android.data.cursors.MonsterCursor
import com.ghstudios.android.data.cursors.MonsterDamageCursor
import com.ghstudios.android.data.cursors.MonsterHabitatCursor
import com.ghstudios.android.data.cursors.MonsterToQuestCursor
import com.ghstudios.android.data.cursors.MonsterWeaknessCursor
import com.ghstudios.android.data.cursors.PalicoArmorCursor
import com.ghstudios.android.data.cursors.PalicoWeaponCursor
import com.ghstudios.android.data.cursors.QuestCursor
import com.ghstudios.android.data.cursors.QuestRewardCursor
import com.ghstudios.android.data.cursors.SkillCursor
import com.ghstudios.android.data.cursors.SkillTreeCursor
import com.ghstudios.android.data.cursors.WeaponCursor
import com.ghstudios.android.data.cursors.WishlistComponentCursor
import com.ghstudios.android.data.cursors.WishlistCursor
import com.ghstudios.android.data.cursors.WishlistDataCursor
import com.ghstudios.android.data.cursors.WyporiumTradeCursor
import com.ghstudios.android.data.database.*
import com.ghstudios.android.data.util.SearchFilter
import com.ghstudios.android.util.first
import com.ghstudios.android.util.firstOrNull
import com.ghstudios.android.util.toList

import java.util.ArrayList
import java.util.HashMap


/*
 * Singleton class
 */
class DataManager private constructor(private val mAppContext: Context) {
    companion object {
        private const val TAG = "DataManager"

        // note: not using lateinit due to incompatibility error with @JvmStatic
        private var sDataManager: DataManager? = null

        @JvmStatic fun bindApplication(app: Application) {
            if (sDataManager == null) {
                // Use the application context to avoid leaking activities
                sDataManager = DataManager(app.applicationContext)
            }
        }

        @JvmStatic fun get(): DataManager {
            if (sDataManager == null) {
                throw UninitializedPropertyAccessException("Cannot call get without first binding the application")
            }

            return sDataManager!!
        }
    }

    private val mHelper = MonsterHunterDatabaseHelper.getInstance(mAppContext)

    // additional query objects. These handle different types of queries
    private val metadataDao = MetadataDao(mHelper)
    private val monsterDao = MonsterDao(mHelper)
    private val itemDao = ItemDao(mHelper)
    private val huntingRewardsDao = HuntingRewardsDao(mHelper)
    private val gatheringDao = GatheringDao(mHelper)
    private val skillDao = SkillDao(mHelper)

    val asbManager = ASBManager(mAppContext, this, mHelper)
    val wishlistManager = WishlistManager(mAppContext, this, mHelper)


    /**
     * Returns a map of supported language codes.
     */
    fun getLanguages() = listOf(
        "en", "es", "fr", "de", "it"
    )

    /********************************* ARMOR QUERIES  */

    fun getArmorSetMetadataByFamily(familyId: Long): List<ArmorMetadata> {
        return metadataDao.queryArmorSetMetadataByFamily(familyId)
    }


    fun getArmorSetMetadataByArmor(armorId: Long): List<ArmorMetadata> {
        return metadataDao.queryArmorSetMetadataByArmor(armorId)
    }

    /* Get a Cursor that has a list of all Armors */
    fun queryArmor(): ArmorCursor {
        return itemDao.queryArmor()
    }

    fun queryArmorSearch(searchTerm: String): ArmorCursor {
        return itemDao.queryArmorSearch(searchTerm)
    }

    /* Get a specific Armor */
    fun getArmor(id: Long): Armor? {
        return itemDao.queryArmor(id)
    }

    fun getArmorByFamily(id: Long): List<Armor> {
        return itemDao.queryArmorByFamily(id)
    }

    /* Get an array of Armor based on hunter type */
    fun queryArmorArrayType(type: Int): List<Armor> {
        val cursor = itemDao.queryArmorType(type)
        return cursor.toList { it.armor }
    }

    /**
     * Get a list of armor based on hunter type with a list of all awarded skill points.
     * If "BOTH" is passed, then its equivalent to querying all armor
     */
    fun queryArmorSkillPointsByType(armorSlot: String, hunterType: Int): List<ArmorSkillPoints> {
        return itemDao.queryArmorSkillPointsByType(armorSlot, hunterType)
    }

    fun queryArmorFamilies(type: Int): List<ArmorFamily> {
        return itemDao.queryArmorFamilies(type)
    }

    /**
     * Returns an armor families cursor
     * @param searchFilter the search predicate to filter on
     * @param skipSolos true to skip armor families with a single child, otherwise returns all.
     */
    @JvmOverloads fun queryArmorFamilyBaseSearch(filter: String, skipSolos: Boolean=false): List<ArmorFamilyBase> {
        return itemDao.queryArmorFamilyBaseSearch(filter, skipSolos)
    }

    /********************************* COMBINING QUERIES  */
    /* Get a Cursor that has a list of all Combinings */
    fun queryCombinings(): CombiningCursor {
        return itemDao.queryCombinings()
    }

    fun queryCombiningOnItemID(id: Long): CombiningCursor {
        return itemDao.queryCombinationsOnItemID(id)
    }

    /********************************* COMPONENT QUERIES  */
    /* Get a Cursor that has a list of Components based on the created Item */
    fun queryComponentCreated(id: Long): ComponentCursor {
        return mHelper.queryComponentCreated(id)
    }

    fun queryComponentCreateByArmorFamily(familyId: Long): ComponentCursor {
        return itemDao.queryComponentsByArmorFamily(familyId)
    }

    /* Get a Cursor that has a list of Components based on the component Item */
    fun queryComponentComponent(id: Long): ComponentCursor {
        return mHelper.queryComponentComponent(id)
    }

    /* Get an array of paths for a created Item */
    fun queryComponentCreateImprove(id: Long): ArrayList<String> {
        // Gets all the component Items
        val cursor = mHelper.queryComponentCreated(id)
        cursor.moveToFirst()

        val paths = ArrayList<String>()

        // Only get distinct paths
        while (!cursor.isAfterLast) {
            val type = cursor.component!!.type

            // Check if not a duplicate
            if (!paths.contains(type)) {
                paths.add(type)
            }

            cursor.moveToNext()
        }

        cursor.close()
        return paths
    }

    /********************************* DECORATION QUERIES  */
    /* Get a Cursor that has a list of all Decorations */
    fun queryDecorations(): DecorationCursor {
        return mHelper.queryDecorations()
    }

    /**
     * Gets a cursor that has a list of decorations that pass the filter.
     * Having a null or empty filter is the same as calling without a filter
     */
    fun queryDecorationsSearch(filter: String?): DecorationCursor {
        return mHelper.queryDecorationsSearch(filter ?: "")
    }

    /* Get a specific Decoration */
    fun getDecoration(id: Long): Decoration? {
        var decoration: Decoration? = null
        val cursor = mHelper.queryDecoration(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            decoration = cursor.decoration
        cursor.close()
        return decoration
    }

    /********************************* GATHERING QUERIES  */
    /* Get a Cursor that has a list of Gathering based on Item */
    fun queryGatheringItem(id: Long): GatheringCursor {
        return mHelper.queryGatheringItem(id)
    }

    /* Get a Cursor that has a list of Gathering based on Location */
    fun queryGatheringLocation(id: Long): GatheringCursor {
        return mHelper.queryGatheringLocation(id)
    }

    /* Get a Cursor that has a list of Gathering based on Location and Quest rank */
    fun queryGatheringLocationRank(id: Long, rank: String): GatheringCursor {
        return mHelper.queryGatheringLocationRank(id, rank)
    }

    fun queryGatheringForQuest(questId: Long, locationId: Long, rank: String): GatheringCursor {
        return gatheringDao.queryGatheringsForQuest(questId, if(locationId>=100) locationId-100 else locationId, rank)
    }


    /********************************* HUNTING REWARD QUERIES  */

    /* Get a Cursor that has a list of HuntingReward based on Item */
    fun queryHuntingRewardItem(id: Long): HuntingRewardCursor {
        return huntingRewardsDao.queryHuntingRewardItem(id)
    }

    /* Get a Cursor that has a list of HuntingReward based on Item and Rank*/
    fun queryHuntingRewardForQuest(id: Long, rank: String): HuntingRewardCursor {
        return huntingRewardsDao.queryHuntingRewardForQuest(id,rank)
    }

    /* Get a Cursor that has a list of HuntingReward based on Monster */
    fun queryHuntingRewardMonster(id: Long): HuntingRewardCursor {
        return huntingRewardsDao.queryHuntingRewardMonster(id)
    }

    /* Get a Cursor that has a list of HuntingReward based on Monster and Rank */
    fun queryHuntingRewardMonsterRank(id: Long, rank: String): HuntingRewardCursor {
        return huntingRewardsDao.queryHuntingRewardMonsterRank(id, rank)
    }

    /********************************* ITEM QUERIES  */

    /**
     * Performs a query to receive item metadata
     * @param id
     * @return
     */
    fun queryItemMetadata(id: Long): ItemMetadata? {
        return metadataDao.queryItemMetadata(id)
    }

    /**
     * Returns a cursor that iterates over regular items.
     * Regular items are items that you can find in the item box
     * @return
     */
    fun queryBasicItems(): ItemCursor {
        return itemDao.queryBasicItems("")
    }

    /**
     * Returns a cursor that iterates over regular items (filterable).
     * Regular items are items that you can find in the item box
     * @return
     */
    fun queryBasicItems(searchTerm: String): ItemCursor {
        return itemDao.queryBasicItems(searchTerm)
    }

    /* Get a Cursor that has a list of all Items */
    fun queryItems(): ItemCursor {
        return itemDao.queryItems()
    }

    /* Get a specific Item */
    fun getItem(id: Long): Item? {
        return itemDao.queryItem(id)
    }

    /* Get a Cursor that has a list of filtered Items through search */
    @JvmOverloads fun queryItemSearch(search: String, includeTypes: List<ItemType> = emptyList()): ItemCursor {
        return itemDao.queryItemSearch(search, includeTypes)
    }

    /********************************* ITEM TO SKILL TREE QUERIES  */
    /* Get a Cursor that has a list of ItemToSkillTree based on Item */
    fun queryItemToSkillTreeItem(id: Long): ItemToSkillTreeCursor {
        return mHelper.queryItemToSkillTreeItem(id)
    }

    /* Get a Cursor that has a list of ItemToSkillTree based on SkillTree */
    fun queryItemToSkillTreeSkillTree(id: Long, type: ItemType): ItemToSkillTreeCursor {
        return mHelper.queryItemToSkillTreeSkillTree(id, ItemTypeConverter.serialize(type))
    }

    /** Get an array of ItemToSkillTree based on Item  */
    fun queryItemToSkillTreeArrayItem(id: Long): ArrayList<ItemToSkillTree> {
        val itst = ArrayList<ItemToSkillTree>()
        val cursor = mHelper.queryItemToSkillTreeItem(id)
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            itst.add(cursor.itemToSkillTree)
            cursor.moveToNext()
        }
        cursor.close()
        return itst
    }

    /** Get an array of ItemToSkillTree based on Item  */
    fun queryItemToSkillTreeArrayByArmorFamily(id: Long): HashMap<Long, List<ItemToSkillTree>> {
        val skills = skillDao.queryItemToSkillTreeForArmorFamily(id)
        val results = HashMap<Long, List<ItemToSkillTree>>()

        for (item in skills) {
            val key = item.item!!.id

            val items = results.getOrPut(key) { ArrayList() }
            with(items as ArrayList) {
                items.add(item)
            }
        }
        return results
    }

    /**
     * Return a list of ItemToSkillTree, where the Item is an Armor object.
     */
    fun queryArmorSkillTreePointsBySkillTree(skillTreeId: Long): List<ItemToSkillTree> {
        return itemDao.queryArmorSkillTreePointsBySkillTree(skillTreeId)
    }

    /********************************* ITEM TO MATERIAL QUERIES  */

    fun queryItemsForMaterial(mat_item_id: Long): ItemToMaterialCursor {
        return mHelper.queryItemsForMaterial(mat_item_id)
    }

    /********************************* LOCATION QUERIES  */
    /* Get a Cursor that has a list of all Locations */
    fun queryLocations(): LocationCursor {
        return mHelper.queryLocations()
    }

    /* Get a specific Location */
    fun getLocation(id: Long): Location? {
        return mHelper.queryLocation(id)
    }

    fun queryLocationsSearch(searchTerm: String): List<Location> {
        // todo: we could also create a cursor wrapper implementation that filters in memory...without first converting to objects
        val locations = queryLocations().toList { it.location }
        if (searchTerm.isBlank()) {
            return locations
        }

        val filter = SearchFilter(searchTerm)
        return locations.filter { filter.matches(it.name) }
    }

    /********************************* MELODY QUERIES  */

    /* Get a Cursor that has a list of all Melodies from a specific set of notes */
    fun queryMelodiesFromNotes(notes: String): HornMelodiesCursor {
        return mHelper.queryMelodiesFromNotes(notes)
    }


    /********************************* MONSTER QUERIES  */
    /* Get a Cursor that has a list of all Monster */

    fun queryMonsterMetadata(id: Long): MonsterMetadata? {
        return metadataDao.queryMonsterMetadata(id)
    }

    /**
     * Returns a cursor that iterates over all monsters of a particular type
     */
    fun queryMonsters(monsterClass: MonsterClass?): MonsterCursor {
        return monsterDao.queryMonsters(monsterClass)
    }

    fun questDeviantMonsterNames(): Array<String> {
        return monsterDao.queryDeviantMonsterNames()
    }

    fun queryMonstersSearch(searchTerm: String): MonsterCursor {
        return monsterDao.queryMonstersSearch(searchTerm)
    }

    /* Get a specific Monster */
    fun getMonster(id: Long): Monster? {
        return monsterDao.queryMonster(id)
    }

    /********************************* MONSTER AILMENT QUERIES  */
    /* Get a cursor that lists all the ailments a particular monster can inflict */
    fun queryAilmentsFromId(id: Long): MonsterAilmentCursor {
        return mHelper.queryAilmentsFromMonster(id)
    }

    /********************************* MONSTER DAMAGE QUERIES  */
    /* Get a Cursor that has a list of MonsterDamage for a specific Monster */
    fun queryMonsterDamage(id: Long): MonsterDamageCursor {
        return mHelper.queryMonsterDamage(id)
    }

    /* Get an array of MonsterDamage for a specific Monster */
    fun queryMonsterDamageArray(id: Long): List<MonsterDamage> {
        val cursor = queryMonsterDamage(id)
        return cursor.toList { it.monsterDamage }
    }

    /********************************* MONSTER STATUS QUERIES  */
    /* Get an array of status objects for a monster */
    fun queryMonsterStatus(id: Long): List<MonsterStatus> {
        val cursor = mHelper.queryMonsterStatus(id)
        return cursor.toList { it.status }
    }

    /********************************* MONSTER TO QUEST QUERIES  */
    /* Get a Cursor that has a list of MonsterToQuest based on Monster */
    fun queryMonsterToQuestMonster(id: Long): MonsterToQuestCursor {
        return mHelper.queryMonsterToQuestMonster(id)
    }

    /* Get a Cursor that has a list of MonsterToQuest based on Quest */
    fun queryMonsterToQuestQuest(id: Long): MonsterToQuestCursor {
        return mHelper.queryMonsterToQuestQuest(id)
    }

    /********************************* MONSTER HABITAT QUERIES  */
    /* Get a Cursor that has a list of MonsterHabitats based on Monster */
    fun queryHabitatMonster(id: Long): MonsterHabitatCursor {
        return mHelper.queryHabitatMonster(id)
    }

    /* Get a Cursor that has a list of MonsterHabitats based on Location */
    fun queryHabitatLocation(id: Long): MonsterHabitatCursor {
        return mHelper.queryHabitatLocation(id)
    }

    /********************************* MONSTER WEAKNESS QUERIES  */

    /* Get a cursor that has all a monsters weaknesses */
    fun queryWeaknessFromMonster(id: Long): MonsterWeaknessCursor {
        return mHelper.queryWeaknessFromMonster(id)
    }

    /* Get an array of MonsterWeakness for a specific Monster */
    fun queryMonsterWeaknessArray(id: Long): List<MonsterWeakness> {
        val cursor = queryWeaknessFromMonster(id)
        return cursor.toList { it.weakness }
    }

    /********************************* QUEST QUERIES  */

    /* Get a Cursor that has a list of all Quests */
    fun queryQuests(): QuestCursor {
        return mHelper.queryQuests()
    }

    fun queryQuestsSearch(searchTerm: String): QuestCursor {
        return mHelper.queryQuestsSearch(searchTerm)
    }

    /* Get a specific Quests */
    fun getQuest(id: Long): Quest? {
        return mHelper.queryQuest(id).firstOrNull { it.quest }
    }

    /* Get an array of Quest based on hub */
    fun queryQuestArrayHub(hub: QuestHub): List<Quest> {
        return this.queryQuestHub(hub).toList { it.quest }
    }

    /* Get a Cursor that has a list of Quest based on hub */
    fun queryQuestHub(hub: QuestHub): QuestCursor {
        return mHelper.queryQuestHub(hub)
    }

    /* Get a Cursor that has a list of Quest based on hub and stars */
    fun queryQuestHubStar(hub: QuestHub, stars: String): QuestCursor {
        return mHelper.queryQuestHubStar(hub, stars)
    }

    /********************************* QUEST REWARD QUERIES  */
    /* Get a Cursor that has a list of QuestReward based on Item */
    fun queryQuestRewardItem(id: Long): QuestRewardCursor {
        return mHelper.queryQuestRewardItem(id)
    }

    /* Get a Cursor that has a list of QuestReward based on Quest */
    fun queryQuestRewardQuest(id: Long): QuestRewardCursor {
        return mHelper.queryQuestRewardQuest(id)
    }

    /********************************* SKILL QUERIES  */

    //    public SkillCursor querySkill(long id) {
    //        return mHelper.querySkill(id);
    //    }

    /* Get a Cursor that has a list of all Skills from a specific SkillTree */
    fun querySkillsFromTree(id: Long): SkillCursor {
        return mHelper.querySkillsFromTree(id)
    }

    /********************************* SKILL TREE QUERIES  */
    /* Get a Cursor that has a list of all SkillTree */
    fun querySkillTrees(): SkillTreeCursor {
        return mHelper.querySkillTrees()
    }

    fun querySkillTreesSearch(searchTerm: String): SkillTreeCursor {
        return mHelper.querySkillTreesSearch(searchTerm)
    }

    /** Get a specific SkillTree */
    fun getSkillTree(id: Long): SkillTree? {
        return mHelper.querySkillTree(id)
    }

    /********************************* WEAPON QUERIES  */
    /* Get a Cursor that has a list of all Weapons */
    fun queryWeapon(): WeaponCursor {
        return mHelper.queryWeapon()
    }

    /* Get a specific Weapon */
    fun getWeapon(id: Long): Weapon? {
        var weapon: Weapon? = null
        val cursor = mHelper.queryWeapon(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            weapon = cursor.weapon
        cursor.close()
        return weapon
    }

    /* Get a Cursor that has a list of Weapons based on weapon type */
    fun queryWeaponType(type: String): WeaponCursor {
        return mHelper.queryWeaponType(type, false)
    }


    fun getWeaponType(id: Long): String {
        val c = mHelper.queryWeaponTypeForWeapon(id)
        c.moveToFirst()
        val wtype = c.getString(0)
        c.close()
        return wtype
    }

    /* Get an array of weapon expandable list items
    * */
    fun queryWeaponTreeArray(type: String): ArrayList<WeaponListEntry> {
        val cursor = mHelper.queryWeaponType(type, false)

        cursor.moveToFirst()
        val weapons = ArrayList<WeaponListEntry>()
        val weaponDict = HashMap<Long, WeaponListEntry>()
        var currentEntry: WeaponListEntry
        var currentWeapon: Weapon?

        while (!cursor.isAfterLast) {
            currentWeapon = cursor.weapon
            currentEntry = WeaponListEntry(currentWeapon)
            weaponDict[currentWeapon.id] = currentEntry
            cursor.moveToNext()
        }

        var parent_id: Int
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            currentWeapon = cursor.weapon
            currentEntry = weaponDict[currentWeapon.id]!!

            parent_id = currentWeapon.parentId
            if (parent_id != 0) {
                weaponDict[parent_id.toLong()]?.addChild(currentEntry)
            }

            weapons.add(currentEntry)
            cursor.moveToNext()
        }

        return weapons
    }

    /*
    * Get an array of weapon expandable list items consisting only of the final upgrades
    */
    fun queryWeaponTreeArrayFinal(type: String): ArrayList<WeaponListEntry> {
        val cursor = mHelper.queryWeaponType(type, true)

        val weapons = ArrayList<WeaponListEntry>()

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            weapons.add(WeaponListEntry(cursor.weapon))
            cursor.moveToNext()
        }

        return weapons
    }

    /* Get a Cursor that has a list of Weapons in the weapon tree for a specified weapon */
    fun queryWeaponTree(id: Long): WeaponCursor {
        return mHelper.queryWeaponFamily(id)
    }

    /**
     * Returns the first weapon of each family tree that led up to the family tree of id.
     */
    fun queryWeaponOrigins(id: Long): List<Weapon>{
        val weapons = ArrayList<Weapon>()

        // Iteratively does the following:
        // - get the first weapon of the tree: (id && S.WEAPON_FAMILY_MASK) + 1
        // - get the parent of that weapon
        // - get the first weapon of that tree and cycle again

        // note: (id && S.WEAPON_FAMILY_MASK) + 1 is the FIRST weapon of the tree.
        var currentId = (id and S.WEAPON_FAMILY_MASK) + 1

        while(true) {
            // This particular weapon cursor returns incomplete info
            // todo: consider making either a WeaponBase superclass, a WeaponBasic class, or return full info from queryWeaponTreeParent
            val weaponBase = mHelper.queryWeaponTreeParent(currentId)
                    .firstOrNull { it.weapon }
                    ?: break

            // Query the full weapon data for the weapon cursor
            val weapon = mHelper.queryWeapon(weaponBase.id).first { it.weapon }

            // add to results, and then get the id of the first weapon of that tree and iterate again
            weapons.add(weapon)
            currentId = (weapon.id and S.WEAPON_FAMILY_MASK) + 1
        }

        return weapons
    }

    fun queryWeaponBranches(id:Long): List<Weapon> {
        val wt = mHelper.queryWeaponFamilyBranches(id).toList { it.weapon }
        val weapons = ArrayList<Weapon>()
        for(w in wt){
            val wCur = mHelper.queryWeapon(w.id).firstOrNull { it.weapon }
            if(wCur!=null) weapons.add(wCur)
        }
        return weapons
    }

    /**
     * Queries all final weapons that can be derived from this one
     */
    fun queryWeaponFinal(id: Long): List<Weapon> {
        val results = mutableListOf<Weapon>()

        // Get the final of this tree
        val final = mHelper.queryWeaponTreeFinal(id)
        if (final != null) {
            results.add(final)
        }

        // Get the branch weapons, and recursively get their final weapons
        val otherBranches = this.queryWeaponBranches(id)
        val branchFinals = otherBranches.map { queryWeaponFinal(it.id) }.flatten()
        results.addAll(branchFinals)

        return results
    }

    fun queryPalicoWeapons(): PalicoWeaponCursor {
        return mHelper.queryPalicoWeapons()
    }

    fun getPalicoWeapon(id: Long): PalicoWeapon? {
        val cursor = mHelper.queryPalicoWeapon(id)
        cursor.moveToFirst()
        val w = cursor.weapon
        cursor.close()
        return w
    }

    fun queryPalicoArmor(): PalicoArmorCursor {
        return mHelper.queryPalicoArmors()
    }

    fun getPalicoArmor(id: Long): PalicoArmor {
        val cursor = mHelper.queryPalicoArmor(id)
        cursor.moveToFirst()
        val w = cursor.armor
        cursor.close()
        return w
    }

    /**************************** WYPORIUM TRADE DATA QUERIES  */
    /* Get a Cursor that has a list of all wyporium trades */
    fun queryWyporiumTrades(): WyporiumTradeCursor {
        return mHelper.queryWyporiumTrades()
    }

    /* Get a specific wyporium trade */
    fun getWyporiumTrade(id: Long): WyporiumTrade? {
        var wyporiumTrade: WyporiumTrade? = null
        val cursor = mHelper.queryWyporiumTrades(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            wyporiumTrade = cursor.wyporiumTrade
        cursor.close()
        return wyporiumTrade
    }
}
