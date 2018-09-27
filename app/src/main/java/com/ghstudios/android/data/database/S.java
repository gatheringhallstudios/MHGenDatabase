package com.ghstudios.android.data.database;

/*
 * Class that only has constant variables
 * 
 * Note: Do not need to instantiate to use
 */
public class S {
	// weapon ids are 3 bytes. Most significant is weapon type, 2nd is family, and the least is level
	public static final long WEAPON_FAMILY_MASK = 0xFFFF00;

	// Armor
	public static final String TABLE_ARMOR = "armor";
	public static final String COLUMN_ARMOR_ID = "_id";
	public static final String COLUMN_ARMOR_SLOT = "slot";
	public static final String COLUMN_ARMOR_DEFENSE = "defense";
	public static final String COLUMN_ARMOR_MAX_DEFENSE = "max_defense";
	public static final String COLUMN_ARMOR_FIRE_RES = "fire_res";
	public static final String COLUMN_ARMOR_THUNDER_RES = "thunder_res";
	public static final String COLUMN_ARMOR_DRAGON_RES = "dragon_res";
	public static final String COLUMN_ARMOR_WATER_RES = "water_res";
	public static final String COLUMN_ARMOR_ICE_RES = "ice_res";
	public static final String COLUMN_ARMOR_GENDER = "gender";
	public static final String COLUMN_ARMOR_HUNTER_TYPE = "hunter_type";
	public static final String COLUMN_ARMOR_NUM_SLOTS = "num_slots";
	
	// Combining
	public static final String TABLE_COMBINING = "combining";
	public static final String COLUMN_COMBINING_ID = "_id";
	public static final String COLUMN_COMBINING_CREATED_ITEM_ID = "created_item_id";
	public static final String COLUMN_COMBINING_ITEM_1_ID = "item_1_id";
	public static final String COLUMN_COMBINING_ITEM_2_ID = "item_2_id";
	public static final String COLUMN_COMBINING_AMOUNT_MADE_MIN = "amount_made_min";
	public static final String COLUMN_COMBINING_AMOUNT_MADE_MAX = "amount_made_max";
	public static final String COLUMN_COMBINING_PERCENTAGE = "percentage";
	
	// Components
	public static final String TABLE_COMPONENTS = "components";
	public static final String COLUMN_COMPONENTS_ID = "_id";
	public static final String COLUMN_COMPONENTS_CREATED_ITEM_ID = "created_item_id";
	public static final String COLUMN_COMPONENTS_COMPONENT_ITEM_ID = "component_item_id";
	public static final String COLUMN_COMPONENTS_QUANTITY = "quantity";
	public static final String COLUMN_COMPONENTS_TYPE = "type";
	public static final String COLUMN_COMPONENTS_KEY = "key";
	
	// Decorations
	public static final String TABLE_DECORATIONS = "decorations";
	public static final String COLUMN_DECORATIONS_ID = "_id";
	public static final String COLUMN_DECORATIONS_NUM_SLOTS = "num_slots";
	
	// Gathering
	public static final String TABLE_GATHERING = "gathering";
	public static final String COLUMN_GATHERING_ID = "_id";
	public static final String COLUMN_GATHERING_ITEM_ID = "item_id";
	public static final String COLUMN_GATHERING_LOCATION_ID = "location_id";
	public static final String COLUMN_GATHERING_AREA = "area";
	public static final String COLUMN_GATHERING_SITE = "site";
	public static final String COLUMN_GATHERING_RANK = "rank";
	public static final String COLUMN_GATHERING_GROUP = "group_num";
	public static final String COLUMN_GATHERING_FIXED = "fixed";
	public static final String COLUMN_GATHERING_RARE = "rare";
    public static final String COLUMN_GATHERING_RATE = "percentage";
    public static final String COLUMN_GATHERING_QUANTITY = "quantity";
	
	// Hunting Rewards
	public static final String TABLE_HUNTING_REWARDS = "hunting_rewards";
	public static final String COLUMN_HUNTING_REWARDS_ID = "_id";
	public static final String COLUMN_HUNTING_REWARDS_ITEM_ID = "item_id";
	public static final String COLUMN_HUNTING_REWARDS_CONDITION = "condition";
	public static final String COLUMN_HUNTING_REWARDS_MONSTER_ID = "monster_id";
	public static final String COLUMN_HUNTING_REWARDS_RANK = "rank";
	public static final String COLUMN_HUNTING_REWARDS_STACK_SIZE = "stack_size";
	public static final String COLUMN_HUNTING_REWARDS_PERCENTAGE = "percentage";
	
	// Items
	public static final String TABLE_ITEMS = "items";
	public static final String COLUMN_ITEMS_ID = "_id";
	public static final String COLUMN_ITEMS_NAME = "name";
	public static final String COLUMN_ITEMS_JPN_NAME = "name_ja";
	public static final String COLUMN_ITEMS_TYPE = "type";
    public static final String COLUMN_ITEMS_SUB_TYPE = "sub_type";
	public static final String COLUMN_ITEMS_RARITY = "rarity";
	public static final String COLUMN_ITEMS_CARRY_CAPACITY = "carry_capacity";
	public static final String COLUMN_ITEMS_BUY = "buy";
	public static final String COLUMN_ITEMS_SELL = "sell";
	public static final String COLUMN_ITEMS_DESCRIPTION = "description";
	public static final String COLUMN_ITEMS_ICON_NAME = "icon_name";
	public static final String COLUMN_ITEMS_ICON_COLOR = "icon_color";
	public static final String COLUMN_ITEMS_ACCOUNT = "account";
	
	// Item to Skill Tree
	public static final String TABLE_ITEM_TO_SKILL_TREE = "item_to_skill_tree";
	public static final String COLUMN_ITEM_TO_SKILL_TREE_ID = "_id";
	public static final String COLUMN_ITEM_TO_SKILL_TREE_ITEM_ID = "item_id";
	public static final String COLUMN_ITEM_TO_SKILL_TREE_SKILL_TREE_ID = "skill_tree_id";
	public static final String COLUMN_ITEM_TO_SKILL_TREE_POINT_VALUE = "point_value";

	// Item to Materials
	public static final String TABLE_ITEM_TO_MATERIAL = "item_to_material";
	public static final String COLUMN_ITEM_TO_MATERIAL_ID = "_id";
	public static final String COLUMN_ITEM_TO_MATERIAL_MATERIAL_ID = "material_item_id";
	public static final String COLUMN_ITEM_TO_MATERIAL_ITEM_ID = "item_id";
	public static final String COLUMN_ITEM_TO_MATERIAL_AMOUNT = "amount";

	// Locations
	public static final String TABLE_LOCATIONS = "locations";
	public static final String COLUMN_LOCATIONS_ID = "_id";
	public static final String COLUMN_LOCATIONS_NAME = "name";
	public static final String COLUMN_LOCATIONS_MAP = "map";
	
	// Monster
	public static final String TABLE_MONSTERS = "monsters";
	public static final String COLUMN_MONSTERS_ID = "_id";
	public static final String COLUMN_MONSTERS_NAME = "name";
	public static final String COLUMN_MONSTERS_JPN_NAME = "name_ja";
	public static final String COLUMN_MONSTERS_CLASS = "class";
	public static final String COLUMN_MONSTERS_FILE_LOCATION = "icon_name";
	public static final String COLUMN_MONSTERS_SORT_NAME = "sort_name";
	public static final String COLUMN_MONSTERS_METADATA = "metadata";

    // Monster Habitat
    public static final String TABLE_HABITAT = "monster_habitat";
    public static final String COLUMN_HABITAT_ID = "_id";
    public static final String COLUMN_HABITAT_LOCATION_ID = "location_id";
    public static final String COLUMN_HABITAT_MONSTER_ID = "monster_id";
    public static final String COLUMN_HABITAT_START = "start_area";
    public static final String COLUMN_HABITAT_AREAS = "move_area";
    public static final String COLUMN_HABITAT_REST = "rest_area";

	// Monster Ailment
	public static final String TABLE_AILMENT = "monster_ailment";
	public static final String COLUMN_AILMENT_ID = "_id";
	public static final String COLUMN_AILMENT_MONSTER_ID = "monster_id";
	public static final String COLUMN_AILMENT_MONSTER_NAME = "monster_name";
	public static final String COLUMN_AILMENT_AILMENT = "ailment";

	// Monster Weakness
	public static final String TABLE_WEAKNESS = "monster_weakness";
	public static final String COLUMN_WEAKNESS_ID = "_id";
	public static final String COLUMN_WEAKNESS_MONSTER_ID = "monster_id";
	public static final String COLUMN_WEAKNESS_MONSTER_NAME = "monster_name";
	public static final String COLUMN_WEAKNESS_STATE = "state";
	public static final String COLUMN_WEAKNESS_FIRE = "fire";
	public static final String COLUMN_WEAKNESS_WATER = "water";
	public static final String COLUMN_WEAKNESS_THUNDER = "thunder";
	public static final String COLUMN_WEAKNESS_ICE = "ice";
	public static final String COLUMN_WEAKNESS_DRAGON = "dragon";
	public static final String COLUMN_WEAKNESS_POISON = "poison";
	public static final String COLUMN_WEAKNESS_PARALYSIS = "paralysis";
	public static final String COLUMN_WEAKNESS_SLEEP = "sleep";
	public static final String COLUMN_WEAKNESS_PITFALL_TRAP = "pitfall_trap";
	public static final String COLUMN_WEAKNESS_SHOCK_TRAP = "shock_trap";
	public static final String COLUMN_WEAKNESS_FLASH_BOMB = "flash_bomb";
	public static final String COLUMN_WEAKNESS_SONIC_BOMB = "sonic_bomb";
	public static final String COLUMN_WEAKNESS_DUNG_BOMB = "dung_bomb";
	public static final String COLUMN_WEAKNESS_MEAT = "meat";

    // Monster Status
    public static final String TABLE_MONSTER_STATUS = "monster_status";
    public static final String COLUMN_MONSTER_STATUS_MONSTER_ID = "monster_id";
    public static final String COLUMN_MONSTER_STATUS_STATUS = "status";
    public static final String COLUMN_MONSTER_STATUS_INITIAL = "initial";
    public static final String COLUMN_MONSTER_STATUS_INCREASE = "increase";
    public static final String COLUMN_MONSTER_STATUS_MAX = "max";
    public static final String COLUMN_MONSTER_STATUS_DURATION = "duration";
    public static final String COLUMN_MONSTER_STATUS_DAMAGE = "damage";

	// Monster Damage
	public static final String TABLE_MONSTER_DAMAGE = "monster_damage";
	public static final String COLUMN_MONSTER_DAMAGE_ID = "_id";
	public static final String COLUMN_MONSTER_DAMAGE_MONSTER_ID = "monster_id";
	public static final String COLUMN_MONSTER_DAMAGE_BODY_PART = "body_part";
	public static final String COLUMN_MONSTER_DAMAGE_CUT = "cut";
	public static final String COLUMN_MONSTER_DAMAGE_IMPACT = "impact";
	public static final String COLUMN_MONSTER_DAMAGE_SHOT = "shot";
	public static final String COLUMN_MONSTER_DAMAGE_FIRE = "fire";
	public static final String COLUMN_MONSTER_DAMAGE_WATER = "water";
	public static final String COLUMN_MONSTER_DAMAGE_ICE = "ice";
	public static final String COLUMN_MONSTER_DAMAGE_THUNDER = "thunder";
	public static final String COLUMN_MONSTER_DAMAGE_DRAGON = "dragon";
	public static final String COLUMN_MONSTER_DAMAGE_KO = "ko";
	
	// Monster to Quest
	public static final String TABLE_MONSTER_TO_QUEST = "monster_to_quest";
	public static final String COLUMN_MONSTER_TO_QUEST_ID = "_id";
	public static final String COLUMN_MONSTER_TO_QUEST_MONSTER_ID = "monster_id";
	public static final String COLUMN_MONSTER_TO_QUEST_QUEST_ID = "quest_id";
	public static final String COLUMN_MONSTER_TO_QUEST_UNSTABLE = "unstable";
	public static final String COLUMN_MONSTER_TO_QUEST_HYPER = "hyper";

	// Quests
	public static final String TABLE_QUESTS = "quests";
	public static final String COLUMN_QUESTS_ID = "_id";
	public static final String COLUMN_QUESTS_SORT_ORDER = "sort_order";
	public static final String COLUMN_QUESTS_NAME = "name";
	public static final String COLUMN_QUESTS_JPN_NAME = "name_ja";
	public static final String COLUMN_QUESTS_GOAL = "goal";
	public static final String COLUMN_QUESTS_HUB = "hub";
	public static final String COLUMN_QUESTS_TYPE = "type";
	public static final String COLUMN_QUESTS_STARS = "stars";
	public static final String COLUMN_QUESTS_RANK = "rank";
	public static final String COLUMN_QUESTS_LOCATION_ID = "location_id";
	public static final String COLUMN_QUESTS_TIME_LIMIT = "time_limit";
	public static final String COLUMN_QUESTS_FEE = "fee";
	public static final String COLUMN_QUESTS_REWARD = "reward";
	public static final String COLUMN_QUESTS_HRP = "hrp";
    public static final String COLUMN_QUESTS_SUB_GOAL = "sub_goal";
    public static final String COLUMN_QUESTS_SUB_REWARD = "sub_reward";
    public static final String COLUMN_QUESTS_SUB_HRP = "sub_hrp";
    public static final String COLUMN_QUESTS_GOAL_TYPE = "goal_type";
	public static final String COLUMN_QUESTS_HUNTER_TYPE = "hunter_type";
	public static final String COLUMN_QUESTS_FLAVOR = "flavor";
	public static final String COLUMN_QUESTS_METADATA = "metadata";
	public static final String COLUMN_QUESTS_PERMIT_MONSTER_ID="permit_monster_id";

	
	// Quest Rewards
	public static final String TABLE_QUEST_REWARDS = "quest_rewards";
	public static final String COLUMN_QUEST_REWARDS_ID = "_id";
	public static final String COLUMN_QUEST_REWARDS_QUEST_ID = "quest_id";
	public static final String COLUMN_QUEST_REWARDS_ITEM_ID = "item_id";
	public static final String COLUMN_QUEST_REWARDS_REWARD_SLOT = "reward_slot";
	public static final String COLUMN_QUEST_REWARDS_PERCENTAGE = "percentage";
	public static final String COLUMN_QUEST_REWARDS_STACK_SIZE = "stack_size";
	
	// Skills
	public static final String TABLE_SKILLS = "skills";
	public static final String COLUMN_SKILLS_ID = "_id";
	public static final String COLUMN_SKILLS_SKILL_TREE_ID = "skill_tree_id";
	public static final String COLUMN_SKILLS_REQUIRED_SKILL_TREE_POINTS = "required_skill_tree_points";
	public static final String COLUMN_SKILLS_NAME = "name";
	public static final String COLUMN_SKILLS_JPN_NAME = "name_ja";
	public static final String COLUMN_SKILLS_DESCRIPTION = "description";
	
	// Skill Trees
	public static final String TABLE_SKILL_TREES = "skill_trees";
	public static final String COLUMN_SKILL_TREES_ID = "_id";
	public static final String COLUMN_SKILL_TREES_NAME = "name";
	public static final String COLUMN_SKILL_TREES_JPN_NAME = "name_ja";
	
	// Trading
	public static final String TABLE_TRADING = "trading";
	public static final String COLUMN_TRADING_ID = "_id";
	public static final String COLUMN_TRADING_LOCATION_ID = "location_id";
	public static final String COLUMN_TRADING_OFFER_ITEM_ID = "offer_item_id";
	public static final String COLUMN_TRADING_RECEIVE_ITEM_ID = "receive_item_id";
	public static final String COLUMN_TRADING_PERCENTAGE = "percentage";
	
	// Weapons
	public static final String TABLE_WEAPONS = "weapons";
	public static final String COLUMN_WEAPONS_ID = "_id";
	public static final String COLUMN_WEAPONS_WTYPE = "wtype";
	public static final String COLUMN_WEAPONS_CREATION_COST = "creation_cost";
	public static final String COLUMN_WEAPONS_UPGRADE_COST = "upgrade_cost";
	public static final String COLUMN_WEAPONS_ATTACK = "attack";
	public static final String COLUMN_WEAPONS_MAX_ATTACK = "max_attack";
	public static final String COLUMN_WEAPONS_ELEMENT = "element";
	public static final String COLUMN_WEAPONS_AWAKEN = "awaken";
    public static final String COLUMN_WEAPONS_ELEMENT_2 = "element_2";
    public static final String COLUMN_WEAPONS_AWAKEN_ATTACK = "awaken_attack";
    public static final String COLUMN_WEAPONS_ELEMENT_ATTACK = "element_attack";
    public static final String COLUMN_WEAPONS_ELEMENT_2_ATTACK = "element_2_attack";
	public static final String COLUMN_WEAPONS_DEFENSE = "defense";
	public static final String COLUMN_WEAPONS_SHARPNESS = "sharpness";
	public static final String COLUMN_WEAPONS_AFFINITY = "affinity";
	public static final String COLUMN_WEAPONS_HORN_NOTES = "horn_notes";
	public static final String COLUMN_WEAPONS_SHELLING_TYPE = "shelling_type";
	public static final String COLUMN_WEAPONS_PHIAL = "phial";
	public static final String COLUMN_WEAPONS_CHARGES = "charges";
	public static final String COLUMN_WEAPONS_COATINGS = "coatings";
	public static final String COLUMN_WEAPONS_RECOIL = "recoil";
	public static final String COLUMN_WEAPONS_RELOAD_SPEED = "reload_speed";
	public static final String COLUMN_WEAPONS_RAPID_FIRE = "rapid_fire";
	public static final String COLUMN_WEAPONS_SPECIAL_AMMO = "special_ammo";
	public static final String COLUMN_WEAPONS_DEVIATION = "deviation";
	public static final String COLUMN_WEAPONS_AMMO = "ammo";
	public static final String COLUMN_WEAPONS_NUM_SLOTS = "num_slots";
	public static final String COLUMN_WEAPONS_FINAL = "final";
    public static final String COLUMN_WEAPONS_TREE_DEPTH = "tree_depth";
    public static final String COLUMN_WEAPONS_PARENT_ID = "parent_id";

	// Palico Weapons
	public static final String TABLE_PALICO_WEAPONS = "palico_weapons";
	public static final String COLUMN_PALICO_WEAPONS_ID = "_id";
	public static final String COLUMN_PALICO_WEAPONS_CREATION_COST = "creation_cost";
	public static final String COLUMN_PALICO_WEAPONS_ATTACK_MELEE = "attack_melee";
	public static final String COLUMN_PALICO_WEAPONS_ATTACK_RANGED = "attack_ranged";
	public static final String COLUMN_PALICO_WEAPONS_ELEMENT = "element";
	public static final String COLUMN_PALICO_WEAPONS_ELEMENT_MELEE = "element_melee";
	public static final String COLUMN_PALICO_WEAPONS_ELEMENT_RANGED = "element_ranged";
	public static final String COLUMN_PALICO_WEAPONS_DEFENSE = "defense";
	public static final String COLUMN_PALICO_WEAPONS_SHARPNESS = "sharpness";
	public static final String COLUMN_PALICO_WEAPONS_AFFINITY_MELEE = "affinity_melee";
	public static final String COLUMN_PALICO_WEAPONS_AFFINITY_RANGED = "affinity_ranged";
	public static final String COLUMN_PALICO_WEAPONS_BLUNT = "blunt";
	public static final String COLUMN_PALICO_WEAPONS_BALANCE = "balance";
	
	// Palico Armor
	public static final String TABLE_PALICO_ARMOR = "palico_armor";
	public static final String COLUMN_PALICO_ARMOR_ID = "_id";
	public static final String COLUMN_PALICO_ARMOR_DEFENSE = "defense";
	public static final String COLUMN_PALICO_ARMOR_FIRE_RES = "fire_res";
	public static final String COLUMN_PALICO_ARMOR_THUNDER_RES = "thunder_res";
	public static final String COLUMN_PALICO_ARMOR_DRAGON_RES = "dragon_res";
	public static final String COLUMN_PALICO_ARMOR_WATER_RES = "water_res";
	public static final String COLUMN_PALICO_ARMOR_ICE_RES = "ice_res";
	public static final String COLUMN_PALICO_ARMOR_FAMILY = "family";
	
    // Horn Melodies
    public static final String TABLE_HORN_MELODIES = "horn_melodies";
    public static final String COLUMN_HORN_MELODIES_ID = "_id";
    public static final String COLUMN_HORN_MELODIES_NOTES = "notes";
    public static final String COLUMN_HORN_MELODIES_SONG = "song";
    public static final String COLUMN_HORN_MELODIES_EFFECT_1 = "effect1";
    public static final String COLUMN_HORN_MELODIES_EFFECT_2 = "effect2";
    public static final String COLUMN_HORN_MELODIES_DURATION = "duration";
    public static final String COLUMN_HORN_MELODIES_EXTENSION = "extension";

	// Wishlist
	public static final String TABLE_WISHLIST = "wishlist";
	public static final String COLUMN_WISHLIST_ID = "_id";
	public static final String COLUMN_WISHLIST_NAME = "name";

	// Wishlist Data
	public static final String TABLE_WISHLIST_DATA = "wishlist_data";
	public static final String COLUMN_WISHLIST_DATA_ID = "_id";
	public static final String COLUMN_WISHLIST_DATA_WISHLIST_ID = "wishlist_id";
	public static final String COLUMN_WISHLIST_DATA_ITEM_ID = "item_id";
	public static final String COLUMN_WISHLIST_DATA_QUANTITY = "quantity";
	public static final String COLUMN_WISHLIST_DATA_SATISFIED = "satisfied";
	public static final String COLUMN_WISHLIST_DATA_PATH = "path";

	// Wishlist Component
	public static final String TABLE_WISHLIST_COMPONENT = "wishlist_component";
	public static final String COLUMN_WISHLIST_COMPONENT_ID = "_id";
	public static final String COLUMN_WISHLIST_COMPONENT_WISHLIST_ID = "wishlist_id";
	public static final String COLUMN_WISHLIST_COMPONENT_COMPONENT_ID = "component_id";
	public static final String COLUMN_WISHLIST_COMPONENT_QUANTITY = "quantity";
	public static final String COLUMN_WISHLIST_COMPONENT_NOTES = "notes";

    // Wyporium Trades
    public static final String TABLE_WYPORIUM_TRADE = "wyporium";
    public static final String COLUMN_WYPORIUM_TRADE_ID = "_id";
    public static final String COLUMN_WYPORIUM_TRADE_ITEM_IN_ID = "item_in_id";
    public static final String COLUMN_WYPORIUM_TRADE_ITEM_OUT_ID = "item_out_id";
    public static final String COLUMN_WYPORIUM_TRADE_UNLOCK_QUEST_ID = "unlock_quest_id";

	// Armor Sets
	public static final String TABLE_ASB_SETS = "asb_sets";

	public static final String COLUMN_ASB_SET_ID = "_id";

	public static final String COLUMN_ASB_SET_NAME = "name";
	public static final String COLUMN_ASB_SET_RANK = "rank";
	public static final String COLUMN_ASB_SET_HUNTER_TYPE = "hunter_type";

	public static final String COLUMN_ASB_WEAPON_SLOTS = "weapon_slots";
	public static final String COLUMN_ASB_WEAPON_DECORATION_1_ID = "weapon_decoration_1";
	public static final String COLUMN_ASB_WEAPON_DECORATION_2_ID = "weapon_decoration_2";
	public static final String COLUMN_ASB_WEAPON_DECORATION_3_ID = "weapon_decoration_3";

	public static final String COLUMN_HEAD_ARMOR_ID = "head_armor";
	public static final String COLUMN_HEAD_DECORATION_1_ID = "head_decoration_1";
	public static final String COLUMN_HEAD_DECORATION_2_ID = "head_decoration_2";
	public static final String COLUMN_HEAD_DECORATION_3_ID = "head_decoration_3";
	
	public static final String COLUMN_BODY_ARMOR_ID = "body_armor";
	public static final String COLUMN_BODY_DECORATION_1_ID = "body_decoration_1";
	public static final String COLUMN_BODY_DECORATION_2_ID = "body_decoration_2";
	public static final String COLUMN_BODY_DECORATION_3_ID = "body_decoration_3";
	
	public static final String COLUMN_ARMS_ARMOR_ID = "arms_armor";
	public static final String COLUMN_ARMS_DECORATION_1_ID = "arms_decoration_1";
	public static final String COLUMN_ARMS_DECORATION_2_ID = "arms_decoration_2";
	public static final String COLUMN_ARMS_DECORATION_3_ID = "arms_decoration_3";
	
	public static final String COLUMN_WAIST_ARMOR_ID = "waist_armor";
	public static final String COLUMN_WAIST_DECORATION_1_ID = "waist_decoration_1";
	public static final String COLUMN_WAIST_DECORATION_2_ID = "waist_decoration_2";
	public static final String COLUMN_WAIST_DECORATION_3_ID = "waist_decoration_3";
	
	public static final String COLUMN_LEGS_ARMOR_ID = "legs_armor";
	public static final String COLUMN_LEGS_DECORATION_1_ID = "legs_decoration_1";
	public static final String COLUMN_LEGS_DECORATION_2_ID = "legs_decoration_2";
	public static final String COLUMN_LEGS_DECORATION_3_ID = "legs_decoration_3";

	public static final String COLUMN_TALISMAN_EXISTS = "talisman_exists";
	public static final String COLUMN_TALISMAN_TYPE = "talisman_type";
	public static final String COLUMN_TALISMAN_SLOTS = "talisman_slots";
	public static final String COLUMN_TALISMAN_DECORATION_1_ID = "talisman_decoration_1";
	public static final String COLUMN_TALISMAN_DECORATION_2_ID = "talisman_decoration_2";
	public static final String COLUMN_TALISMAN_DECORATION_3_ID = "talisman_decoration_3";
	public static final String COLUMN_TALISMAN_SKILL_1_ID = "talisman_skill_1";
	public static final String COLUMN_TALISMAN_SKILL_1_POINTS = "talisman_skill_1_points";
	public static final String COLUMN_TALISMAN_SKILL_2_ID = "talisman_skill_2";
	public static final String COLUMN_TALISMAN_SKILL_2_POINTS = "talisman_skill_2_points";
}
