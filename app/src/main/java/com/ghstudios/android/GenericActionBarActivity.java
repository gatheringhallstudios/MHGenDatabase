package com.ghstudios.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ghstudios.android.features.meta.AboutActivity;
import com.ghstudios.android.features.monsters.list.MonsterListPagerActivity;
import com.ghstudios.android.features.meta.PreferencesActivity;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.features.armorsetbuilder.list.ASBSetListPagerActivity;
import com.ghstudios.android.features.armor.list.ArmorListPagerActivity;
import com.ghstudios.android.features.combining.CombiningListActivity;
import com.ghstudios.android.features.decorations.list.DecorationListActivity;
import com.ghstudios.android.features.items.list.ItemListActivity;
import com.ghstudios.android.features.locations.LocationListActivity;
import com.ghstudios.android.features.palicos.PalicoPagerActivity;
import com.ghstudios.android.features.quests.QuestListPagerActivity;
import com.ghstudios.android.features.skills.SkillTreeListActivity;
import com.ghstudios.android.features.search.UniversalSearchActivity;
import com.ghstudios.android.features.weapons.WeaponSelectionListActivity;
import com.ghstudios.android.features.wishlist.list.WishlistListActivity;

import de.cketti.library.changelog.ChangeLog;

/*
 * Any subclass needs to:
 *  - override onCreate() to set title
 *  - override createFragment() for detail fragments
 */

public abstract class GenericActionBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = getClass().getSimpleName();

    protected static final String DIALOG_ABOUT = "about";

    private Handler mHandler;

    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    // is this activity top of the hierarchy?
    // defaults to false, use setAsTopLevel() to set to true
    private boolean isTopLevel = false;

    // start drawer in the closed position unless otherwise specified
    private static boolean drawerOpened = false;

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150; // Unused until fade out animation is properly implemented
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    public interface ActionOnCloseListener {
        void actionOnClose();

    }

    public ActionOnCloseListener actionOnCloseListener;


    /**
     * Method that returns the MenuSection value that the activity belongs to.
     * Override this to set the selected Drawer item.
     * @return
     */
    protected abstract int getSelectedSection();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display changelog on first run after update
        // On the initial release, there's no need for a changelog
        // todo: replace for something more up-to-date
        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }

        // Handler to implement drawer delay and runnable
        mHandler = new Handler();
    }

    /**
     * Sets this activity to be a top level activity.
     * Top level activities show a drawer menu in the action bar
     * instead of a back button.
     */
    public void setAsTopLevel(){
        isTopLevel = true;
        enableDrawerIndicator();
    }

    // Set up drawer toggle actions
    public void setupDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
                if (actionOnCloseListener != null) {
                    actionOnCloseListener.actionOnClose();
                    actionOnCloseListener = null;
                }
            }
        };

        // Setup the navigation view to handle our events
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(getSelectedSection());
        navigationView.setNavigationItemSelectedListener(this);

        // Enable menu button to toggle drawer
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //automatically open drawer on launch
        if (!drawerOpened) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            drawerOpened = true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // NOTE: We're keeping the below around in case we want to go back
        // to closing before navigating...I think I prefer snappier navigation tho
//                // Wait for drawer to close. This actually waits too long. Turn it into a runnable.
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        goToNavDrawerItem(position);
//                    }
//                }, NAVDRAWER_LAUNCH_DELAY);
//
//                mDrawerLayout.closeDrawers();

        Context ctx = GenericActionBarActivity.this;
        int itemId = item.getItemId();

        // Handle "Other" menu first.
        switch (itemId) {
            case R.id.settings:
                Intent preferences = new Intent(ctx, PreferencesActivity.class);
                startActivity(preferences);
                return true;

            case R.id.change_log:
                ChangeLog cl = new ChangeLog(this);
                cl.getFullLogDialog().show();
                return true;

            case R.id.about:
                Intent about = new Intent(ctx, AboutActivity.class);
                startActivity(about);
                return true;

        }

        // Set navigation actions
        Intent intent = null;

        switch (itemId) {
            case MenuSection.MONSTERS: // Monsters
                intent = new Intent(ctx, MonsterListPagerActivity.class);
                break;
            case MenuSection.WEAPONS: // Weapons
                intent = new Intent(ctx, WeaponSelectionListActivity.class);
                break;
            case MenuSection.ARMOR: // Armor
                intent = new Intent(ctx, ArmorListPagerActivity.class);
                break;
            case MenuSection.QUESTS: // Quests
                intent = new Intent(ctx, QuestListPagerActivity.class);
                break;
            case MenuSection.ITEMS: // Items
                intent = new Intent(ctx, ItemListActivity.class);
                break;
            case MenuSection.PALICOS:
                intent = new Intent(ctx, PalicoPagerActivity.class);
                break;
            case MenuSection.COMBINING: // Combining
                intent = new Intent(ctx, CombiningListActivity.class);
                break;
            case MenuSection.LOCATIONS: // Locations
                intent = new Intent(ctx, LocationListActivity.class);
                break;
            case MenuSection.DECORATION: // Decorations
                intent = new Intent(ctx, DecorationListActivity.class);
                break;
            case MenuSection.SKILL_TREES: // Skill Trees
                intent = new Intent(ctx, SkillTreeListActivity.class);
                break;
            case MenuSection.ARMOR_SET_BUILDER: // Armor Set Builder
                intent = new Intent(ctx, ASBSetListPagerActivity.class);
                break;
            case MenuSection.WISH_LISTS: // Wishlists
                intent = new Intent(ctx, WishlistListActivity.class);
                break;
            default:
                Log.e(TAG, "Failed navigation, invalid item id selected");
                return false;
        }

        // Clear the back stack whenever a nav drawer item is selected
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        final Intent finalIntent = intent;

        startActivity(finalIntent);

        // Clear default animation
        overridePendingTransition(0, 0);

        return true;
    }

    public void enableDrawerIndicator() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    public void disableDrawerIndicator() {
        mDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    // Sync button animation sync with drawer state
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Animate fade in
        View mainContent = findViewById(R.id.fragment_container);
        if(mainContent != null){
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navigationView != null) {
            navigationView.setCheckedItem(getSelectedSection());
        }
    }

    // Handle toggle state sync across configuration changes (rotation)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Detect navigation drawer item selected
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Detect home and or expansion menu item selections
        switch (item.getItemId()) {

            case android.R.id.home:
                // Detect back/up button is pressed
                // Finish current activity and pop it off the stack.
                // Basically a back button.
                this.finish();
                return true;

            case R.id.universal_search:
                Intent startSearch = new Intent(GenericActionBarActivity.this, UniversalSearchActivity.class);
                startActivity(startSearch);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            String title = mi.getTitle().toString();
            Spannable newTitle = new SpannableString(title);
            newTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0,
                    newTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mi.setTitle(newTitle);
        }
        return true;
    }

    /**
     * Overrides "back" to perform some black magic.
     * If this activity was flagged as "top level"
     */
    @Override
    public void onBackPressed() {
        // If back is pressed while drawer is open, close drawer.
        if (!isTopLevel && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
        else if (isTopLevel && !mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            // If this is a top level activity and drawer is closed, open drawer
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        else if (isTopLevel && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            // If top level and drawer is open, prompt for exit
            //Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    .setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_dialog)
                    .setPositiveButton(R.string.exit_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Stop the activity
                            finish();
                        }

                    })
                    .setNegativeButton(R.string.exit_cancel, null)
                    .show();
        }
        else{
            super.onBackPressed();
        }
    }

    /**
     * Indirectly calls onActivityResult, allows a generic way for a dialog
     * to return to both activities and fragments.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void sendActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }
}