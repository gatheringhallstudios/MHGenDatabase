package com.ghstudios.android.data.database

import android.content.Context

import com.ghstudios.android.util.MHUtils
import com.ghstudios.android.components.WeaponListEntry
import com.ghstudios.android.data.classes.ASBSession
import com.ghstudios.android.data.classes.ASBSet
import com.ghstudios.android.data.classes.Armor
import com.ghstudios.android.data.classes.ArmorFamily
import com.ghstudios.android.data.classes.Component
import com.ghstudios.android.data.classes.Decoration
import com.ghstudios.android.data.classes.Item
import com.ghstudios.android.data.classes.ItemToSkillTree
import com.ghstudios.android.data.classes.ItemType
import com.ghstudios.android.data.classes.Location
import com.ghstudios.android.data.classes.Monster
import com.ghstudios.android.data.classes.MonsterClass
import com.ghstudios.android.data.classes.MonsterDamage
import com.ghstudios.android.data.classes.MonsterStatus
import com.ghstudios.android.data.classes.MonsterWeakness
import com.ghstudios.android.data.classes.PalicoArmor
import com.ghstudios.android.data.classes.PalicoWeapon
import com.ghstudios.android.data.classes.Quest
import com.ghstudios.android.data.classes.SkillTree
import com.ghstudios.android.data.classes.Weapon
import com.ghstudios.android.data.classes.Wishlist
import com.ghstudios.android.data.classes.WishlistComponent
import com.ghstudios.android.data.classes.WishlistData
import com.ghstudios.android.data.classes.WyporiumTrade
import com.ghstudios.android.data.classes.meta.ArmorMetadata
import com.ghstudios.android.data.classes.meta.ItemMetadata
import com.ghstudios.android.data.classes.meta.MonsterMetadata
import com.ghstudios.android.data.cursors.ASBSessionCursor
import com.ghstudios.android.data.cursors.ASBSetCursor
import com.ghstudios.android.data.cursors.ArmorCursor
import com.ghstudios.android.data.cursors.ArmorFamilyCursor
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
import com.ghstudios.android.data.cursors.MonsterStatusCursor
import com.ghstudios.android.data.cursors.MonsterToQuestCursor
import com.ghstudios.android.data.cursors.MonsterWeaknessCursor
import com.ghstudios.android.data.cursors.PalicoArmorCursor
import com.ghstudios.android.data.cursors.PalicoWeaponCursor
import com.ghstudios.android.data.cursors.QuestCursor
import com.ghstudios.android.data.cursors.QuestRewardCursor
import com.ghstudios.android.data.cursors.SkillCursor
import com.ghstudios.android.data.cursors.SkillTreeCursor
import com.ghstudios.android.data.cursors.WeaponCursor
import com.ghstudios.android.data.cursors.WeaponTreeCursor
import com.ghstudios.android.data.cursors.WishlistComponentCursor
import com.ghstudios.android.data.cursors.WishlistCursor
import com.ghstudios.android.data.cursors.WishlistDataCursor
import com.ghstudios.android.data.cursors.WyporiumTradeCursor
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

        @JvmStatic fun get(c: Context): DataManager {
            if (sDataManager == null) {
                // Use the application context to avoid leaking activities
                sDataManager = DataManager(c.applicationContext)
                return sDataManager!!
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

    fun queryArmorFamilies(type: Int): List<ArmorFamily> {
        val cursor = itemDao.queryArmorFamilies(type)

        val results = mutableListOf<ArmorFamily>()

        cursor.moveToFirst()
        var family = cursor.armor
        results.add(family)
        while (cursor.moveToNext()) {
            val newFamily = cursor.armor
            if (family.id == newFamily.id) {
                family.skills.add(newFamily.skills[0])
            } else {
                family = newFamily
                results.add(family)
            }
        }
        return results
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
        var filter = filter
        filter = filter?.trim { it <= ' ' } ?: ""
        return if (filter == "") queryDecorations() else mHelper.queryDecorationsSearch(filter)
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
        return gatheringDao.queryGatheringsForQuest(questId, locationId, rank)
    }


    /********************************* HUNTING REWARD QUERIES  */

    /* Get a Cursor that has a list of HuntingReward based on Item */
    fun queryHuntingRewardItem(id: Long): HuntingRewardCursor {
        return huntingRewardsDao.queryHuntingRewardItem(id)
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
    fun queryItemSearch(search: String): ItemCursor {
        return itemDao.queryItemSearch(search)
    }

    /********************************* ITEM TO SKILL TREE QUERIES  */
    /* Get a Cursor that has a list of ItemToSkillTree based on Item */
    fun queryItemToSkillTreeItem(id: Long): ItemToSkillTreeCursor {
        return mHelper.queryItemToSkillTreeItem(id)
    }

    /* Get a Cursor that has a list of ItemToSkillTree based on SkillTree */
    fun queryItemToSkillTreeSkillTree(id: Long, type: String): ItemToSkillTreeCursor {
        return mHelper.queryItemToSkillTreeSkillTree(id, type)
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
        var location: Location? = null
        val cursor = mHelper.queryLocation(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            location = cursor.location
        cursor.close()
        return location
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
    fun queryQuestArrayHub(hub: String): List<Quest> {
        val cursor = mHelper.queryQuestHub(hub)
        return cursor.toList { it.quest }
    }

    /* Get a Cursor that has a list of Quest based on hub */
    fun queryQuestHub(hub: String): QuestCursor {
        return mHelper.queryQuestHub(hub)
    }

    /* Get a Cursor that has a list of Quest based on hub and stars */
    fun queryQuestHubStar(hub: String, stars: String): QuestCursor {
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
    fun querySkillFromTree(id: Long): SkillCursor {
        return mHelper.querySkillFromTree(id)
    }

    /********************************* SKILL TREE QUERIES  */
    /* Get a Cursor that has a list of all SkillTree */
    fun querySkillTrees(): SkillTreeCursor {
        return mHelper.querySkillTrees()
    }

    fun querySkillTreesSearch(searchTerm: String): SkillTreeCursor {
        return mHelper.querySkillTreesSearch(searchTerm)
    }

    /* Get a specific SkillTree */
    fun getSkillTree(id: Long): SkillTree? {
        var skillTree: SkillTree? = null
        val cursor = mHelper.querySkillTree(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            skillTree = cursor.skillTree
        cursor.close()
        return skillTree
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
            weaponDict[currentWeapon!!.id] = currentEntry
            cursor.moveToNext()
        }

        var parent_id: Int
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            currentWeapon = cursor.weapon
            currentEntry = weaponDict[currentWeapon!!.id]!!

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
        val ids = ArrayList<Long>()
        ids.add(id)            // Add specified weapon to returned array

        var currentId = id
        var cursor: WeaponTreeCursor? = null

        // Get ancestors and add them at the beginning of the tree
        do {
            cursor = mHelper.queryWeaponTreeParent(currentId)
            cursor.moveToFirst()

            if (cursor.isAfterLast)
                break

            currentId = cursor.weapon!!.id
            ids.add(0, currentId)

            cursor.close()
        } while (true)

        currentId = id        // set current id back to specified weapon

        // Get children only; exclude descendants of children
        cursor = mHelper.queryWeaponTreeChild(currentId)
        cursor.moveToFirst()

        if (!cursor.isAfterLast) {
            for (i in 0 until cursor.count) {
                ids.add(cursor.weapon!!.id)
                cursor.moveToNext()
            }
        }
        cursor.close()

        // Convert Arraylist to a regular array to return
        val idArray = LongArray(ids.size)
        for (i in idArray.indices) {
            idArray[i] = ids[i]
        }

        return mHelper.queryWeapons(idArray)

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

    /********************************* WISHLIST QUERIES  */
    /* Get a Cursor that has a list of all Wishlists */
    fun queryWishlists(): WishlistCursor {
        return mHelper.queryWishlists()
    }

    /* Get a specific Wishlist */
    fun queryWishlist(id: Long): WishlistCursor {
        return mHelper.queryWishlist(id)
    }

    /** Add a new Wishlist with a given name and returns the id.  */
    fun queryAddWishlist(name: String): Long {
        return mHelper.queryAddWishlist(name)
    }

    /* Update a specific Wishlist with a new name */
    fun queryUpdateWishlist(id: Long, name: String) {
        mHelper.queryUpdateWishlist(id, name)
    }

    /* Delete a specific Wishlist */
    fun queryDeleteWishlist(id: Long) {
        mHelper.queryDeleteWishlist(id)
    }

    /* Copy a specific Wishlist into a new wishlist, including its entries */
    fun queryCopyWishlist(id: Long, name: String) {
        val newId = mHelper.queryAddWishlist(name)

        // Get all of the entries from the copied wishlist
        val cursor = mHelper.queryWishlistData(id)
        cursor.moveToFirst()

        // Add all of the retrieved entries into the new wishlist
        while (!cursor.isAfterLast) {
            val wishlist = cursor.wishlistData
            mHelper.queryAddWishlistDataAll(newId, wishlist!!.item.id,
                    wishlist.quantity, wishlist.satisfied, wishlist.path)
            cursor.moveToNext()
        }
        cursor.close()

        // Get all of the components from the copied wishlist
        val wcCursor = mHelper.queryWishlistComponents(id)
        wcCursor.moveToFirst()

        // Add all of the retrieved components into the new wishlist
        while (!wcCursor.isAfterLast) {
            val wishlist = wcCursor.wishlistComponent
            mHelper.queryAddWishlistComponentAll(newId, wishlist!!.item.id,
                    wishlist.quantity, wishlist.notes)
            wcCursor.moveToNext()
        }
        wcCursor.close()
    }

    /* Get a specific Wishlist */
    fun getWishlist(id: Long): Wishlist? {
        var wishlist: Wishlist? = null
        val cursor = mHelper.queryWishlist(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            wishlist = cursor.wishlist
        cursor.close()
        return wishlist
    }

    /********************************* WISHLIST DATA QUERIES  */
    /* Get a Cursor that has a list of WishlistData based on Wishlist */
    fun queryWishlistData(id: Long): WishlistDataCursor {
        return mHelper.queryWishlistData(id)
    }

    /* Add an entry to a specific wishlist with the given item and quantity */
    fun queryAddWishlistData(wishlist_id: Long, item_id: Long, quantity: Int, path: String) {

        val cursor = mHelper.queryWishlistData(wishlist_id, item_id, path)
        cursor.moveToFirst()

        if (cursor.isAfterLast) {
            // Add new entry to wishlist_data
            mHelper.queryAddWishlistData(wishlist_id, item_id, quantity, path)
        } else {
            // Update existing entry
            val data = cursor.wishlistData
            val id = data!!.id
            val total = data.quantity + quantity

            mHelper.queryUpdateWishlistDataQuantity(id, total)
        }
        cursor.close()

        helperQueryAddWishlistData(wishlist_id, item_id, quantity, path)
        helperQueryUpdateWishlistSatisfied(wishlist_id)
    }

    /* Helper method: Add an entry to a wishlist,
     *        and add the necessary components from the chosen path
     */
    private fun helperQueryAddWishlistData(wishlist_id: Long, item_id: Long, quantity: Int, path: String) {
        // Get the components for the entry
        val cc = mHelper.queryComponentCreatedType(item_id, path)
        cc.moveToFirst()

        var wc: WishlistComponentCursor? = null

        // Add each component to the wishlist component list
        while (!cc.isAfterLast) {
            val component_id = cc.component!!.component.id
            val c_amt = cc.component!!.quantity * quantity

            wc = mHelper.queryWishlistComponent(wishlist_id, component_id)
            wc.moveToFirst()

            if (wc.isAfterLast) {
                // Add component entry to wishlist_component
                mHelper.queryAddWishlistComponent(wishlist_id, component_id, c_amt)
            } else {
                // Update component entry to wishlist_component
                val wc_id = wc.wishlistComponent!!.id
                val old_amt = wc.wishlistComponent!!.quantity

                mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt + c_amt)
            }
            wc.close()

            cc.moveToNext()
        }
        cc.close()
    }

    /* Update an entry to the given quantity */
    fun queryUpdateWishlistData(id: Long, quantity: Int) {

        // Get the existing entry from WishlistData
        val wdCursor = mHelper.queryWishlistDataId(id)
        wdCursor.moveToFirst()
        val wd = wdCursor.wishlistData
        wdCursor.close()

        val wishlist_id = wd!!.wishlistId
        val item_id = wd.item.id
        val wd_old_quantity = wd.quantity
        val path = wd.path

        // Find the different between new and old quantities
        val diff_quantity = quantity - wd_old_quantity

        // Get the components for the WishlistData entry
        val cc = mHelper.queryComponentCreatedType(item_id, path)
        cc.moveToFirst()

        // Update those components in WishlistComponent
        while (!cc.isAfterLast) {
            val component_id = cc.component!!.component.id
            val c_amt = cc.component!!.quantity * diff_quantity

            val wc = mHelper.queryWishlistComponent(wishlist_id, component_id)
            wc.moveToFirst()

            // Update component entry to wishlist_component
            val wc_id = wc.wishlistComponent!!.id
            val old_amt = wc.wishlistComponent!!.quantity

            mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt + c_amt)

            wc.close()
            cc.moveToNext()
        }
        cc.close()

        mHelper.queryUpdateWishlistDataQuantity(id, quantity)

        // Check for any changes if any WishlistData is satisfied (can be build)
        helperQueryUpdateWishlistSatisfied(wishlist_id)
    }

    /* Delete an entry from WishlistData */
    fun queryDeleteWishlistData(id: Long) {

        // Get the existing entry from WishlistData
        val wdCursor = mHelper.queryWishlistDataId(id)
        wdCursor.moveToFirst()
        val wd = wdCursor.wishlistData
        wdCursor.close()

        val wishlist_id = wd!!.wishlistId
        val item_id = wd.item.id
        val wd_old_quantity = wd.quantity
        val path = wd.path

        // Get the components for the WishlistData entry
        val cc = mHelper.queryComponentCreatedType(item_id, path)
        cc.moveToFirst()

        // Update those components in WishlistComponent
        while (!cc.isAfterLast) {
            val component_id = cc.component!!.component.id
            val c_amt = cc.component!!.quantity * wd_old_quantity

            val wc = mHelper.queryWishlistComponent(wishlist_id, component_id)
            wc.moveToFirst()

            // Update component entry to wishlist_component
            val wc_id = wc.wishlistComponent!!.id
            val old_amt = wc.wishlistComponent!!.quantity

            val new_amt = old_amt - c_amt

            if (new_amt > 0) {
                // Update wishlist_component if component is still needed
                mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt - c_amt)
            } else {
                // If component no longer needed, delete it from wishlist_component
                mHelper.queryDeleteWishlistComponent(wc_id)
            }

            wc.close()
            cc.moveToNext()
        }
        cc.close()

        mHelper.queryDeleteWishlistData(id)
    }

    /* Get the total price/cost for the specified wishlist */
    fun queryWishlistPrice(id: Long): Int {
        var total = 0        // total cost

        // Get all of the WishlistData from the wishlist
        val wdc = mHelper.queryWishlistData(id)
        wdc.moveToFirst()

        var buy: Int
        var quantity = 0

        // Calculate cost for each WishlistData entry
        while (!wdc.isAfterLast) {
            buy = 0        // cost for entry
            val wd = wdc.wishlistData
            val i = wd!!.item
            val type = wd.path

            // Check path if the entry is a Weapon
            if (i.type === ItemType.WEAPON) {
                val wc = mHelper.queryWeapon(i.id)
                wc.moveToFirst()

                // Get the cost from the desired path
                if (type == "Create")
                    buy = wc.weapon!!.creationCost
                else if (type == "Improve") {
                    buy = wc.weapon!!.upgradeCost
                }
                wc.close()
            } else {
                buy = wd.item.buy
            }// For Armor and Decoration

            // Add the entry cost to total cost
            quantity = wd.quantity
            total = total + buy * quantity

            wdc.moveToNext()
        }
        wdc.close()
        return total
    }

    /********************************* WISHLIST COMPONENT QUERIES  */
    /* Get a Cursor that has a list of WishlistComponent based on Wishlist */
    fun queryWishlistComponents(id: Long): WishlistComponentCursor {
        return mHelper.queryWishlistComponents(id)
    }

    /* Update the specified WishlistComponent by the given quantity */
    fun queryUpdateWishlistComponentNotes(id: Long, notes: Int) {
        mHelper.queryUpdateWishlistComponentNotes(id, notes)
    }

    /**
     * From a specified wishlist id, check if any WishlistData can be built
     */
    fun helperQueryUpdateWishlistSatisfied(wishlist_id: Long) {
        val wdc = mHelper.queryWishlistData(wishlist_id)
        wdc.moveToFirst()

        var wd: WishlistData? = null
        var wc: WishlistComponent? = null
        var wcc: WishlistComponentCursor? = null

        var c: Component? = null
        var cc: ComponentCursor? = null

        var path: String
        var created_id: Long
        var component_id: Long
        var required_amt: Int
        var have_amt: Int
        var satisfied: Int

        // For every WishlistData
        while (!wdc.isAfterLast) {
            satisfied = 1            // Set true until unsatisfied
            wd = wdc.wishlistData
            created_id = wd!!.item.id
            path = wd.path

            cc = mHelper.queryComponentCreatedType(created_id, path)
            cc.moveToFirst()

            // For every component of the current WishlistData entry
            while (!cc.isAfterLast) {
                c = cc.component
                component_id = c!!.component.id

                wcc = mHelper.queryWishlistComponent(wishlist_id, component_id)
                wcc.moveToFirst()
                wc = wcc.wishlistComponent

                // Get the amounts
                required_amt = c.quantity
                have_amt = wc!!.notes

                // Check if user does not have enough materials
                if (have_amt < required_amt) {
                    satisfied = 0
                    break
                }

                wcc.close()
                cc.moveToNext()
            }

            cc.close()

            // Update the WishlistData entry
            mHelper.queryUpdateWishlistDataSatisfied(wd.id, satisfied)
            wdc.moveToNext()
        }

        wdc.close()
    }

    /********************************* ARMOR SET BUILDER QUERIES  */

    fun queryASBSets(): ASBSetCursor {
        return mHelper.queryASBSets()
    }

    fun getASBSet(id: Long): ASBSet? {
        var set: ASBSet? = null
        val cursor = mHelper.queryASBSet(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            set = cursor.asbSet

        cursor.close()
        return set
    }

    /** Get a cursor with a list of all armor sets.  */
    fun queryASBSessions(): ASBSessionCursor {
        return mHelper.queryASBSessions()
    }

    /** Get a specific armor set.  */
    fun getASBSession(id: Long): ASBSession? {
        var session: ASBSession? = null
        val cursor = mHelper.queryASBSession(id)
        cursor.moveToFirst()

        if (!cursor.isAfterLast)
            session = cursor.getASBSession(mAppContext)

        cursor.close()
        return session
    }

    /** Adds a new ASB set to the list.  */
    fun queryAddASBSet(name: String, rank: Int, hunterType: Int) {
        mHelper.queryAddASBSet(name, rank, hunterType)
    }

    /** Adds a new set that is a copy of the designated set to the list.  */
    fun queryAddASBSet(setId: Long) {
        val set = getASBSet(setId)
        mHelper.queryAddASBSet(set!!.name!!, set.rank, set.hunterType)
    }

    fun queryDeleteASBSet(setId: Long) {
        mHelper.queryDeleteASBSet(setId)
    }

    fun queryUpdateASBSet(setId: Long, name: String, rank: Int, hunterType: Int) {
        mHelper.queryUpdateASBSet(setId, name, rank, hunterType)
    }

    fun queryPutASBSessionArmor(asbSetId: Long, armorId: Long, pieceIndex: Int) {
        mHelper.queryAddASBSessionArmor(asbSetId, armorId, pieceIndex)
    }

    fun queryRemoveASBSessionArmor(asbSetId: Long, pieceIndex: Int) {
        mHelper.queryAddASBSessionArmor(asbSetId, -1, pieceIndex)
    }

    fun queryPutASBSessionDecoration(asbSetId: Long, decorationId: Long, pieceIndex: Int, decorationIndex: Int) {
        mHelper.queryPutASBSessionDecoration(asbSetId, decorationId, pieceIndex, decorationIndex)
    }

    fun queryRemoveASBSessionDecoration(asbSetId: Long, pieceIndex: Int, decorationIndex: Int) {
        mHelper.queryPutASBSessionDecoration(asbSetId, -1, pieceIndex, decorationIndex)
    }

    fun queryCreateASBSessionTalisman(asbSetId: Long, type: Int, slots: Int, skill1Id: Long, skill1Points: Int, skill2Id: Long, skill2Points: Int) {
        mHelper.queryCreateASBSessionTalisman(asbSetId, type, slots, skill1Id, skill1Points, skill2Id, skill2Points)
    }

    fun queryRemoveASBSessionTalisman(asbSetId: Long) {
        mHelper.queryRemoveASBSessionTalisman(asbSetId)
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
