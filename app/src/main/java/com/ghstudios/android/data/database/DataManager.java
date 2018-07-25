package com.ghstudios.android.data.database;

import android.content.Context;
import android.database.Cursor;

import com.ghstudios.android.MHUtils;
import com.ghstudios.android.components.WeaponListEntry;
import com.ghstudios.android.data.classes.ASBSession;
import com.ghstudios.android.data.classes.ASBSet;
import com.ghstudios.android.data.classes.Armor;
import com.ghstudios.android.data.classes.Component;
import com.ghstudios.android.data.classes.Decoration;
import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemToSkillTree;
import com.ghstudios.android.data.classes.Location;
import com.ghstudios.android.data.classes.Monster;
import com.ghstudios.android.data.classes.MonsterDamage;
import com.ghstudios.android.data.classes.MonsterSize;
import com.ghstudios.android.data.classes.MonsterStatus;
import com.ghstudios.android.data.classes.MonsterWeakness;
import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.classes.Quest;
import com.ghstudios.android.data.classes.SkillTree;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.data.classes.Wishlist;
import com.ghstudios.android.data.classes.WishlistComponent;
import com.ghstudios.android.data.classes.WishlistData;
import com.ghstudios.android.data.classes.WyporiumTrade;
import com.ghstudios.android.data.classes.meta.ArmorMetadata;
import com.ghstudios.android.data.classes.meta.ItemMetadata;
import com.ghstudios.android.data.classes.meta.MonsterMetadata;
import com.ghstudios.android.data.cursors.ASBSessionCursor;
import com.ghstudios.android.data.cursors.ASBSetCursor;
import com.ghstudios.android.data.cursors.ArmorCursor;
import com.ghstudios.android.data.cursors.CombiningCursor;
import com.ghstudios.android.data.cursors.ComponentCursor;
import com.ghstudios.android.data.cursors.DecorationCursor;
import com.ghstudios.android.data.cursors.GatheringCursor;
import com.ghstudios.android.data.cursors.HornMelodiesCursor;
import com.ghstudios.android.data.cursors.HuntingRewardCursor;
import com.ghstudios.android.data.cursors.ItemCursor;
import com.ghstudios.android.data.cursors.ItemToMaterialCursor;
import com.ghstudios.android.data.cursors.ItemToSkillTreeCursor;
import com.ghstudios.android.data.cursors.LocationCursor;
import com.ghstudios.android.data.cursors.MonsterAilmentCursor;
import com.ghstudios.android.data.cursors.MonsterCursor;
import com.ghstudios.android.data.cursors.MonsterDamageCursor;
import com.ghstudios.android.data.cursors.MonsterHabitatCursor;
import com.ghstudios.android.data.cursors.MonsterStatusCursor;
import com.ghstudios.android.data.cursors.MonsterToQuestCursor;
import com.ghstudios.android.data.cursors.MonsterWeaknessCursor;
import com.ghstudios.android.data.cursors.PalicoWeaponCursor;
import com.ghstudios.android.data.cursors.QuestCursor;
import com.ghstudios.android.data.cursors.QuestRewardCursor;
import com.ghstudios.android.data.cursors.SkillCursor;
import com.ghstudios.android.data.cursors.SkillTreeCursor;
import com.ghstudios.android.data.cursors.WeaponCursor;
import com.ghstudios.android.data.cursors.WeaponTreeCursor;
import com.ghstudios.android.data.cursors.WishlistComponentCursor;
import com.ghstudios.android.data.cursors.WishlistCursor;
import com.ghstudios.android.data.cursors.WishlistDataCursor;
import com.ghstudios.android.data.cursors.WyporiumTradeCursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
 * Singleton class
 */
public class DataManager {
    private static final String TAG = "DataManager";

    private static DataManager sDataManager;        // Singleton design
    private Context mAppContext;
    private MonsterHunterDatabaseHelper mHelper;    // Used for queries

    // additional query objects. These handle different types of queries
    private MetadataDao metadataDao;
    private MonsterDao monsterDao;
    private ItemDao itemDao;
    private HuntingRewardsDao huntingRewardsDao;
    private GatheringDao gatheringDao;
    
    /* Singleton design */
    private DataManager(Context appContext) {
        mAppContext = appContext;
        mHelper = MonsterHunterDatabaseHelper.getInstance(appContext);
        metadataDao = new MetadataDao(mHelper);
        monsterDao = new MonsterDao(mHelper);
        itemDao = new ItemDao(mHelper);
        huntingRewardsDao = new HuntingRewardsDao(mHelper);
        gatheringDao = new GatheringDao(mHelper);
    }
    
    public static DataManager get(Context c) {
        if (sDataManager == null) {
            // Use the application context to avoid leaking activities
            sDataManager = new DataManager(c.getApplicationContext());
        }
        return sDataManager;
    }

    
/********************************* ARMOR QUERIES ******************************************/    

    public List<ArmorMetadata> getArmorSetMetadataByFamily(long familyId) {
        return metadataDao.queryArmorSetMetadataByFamily(familyId);
    }


    public List<ArmorMetadata> getArmorSetMetadataByArmor(long armorId) {
        return metadataDao.queryArmorSetMetadataByArmor(armorId);
    }

    /* Get a Cursor that has a list of all Armors */
    public ArmorCursor queryArmor() {
        return itemDao.queryArmor();
    }
    
    /* Get a specific Armor */
    public Armor getArmor(long id) {
        return itemDao.queryArmor(id);
    }
    
    /* Get an array of Armor based on hunter type */
    public List<Armor> queryArmorArrayType(int type) {
        ArmorCursor cursor = itemDao.queryArmorType(type);
        return MHUtils.cursorToList(cursor, ArmorCursor::getArmor);
    }
    
/********************************* COMBINING QUERIES ******************************************/
    /* Get a Cursor that has a list of all Combinings */
    public CombiningCursor queryCombinings() {
        return itemDao.queryCombinings();
    }

    public CombiningCursor queryCombiningOnItemID(long id) {
         return itemDao.queryCombinationsOnItemID(id);
    }
    
/********************************* COMPONENT QUERIES ******************************************/
    /* Get a Cursor that has a list of Components based on the created Item */
    public ComponentCursor queryComponentCreated(long id) {
        return mHelper.queryComponentCreated(id);
    }

    /* Get a Cursor that has a list of Components based on the component Item */
    public ComponentCursor queryComponentComponent(long id) {
        return mHelper.queryComponentComponent(id);
    }

    /* Get an array of paths for a created Item */
    public ArrayList<String> queryComponentCreateImprove(long id) {
        // Gets all the component Items
        ComponentCursor cursor = mHelper.queryComponentCreated(id);
        cursor.moveToFirst();
        
        ArrayList<String> paths = new ArrayList<String>();
        
        // Only get distinct paths
        while (!cursor.isAfterLast()) {
            String type = cursor.getComponent().getType();
            
            // Check if not a duplicate
            if(!paths.contains(type)) {
                paths.add(type);
            }
            
            cursor.moveToNext();
        }
        
        cursor.close();
        return paths;
    }
    
/********************************* DECORATION QUERIES ******************************************/
    /* Get a Cursor that has a list of all Decorations */
    public DecorationCursor queryDecorations() {
        return mHelper.queryDecorations();
    }

    /**
     * Gets a cursor that has a list of decorations that pass the filter.
     * Having a null or empty filter is the same as calling without a filter
     */
    public DecorationCursor queryDecorationsSearch(String filter) {
        filter = (filter == null) ? "" : filter.trim();
        if (filter.equals(""))
            return queryDecorations();
        return mHelper.queryDecorationsSearch(filter);
    }

    /* Get a specific Decoration */
    public Decoration getDecoration(long id) {
        Decoration decoration = null;
        DecorationCursor cursor = mHelper.queryDecoration(id);
        cursor.moveToFirst();
        
        if (!cursor.isAfterLast())
            decoration = cursor.getDecoration();
        cursor.close();
        return decoration;
    }
    
/********************************* GATHERING QUERIES ******************************************/
    /* Get a Cursor that has a list of Gathering based on Item */
    public GatheringCursor queryGatheringItem(long id) {
        return mHelper.queryGatheringItem(id);
    }

    /* Get a Cursor that has a list of Gathering based on Location */
    public GatheringCursor queryGatheringLocation(long id) {
        return mHelper.queryGatheringLocation(id);
    }
    
    /* Get a Cursor that has a list of Gathering based on Location and Quest rank */
    public GatheringCursor queryGatheringLocationRank(long id, String rank) {
        return mHelper.queryGatheringLocationRank(id, rank);
    }

    public GatheringCursor queryGatheringForQuest(long questId, long locationId, String rank) {
        return gatheringDao.queryGatheringsForQuest(questId,locationId,rank);
    }

    
/********************************* HUNTING REWARD QUERIES ******************************************/

    /* Get a Cursor that has a list of HuntingReward based on Item */
    public HuntingRewardCursor queryHuntingRewardItem(long id) {
        return huntingRewardsDao.queryHuntingRewardItem(id);
    }

    /* Get a Cursor that has a list of HuntingReward based on Monster */
    public HuntingRewardCursor queryHuntingRewardMonster(long id) {
        return huntingRewardsDao.queryHuntingRewardMonster(id);
    }

    /* Get a Cursor that has a list of HuntingReward based on Monster and Rank */
    public HuntingRewardCursor queryHuntingRewardMonsterRank(long id, String rank) {
        return huntingRewardsDao.queryHuntingRewardMonsterRank(id, rank);
    }
    
/********************************* ITEM QUERIES ******************************************/

    /**
     * Performs a query to receive item metadata
     * @param id
     * @return
     */
    public ItemMetadata queryItemMetadata(long id) {
        return metadataDao.queryItemMetadata(id);
    }

    /* Get a Cursor that has a list of all Items */
    public ItemCursor queryItems() {
        return itemDao.queryItems();
    }
    
    /* Get a specific Item */
    public Item getItem(long id) {
        return itemDao.queryItem(id);
    }
    
    /* Get a Cursor that has a list of filtered Items through search */
    public ItemCursor queryItemSearch(String search) {
        return itemDao.queryItemSearch(search);
    }
    
/********************************* ITEM TO SKILL TREE QUERIES ******************************************/
    /* Get a Cursor that has a list of ItemToSkillTree based on Item */
    public ItemToSkillTreeCursor queryItemToSkillTreeItem(long id) {
        return mHelper.queryItemToSkillTreeItem(id);
    }
    
    /* Get a Cursor that has a list of ItemToSkillTree based on SkillTree */
    public ItemToSkillTreeCursor queryItemToSkillTreeSkillTree(long id, String type) {
        return mHelper.queryItemToSkillTreeSkillTree(id, type);
    }

    /** Get an array of ItemToSkillTree based on Item */
    public ArrayList<ItemToSkillTree> queryItemToSkillTreeArrayItem(long id) {
        ArrayList<ItemToSkillTree> itst = new ArrayList<ItemToSkillTree>();
        ItemToSkillTreeCursor cursor = mHelper.queryItemToSkillTreeItem(id);
        cursor.moveToFirst();
        
        while(!cursor.isAfterLast()) {
            itst.add(cursor.getItemToSkillTree());
            cursor.moveToNext();
        }
        cursor.close();
        return itst;
    }

/********************************* ITEM TO MATERIAL QUERIES ******************************************/

    public ItemToMaterialCursor queryItemsForMaterial(long mat_item_id){
        return mHelper.queryItemsForMaterial(mat_item_id);
    }

/********************************* LOCATION QUERIES ******************************************/
    /* Get a Cursor that has a list of all Locations */
    public LocationCursor queryLocations() {
        return mHelper.queryLocations();
    }

    /* Get a specific Location */
    public Location getLocation(long id) {
        Location location = null;
        LocationCursor cursor = mHelper.queryLocation(id);
        cursor.moveToFirst();
        
        if (!cursor.isAfterLast())
            location = cursor.getLocation();
        cursor.close();
        return location;
    }

/********************************* MELODY QUERIES ******************************************/

    /* Get a Cursor that has a list of all Melodies from a specific set of notes */
    public HornMelodiesCursor queryMelodiesFromNotes(String notes) {
        return mHelper.queryMelodiesFromNotes(notes);
    }


/********************************* MONSTER QUERIES ******************************************/
    /* Get a Cursor that has a list of all Monster */

    public MonsterMetadata queryMonsterMetadata(long id) {
        return metadataDao.queryMonsterMetadata(id);
    }

    public MonsterCursor queryMonsters() {
        return monsterDao.queryMonsters();
    }
    
    /* Get a Cursor that has a list of all small Monster */    
    public MonsterCursor querySmallMonsters() {
        return monsterDao.queryMonsters(MonsterSize.SMALL);
    }

    /* Get a Cursor that has a list of all large Monster */    
    public MonsterCursor queryLargeMonsters() {
        return monsterDao.queryMonsters(MonsterSize.LARGE);
    }

    public MonsterCursor queryMonstersSearch(String searchTerm) {
        return monsterDao.queryMonstersSearch(searchTerm);
    }

    /* Get a specific Monster */
    public Monster getMonster(long id) {
        return monsterDao.queryMonster(id);
    }

/********************************* MONSTER AILMENT QUERIES ******************************************/
    /* Get a cursor that lists all the ailments a particular monster can inflict */
    public MonsterAilmentCursor queryAilmentsFromId(long id){
        return mHelper.queryAilmentsFromMonster(id);
    }

/********************************* MONSTER DAMAGE QUERIES ******************************************/    
    /* Get a Cursor that has a list of MonsterDamage for a specific Monster */
    public MonsterDamageCursor queryMonsterDamage(long id) {
        return mHelper.queryMonsterDamage(id);
    }
    
    /* Get an array of MonsterDamage for a specific Monster */
    public List<MonsterDamage> queryMonsterDamageArray(long id) {
        MonsterDamageCursor cursor = queryMonsterDamage(id);
        return MHUtils.cursorToList(cursor, MonsterDamageCursor::getMonsterDamage);
    }

/********************************* MONSTER STATUS QUERIES ******************************************/
    /* Get an array of status objects for a monster */
    public List<MonsterStatus> queryMonsterStatus(long id) {
        MonsterStatusCursor cursor = mHelper.queryMonsterStatus(id);
        return MHUtils.cursorToList(cursor, MonsterStatusCursor::getStatus);
    }
        
/********************************* MONSTER TO QUEST QUERIES ******************************************/
    /* Get a Cursor that has a list of MonsterToQuest based on Monster */
    public MonsterToQuestCursor queryMonsterToQuestMonster(long id) {
        return mHelper.queryMonsterToQuestMonster(id);
    }

    /* Get a Cursor that has a list of MonsterToQuest based on Quest */
    public MonsterToQuestCursor queryMonsterToQuestQuest(long id) {
        return mHelper.queryMonsterToQuestQuest(id);
    }

/********************************* MONSTER HABITAT QUERIES ******************************************/
    /* Get a Cursor that has a list of MonsterHabitats based on Monster */
    public MonsterHabitatCursor queryHabitatMonster(long id) {
        return mHelper.queryHabitatMonster(id);
    }

    /* Get a Cursor that has a list of MonsterHabitats based on Location */
    public MonsterHabitatCursor queryHabitatLocation(long id) {
        return mHelper.queryHabitatLocation(id);
    }

/********************************* MONSTER WEAKNESS QUERIES ******************************************/

    /* Get a cursor that has all a monsters weaknesses */
    public MonsterWeaknessCursor queryWeaknessFromMonster(long id){
        return mHelper.queryWeaknessFromMonster(id);
    }

    /* Get an array of MonsterWeakness for a specific Monster */
    public List<MonsterWeakness> queryMonsterWeaknessArray(long id) {
        MonsterWeaknessCursor cursor = queryWeaknessFromMonster(id);
        return MHUtils.cursorToList(cursor, MonsterWeaknessCursor::getWeakness);
    }

/********************************* QUEST QUERIES ******************************************/    

    /* Get a Cursor that has a list of all Quests */
    public QuestCursor queryQuests() {
        return mHelper.queryQuests();
    }

    public QuestCursor queryQuestsSearch(String searchTerm) {
        return mHelper.queryQuestsSearch(searchTerm);
    }

    /* Get a specific Quests */
    public Quest getQuest(long id) {
        Quest quest = null;
        QuestCursor cursor = mHelper.queryQuest(id);
        cursor.moveToFirst();
        
        if (!cursor.isAfterLast())
            quest = cursor.getQuest();
        cursor.close();
        return quest;
    }
    
    /* Get an array of Quest based on hub */
    public List<Quest> queryQuestArrayHub(String hub) {
        QuestCursor cursor = mHelper.queryQuestHub(hub);
        return MHUtils.cursorToList(cursor, QuestCursor::getQuest);
    }
    
    /* Get a Cursor that has a list of Quest based on hub */
    public QuestCursor queryQuestHub(String hub) {
        return mHelper.queryQuestHub(hub);
    }

        /* Get a Cursor that has a list of Quest based on hub and stars */
    public QuestCursor queryQuestHubStar(String hub, String stars) {
        return mHelper.queryQuestHubStar(hub, stars);
    }
    
/********************************* QUEST REWARD QUERIES ******************************************/
    /* Get a Cursor that has a list of QuestReward based on Item */
    public QuestRewardCursor queryQuestRewardItem(long id) {
        return mHelper.queryQuestRewardItem(id);
    }
    
    /* Get a Cursor that has a list of QuestReward based on Quest */
    public QuestRewardCursor queryQuestRewardQuest(long id) {
        return mHelper.queryQuestRewardQuest(id);
    }
    
/********************************* SKILL QUERIES ******************************************/    

//    public SkillCursor querySkill(long id) {
//        return mHelper.querySkill(id);
//    }
    
    /* Get a Cursor that has a list of all Skills from a specific SkillTree */
    public SkillCursor querySkillFromTree(long id) {
        return mHelper.querySkillFromTree(id);
    }
        
/********************************* SKILL TREE QUERIES ******************************************/    
    /* Get a Cursor that has a list of all SkillTree */
    public SkillTreeCursor querySkillTrees() {
        return mHelper.querySkillTrees();
    }

    public SkillTreeCursor querySkillTreesSearch(String searchTerm) {
        return mHelper.querySkillTreesSearch(searchTerm);
    }

    /* Get a specific SkillTree */
    public SkillTree getSkillTree(long id) {
        SkillTree skillTree = null;
        SkillTreeCursor cursor = mHelper.querySkillTree(id);
        cursor.moveToFirst();
        
        if (!cursor.isAfterLast())
            skillTree = cursor.getSkillTree();
        cursor.close();
        return skillTree;
    }
    
/********************************* WEAPON QUERIES ******************************************/    
    /* Get a Cursor that has a list of all Weapons */
    public WeaponCursor queryWeapon() {
        return mHelper.queryWeapon();
    }

    /* Get a specific Weapon */
    public Weapon getWeapon(long id) {
        Weapon weapon = null;
        WeaponCursor cursor = mHelper.queryWeapon(id);
        cursor.moveToFirst();
        
        if (!cursor.isAfterLast())
            weapon = cursor.getWeapon();
        cursor.close();
        return weapon;
    }

    /* Get a Cursor that has a list of Weapons based on weapon type */
    public WeaponCursor queryWeaponType(String type) {
        return mHelper.queryWeaponType(type, false);
    }


    public String getWeaponType(long id){
        Cursor c = mHelper.queryWeaponTypeForWeapon(id);
        c.moveToFirst();
        String wtype = c.getString(0);
        c.close();
        return wtype;
    }

    /* Get an array of weapon expandable list items
    * */
    public ArrayList<WeaponListEntry> queryWeaponTreeArray(String type) {
        WeaponCursor cursor = mHelper.queryWeaponType(type, false);

        cursor.moveToFirst();
        ArrayList<WeaponListEntry> weapons = new ArrayList<WeaponListEntry>();
        HashMap<Long,WeaponListEntry> weaponDict = new HashMap<>();
        WeaponListEntry currentEntry;
        Weapon currentWeapon;

        while(!cursor.isAfterLast()){
            currentWeapon = cursor.getWeapon();
            currentEntry = new WeaponListEntry(currentWeapon);
            weaponDict.put(currentWeapon.getId(),currentEntry);
            cursor.moveToNext();
        }

        int parent_id;
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            currentWeapon = cursor.getWeapon();
            currentEntry = weaponDict.get(currentWeapon.getId());

            parent_id = currentWeapon.getParentId();
            if(parent_id != 0) {
                weaponDict.get((long) parent_id).addChild(currentEntry);
            }

            weapons.add(currentEntry);
            cursor.moveToNext();
        }

        return weapons;
    }

    /*
    * Get an array of weapon expandable list items consisting only of the final upgrades
    */
    public ArrayList<WeaponListEntry> queryWeaponTreeArrayFinal(String type) {
        WeaponCursor cursor = mHelper.queryWeaponType(type, true);

        ArrayList<WeaponListEntry> weapons = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            weapons.add(new WeaponListEntry(cursor.getWeapon()));
            cursor.moveToNext();
        }

        return weapons;
    }
    
    /* Get a Cursor that has a list of Weapons in the weapon tree for a specified weapon */
    public WeaponCursor queryWeaponTree(long id) {
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(id);            // Add specified weapon to returned array
        
        long currentId = id;
        WeaponTreeCursor cursor = null;
        
        // Get ancestors and add them at the beginning of the tree
        do {
            cursor = mHelper.queryWeaponTreeParent(currentId);
            cursor.moveToFirst();
            
            if(cursor.isAfterLast())
                break;
            
            currentId = cursor.getWeapon().getId();
            ids.add(0, currentId);
            
            cursor.close();
        }
        while (true);
        
        currentId = id;        // set current id back to specified weapon

        // Get children only; exclude descendants of children
        cursor = mHelper.queryWeaponTreeChild(currentId);
        cursor.moveToFirst();
        
        if(!cursor.isAfterLast()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                ids.add(cursor.getWeapon().getId());
                cursor.moveToNext();
            }
        }
        cursor.close();
        
        // Convert Arraylist to a regular array to return
        long[] idArray = new long[ids.size()];
        for (int i = 0; i < idArray.length; i++) {
            idArray[i] = ids.get(i);
        }

        return mHelper.queryWeapons(idArray);
        
    }

    public PalicoWeaponCursor queryPalicoWeapons(){
        return mHelper.queryPalicoWeapons();
    }

    public PalicoWeapon getPalicoWeapon(long id){
        PalicoWeaponCursor cursor = mHelper.queryPalicoWeapon(id);
        cursor.moveToFirst();
        PalicoWeapon w = cursor.getWeapon();
        cursor.close();
        return w;
    }
    
/********************************* WISHLIST QUERIES ******************************************/    
    /* Get a Cursor that has a list of all Wishlists */
    public WishlistCursor queryWishlists() {
        return mHelper.queryWishlists();
    }

    /* Get a specific Wishlist */
    public WishlistCursor queryWishlist(long id) {
        return mHelper.queryWishlist(id);
    }

    /* Add a new Wishlist with a given name */
    public void queryAddWishlist(String name) {
        mHelper.queryAddWishlist(name);
    }

    /* Update a specific Wishlist with a new name */
    public void queryUpdateWishlist(long id, String name) {
        mHelper.queryUpdateWishlist(id, name);
    }

    /* Delete a specific Wishlist */
    public void queryDeleteWishlist(long id) {
        mHelper.queryDeleteWishlist(id);
    }

    /* Copy a specific Wishlist into a new wishlist, including its entries */
    public void queryCopyWishlist(long id, String name) {
        long newId = mHelper.queryAddWishlist(name);
        
        // Get all of the entries from the copied wishlist
        WishlistDataCursor cursor = mHelper.queryWishlistData(id);
        cursor.moveToFirst();

        // Add all of the retrieved entries into the new wishlist
        while(!cursor.isAfterLast()) {
            WishlistData wishlist = cursor.getWishlistData();
            mHelper.queryAddWishlistDataAll(newId, wishlist.getItem().getId(), 
                    wishlist.getQuantity(), wishlist.getSatisfied(), wishlist.getPath());
            cursor.moveToNext();
        }
        cursor.close();

        // Get all of the components from the copied wishlist
        WishlistComponentCursor wcCursor = mHelper.queryWishlistComponents(id);
        wcCursor.moveToFirst();

        // Add all of the retrieved components into the new wishlist
        while(!wcCursor.isAfterLast()) {
            WishlistComponent wishlist = wcCursor.getWishlistComponent();
            mHelper.queryAddWishlistComponentAll(newId, wishlist.getItem().getId(), 
                    wishlist.getQuantity(), wishlist.getNotes());
            wcCursor.moveToNext();
        }
        wcCursor.close();
    }

    /* Get a specific Wishlist */
    public Wishlist getWishlist(long id) {
        Wishlist wishlist = null;
        WishlistCursor cursor = mHelper.queryWishlist(id);
        cursor.moveToFirst();
        
        if (!cursor.isAfterLast())
            wishlist = cursor.getWishlist();
        cursor.close();
        return wishlist;
    }
    
/********************************* WISHLIST DATA QUERIES ******************************************/    
    /* Get a Cursor that has a list of WishlistData based on Wishlist */
    public WishlistDataCursor queryWishlistData(long id) {
        return mHelper.queryWishlistData(id);
    }
    
    /* Add an entry to a specific wishlist with the given item and quantity */
    public void queryAddWishlistData(long wishlist_id, long item_id, int quantity, String path) {

        WishlistDataCursor cursor = mHelper.queryWishlistData(wishlist_id, item_id, path);
        cursor.moveToFirst();

        if (cursor.isAfterLast()) {
            // Add new entry to wishlist_data
            mHelper.queryAddWishlistData(wishlist_id, item_id, quantity, path);
        }
        else {
            // Update existing entry
            WishlistData data = cursor.getWishlistData();
            long id = data.getId();
            int total = data.getQuantity() + quantity;
            
            mHelper.queryUpdateWishlistDataQuantity(id, total);
        }
        cursor.close();
        
        helperQueryAddWishlistData(wishlist_id, item_id, quantity, path);
        helperQueryUpdateWishlistSatisfied(wishlist_id);
    }
    
    /* Helper method: Add an entry to a wishlist, 
     *        and add the necessary components from the chosen path
     */
    private void helperQueryAddWishlistData(long wishlist_id, long item_id, int quantity, String path) {
        // Get the components for the entry
        ComponentCursor cc = mHelper.queryComponentCreatedType(item_id, path);
        cc.moveToFirst();
        
        WishlistComponentCursor wc = null;
        
        // Add each component to the wishlist component list
        while (!cc.isAfterLast()) {
            long component_id = cc.getComponent().getComponent().getId();
            int c_amt = (cc.getComponent().getQuantity()) * (quantity);
            
            wc = mHelper.queryWishlistComponent(wishlist_id, component_id);
            wc.moveToFirst();
            
            if (wc.isAfterLast()) {
                // Add component entry to wishlist_component
                mHelper.queryAddWishlistComponent(wishlist_id, component_id, c_amt);
            }
            else {
                // Update component entry to wishlist_component
                long wc_id = wc.getWishlistComponent().getId();
                int old_amt = wc.getWishlistComponent().getQuantity();

                mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt + c_amt);
            }
            wc.close();
            
            cc.moveToNext();
        }
        cc.close();
    }

    /* Update an entry to the given quantity */
    public void queryUpdateWishlistData(long id, int quantity) {
        
        // Get the existing entry from WishlistData
        WishlistDataCursor wdCursor = mHelper.queryWishlistDataId(id);
        wdCursor.moveToFirst();
        WishlistData wd = wdCursor.getWishlistData();
        wdCursor.close();
        
        long wishlist_id = wd.getWishlistId();
        long item_id = wd.getItem().getId();
        int wd_old_quantity = wd.getQuantity();
        String path = wd.getPath();
        
        // Find the different between new and old quantities
        int diff_quantity = quantity - wd_old_quantity;
        
        // Get the components for the WishlistData entry
        ComponentCursor cc = mHelper.queryComponentCreatedType(item_id, path);
        cc.moveToFirst();
        
        // Update those components in WishlistComponent
        while (!cc.isAfterLast()) {
            long component_id = cc.getComponent().getComponent().getId();
            int c_amt = (cc.getComponent().getQuantity()) * (diff_quantity);
            
            WishlistComponentCursor wc = mHelper.queryWishlistComponent(wishlist_id, component_id);
            wc.moveToFirst();
            
            // Update component entry to wishlist_component
            long wc_id = wc.getWishlistComponent().getId();
            int old_amt = wc.getWishlistComponent().getQuantity();
            
            mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt + c_amt);
            
            wc.close();
            cc.moveToNext();
        }
        cc.close();
        
        mHelper.queryUpdateWishlistDataQuantity(id, quantity);
        
        // Check for any changes if any WishlistData is satisfied (can be build)
        helperQueryUpdateWishlistSatisfied(wishlist_id);
    }

    /* Delete an entry from WishlistData */
    public void queryDeleteWishlistData(long id) {

        // Get the existing entry from WishlistData
        WishlistDataCursor wdCursor = mHelper.queryWishlistDataId(id);
        wdCursor.moveToFirst();
        WishlistData wd = wdCursor.getWishlistData();
        wdCursor.close();
        
        long wishlist_id = wd.getWishlistId();
        long item_id = wd.getItem().getId();
        int wd_old_quantity = wd.getQuantity();
        String path = wd.getPath();

        // Get the components for the WishlistData entry
        ComponentCursor cc = mHelper.queryComponentCreatedType(item_id, path);
        cc.moveToFirst();
        
        // Update those components in WishlistComponent
        while (!cc.isAfterLast()) {
            long component_id = cc.getComponent().getComponent().getId();
            int c_amt = (cc.getComponent().getQuantity()) * (wd_old_quantity);
            
            WishlistComponentCursor wc = mHelper.queryWishlistComponent(wishlist_id, component_id);
            wc.moveToFirst();
            
            // Update component entry to wishlist_component
            long wc_id = wc.getWishlistComponent().getId();
            int old_amt = wc.getWishlistComponent().getQuantity();
            
            int new_amt = old_amt - c_amt;
            
            if (new_amt > 0) {
                // Update wishlist_component if component is still needed
                mHelper.queryUpdateWishlistComponentQuantity(wc_id, old_amt - c_amt);
            }
            else {
                // If component no longer needed, delete it from wishlist_component
                mHelper.queryDeleteWishlistComponent(wc_id);
            }
            
            wc.close();
            cc.moveToNext();
        }
        cc.close();
        
        mHelper.queryDeleteWishlistData(id);
    }
    
    /* Get the total price/cost for the specified wishlist */
    public int queryWishlistPrice(long id) {
        int total = 0;        // total cost
        
        // Get all of the WishlistData from the wishlist
        WishlistDataCursor wdc = mHelper.queryWishlistData(id);
        wdc.moveToFirst();
        
        int buy;
        int quantity = 0;
        
        // Calculate cost for each WishlistData entry
        while(!wdc.isAfterLast()) {
            buy = 0;        // cost for entry
            WishlistData wd = wdc.getWishlistData();
            Item i = wd.getItem();
            String type = wd.getPath();
            
            // Check path if the entry is a Weapon
            if ((i.getType()).equals("Weapon")) {
                WeaponCursor wc = mHelper.queryWeapon(i.getId());
                wc.moveToFirst();
                
                // Get the cost from the desired path
                if (type.equals("Create"))
                    buy = wc.getWeapon().getCreationCost();
                else if (type.equals("Improve")) {
                    buy = wc.getWeapon().getUpgradeCost();
                }
                wc.close();
            }
            // For Armor and Decoration
            else {
                buy = wd.getItem().getBuy();
            }
            
            // Add the entry cost to total cost
            quantity = wd.getQuantity();
            total = total + (buy * quantity);
            
            wdc.moveToNext();
        }
        wdc.close();
        return total;
    }
    
/********************************* WISHLIST COMPONENT QUERIES ******************************************/    
    /* Get a Cursor that has a list of WishlistComponent based on Wishlist */
    public WishlistComponentCursor queryWishlistComponents(long id) {
        return mHelper.queryWishlistComponents(id);
    }    
    
    /* Update the specified WishlistComponent by the given quantity */
    public void queryUpdateWishlistComponentNotes(long id, int notes) {
        mHelper.queryUpdateWishlistComponentNotes(id, notes);
    }
    
    /* From a specified wishlist id, check if any WishlistData can be built */
    public void helperQueryUpdateWishlistSatisfied(long wishlist_id) {
        WishlistDataCursor wdc = mHelper.queryWishlistData(wishlist_id);
        wdc.moveToFirst();
        
        WishlistData wd = null;
        WishlistComponent wc = null;
        WishlistComponentCursor wcc = null;
        
        Component c = null;
        ComponentCursor cc = null;
        
        String path;
        long created_id;
        long component_id;
        int required_amt;
        int have_amt;
        int satisfied;
        
        // For every WishlistData
        while(!wdc.isAfterLast()) {
            satisfied  = 1;            // Set true until unsatisfied
            wd = wdc.getWishlistData();
            created_id = wd.getItem().getId();
            path = wd.getPath();
            
            cc = mHelper.queryComponentCreatedType(created_id, path);
            cc.moveToFirst();
            
            // For every component of the current WishlistData entry
            while(!cc.isAfterLast()) {
                c = cc.getComponent();
                component_id = c.getComponent().getId();
                
                wcc = mHelper.queryWishlistComponent(wishlist_id, component_id);
                wcc.moveToFirst();
                wc = wcc.getWishlistComponent();
                
                // Get the amounts
                required_amt = c.getQuantity();
                have_amt = wc.getNotes();

                // Check if user does not have enough materials
                if (have_amt < required_amt) {
                    satisfied = 0;
                    break;
                }
                
                wcc.close();    
                cc.moveToNext();
            }
            
            cc.close();
            
            // Update the WishlistData entry
            mHelper.queryUpdateWishlistDataSatisfied(wd.getId(), satisfied);
            wdc.moveToNext();
        }
        
        wdc.close();
    }

    /********************************* ARMOR SET BUILDER QUERIES ******************************************/

    public ASBSetCursor queryASBSets() {
        return mHelper.queryASBSets();
    }

    public ASBSet getASBSet(long id) {
        ASBSet set = null;
        ASBSetCursor cursor = mHelper.queryASBSet(id);
        cursor.moveToFirst();

        if (!cursor.isAfterLast())
            set = cursor.getASBSet();

        cursor.close();
        return set;
    }

    /** Get a cursor with a list of all armor sets. */
    public ASBSessionCursor queryASBSessions() {
        return mHelper.queryASBSessions();
    }

    /** Get a specific armor set. */
    public ASBSession getASBSession(long id) {
        ASBSession session = null;
        ASBSessionCursor cursor = mHelper.queryASBSession(id);
        cursor.moveToFirst();

        if (!cursor.isAfterLast())
            session = cursor.getASBSession(mAppContext);

        cursor.close();
        return session;
    }

    /** Adds a new ASB set to the list. */
    public void queryAddASBSet(String name, int rank,  int hunterType) {
        mHelper.queryAddASBSet(name, rank, hunterType);
    }

    /** Adds a new set that is a copy of the designated set to the list. */
    public void queryAddASBSet(long setId) {
        ASBSet set = getASBSet(setId);
        mHelper.queryAddASBSet(set.getName(), set.getRank(), set.getHunterType());
    }

    public void queryDeleteASBSet(long setId) {
        mHelper.queryDeleteASBSet(setId);
    }

    public void queryUpdateASBSet(long setId, String name, int rank, int hunterType) {
        mHelper.queryUpdateASBSet(setId, name, rank, hunterType);
    }

    public void queryPutASBSessionArmor(long asbSetId, long armorId, int pieceIndex) {
        mHelper.queryAddASBSessionArmor(asbSetId, armorId, pieceIndex);
    }

    public void queryRemoveASBSessionArmor(long asbSetId, int pieceIndex) {
        mHelper.queryAddASBSessionArmor(asbSetId, -1, pieceIndex);
    }

    public void queryPutASBSessionDecoration(long asbSetId, long decorationId, int pieceIndex, int decorationIndex) {
        mHelper.queryPutASBSessionDecoration(asbSetId, decorationId, pieceIndex, decorationIndex);
    }

    public void queryRemoveASBSessionDecoration(long asbSetId, int pieceIndex, int decorationIndex) {
        mHelper.queryPutASBSessionDecoration(asbSetId, -1, pieceIndex, decorationIndex);
    }

    public void queryCreateASBSessionTalisman(long asbSetId, int type, int slots, long skill1Id, int skill1Points, long skill2Id, int skill2Points) {
        mHelper.queryCreateASBSessionTalisman(asbSetId, type, slots, skill1Id, skill1Points, skill2Id, skill2Points);
    }

    public void queryRemoveASBSessionTalisman(long asbSetId) {
        mHelper.queryRemoveASBSessionTalisman(asbSetId);
    }

    /**************************** WYPORIUM TRADE DATA QUERIES *************************************/
        /* Get a Cursor that has a list of all wyporium trades */
    public WyporiumTradeCursor queryWyporiumTrades() {
        return mHelper.queryWyporiumTrades();
    }

    /* Get a specific wyporium trade */
    public WyporiumTrade getWyporiumTrade(long id) {
        WyporiumTrade wyporiumTrade = null;
        WyporiumTradeCursor cursor = mHelper.queryWyporiumTrades(id);
        cursor.moveToFirst();

        if (!cursor.isAfterLast())
            wyporiumTrade = cursor.getWyporiumTrade();
        cursor.close();
        return wyporiumTrade;
    }
}
