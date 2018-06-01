package com.ghstudios.android;

import com.ghstudios.android.mhgendatabase.R;

/**
 * Provides index values for each Navigation drawer item.
 * Used by each activity/fragment to return its index to GenericActionBarActivity.
 *
 * Integer values are fully compatible with the R.id.nav_* used in layouts
 *
 * @author habibokanla (01/03/15)
 */

public class MenuSection {
    public static final int NONE = 0;

    // mapped to nav ids for compatibility with NavigationView drawer items
    public static final int MONSTERS = R.id.nav_monsters;
    public static final int WEAPONS = R.id.nav_weapons;
    public static final int ARMOR = R.id.nav_armor;
    public static final int QUESTS = R.id.nav_quests;
    public static final int ITEMS = R.id.nav_items;
    public static final int PALICOS = R.id.nav_palicos;
    public static final int COMBINING = R.id.nav_combining;
    public static final int LOCATIONS = R.id.nav_locations;
    public static final int DECORATION = R.id.nav_decorations;
    public static final int SKILL_TREES = R.id.nav_skills;
    public static final int ARMOR_SET_BUILDER = R.id.nav_asb;
    public static final int WISH_LISTS = R.id.nav_wishlists;

}
