package com.ghstudios.android

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.StringRes
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ghstudios.android.mhgendatabase.R

import java.util.ArrayList

/**
 * Abstract Base Activity for implementing screens with multiple tabs.
 * Subclass this and call addTab() to set up hub pages.
 * NOTE: This is adapted from the BasePagerFragment from the world app.
 * If switching to single activity, replace for the fragment version.
 * Everything else is the same.
 */
abstract class BasePagerActivity : GenericActivity() {
    companion object {
        const val ARG_TAB_BEHAVIOR = "ARG_TAB_BEHAVIOR"
    }

    enum class TabBehavior {
        AUTO,
        FIXED,
        SCROLLING
    }

    private var behavior = TabBehavior.AUTO
    private var hideTabsIfSingularFlag: Boolean = false

    /**
     * Called when the fragment wants the tabs, but after Butterknife
     * has binded the view
     * @param tabs
     */
    abstract fun onAddTabs(tabs: TabAdder)

    override fun createFragment(): androidx.fragment.app.Fragment {
        return InnerPagerFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // restore tab behavior variable...or default to auto
        behavior = savedInstanceState?.getSerializable(ARG_TAB_BEHAVIOR) as TabBehavior?
                ?: TabBehavior.AUTO
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_TAB_BEHAVIOR, behavior)
    }

    /**
     * Function to reset pager tabs, using a callback with a provided tab adder
     */
    fun resetTabs(builder: (TabAdder) -> Unit) {
        val adder = InnerTabAdder(application.applicationContext)
        builder(adder)

        val fragment = this.detail as InnerPagerFragment
        fragment.resetTabs(adder.getTabs(), adder.defaultIdx)
    }

    /**
     * Sets the pager activity to hide the tab strip if there is only one fragment to show.
     * This is a one way operation, once enabled it cannot be disabled.
     */
    fun hideTabsIfSingular() {
        hideTabsIfSingularFlag = true
    }

    /**
     * Sets the currently selected tab to the tab index
     */
    fun setSelectedTab(tabIndex: Int) {
        val fragment = this.detail as? InnerPagerFragment
        fragment?.setSelectedTab(tabIndex)
    }

    /**
     * Sets the tab behavior. Defaults to AUTO.
     * Currently only works if set in onAddTabs. Midlife updates do nothing.
     */
    fun setTabBehavior(behavior: TabBehavior) {
        this.behavior = behavior

        // todo: update if already initialized
    }


    interface TabAdder {
        /**
         * Adds a tab to the fragment, including a drawable
         *
         * @param title   The title to display for the tab
         * @param builder A lambda that builds the tab fragment
         */
        fun addTab(title: String, icon: Drawable?, builder: () -> androidx.fragment.app.Fragment)

        /**
         * Adds a tab to the fragment
         * @param titleRes The string resource to use as a title
         * @param builder  A lambda that builds the tab fragment
         */
        fun addTab(@StringRes titleRes: Int, builder: () -> androidx.fragment.app.Fragment)

        /**
         * Adds a tab to the fragment.
         *
         * @param title   The title to display for the tab
         * @param builder A lambda that builds the tab fragment
         */
        fun addTab(title: String, builder: () -> androidx.fragment.app.Fragment)

        /**
         * Sets the default selected tab idx
         * @param idx
         */
        fun setDefaultItem(idx: Int)
    }

    /**
     * Internal pairing of a pager tab and icon.
     * Icons are defined at the view level, not the adapter level.
     * SpannableStrings have issues with tabs, even with textAllCaps false.
     */
    internal class PagerIconTab(
            val icon: Drawable?,
            val tab: PagerTab
    )

    /** Internal only implementation of the TabAdder  */
    private class InnerTabAdder(private val ctx: Context) : TabAdder {
        var defaultIdx = -1
            private set
        private val tabs = ArrayList<PagerIconTab>()

        override fun addTab(title: String, icon: Drawable?, builder: () -> androidx.fragment.app.Fragment) {
            tabs.add(PagerIconTab(icon, PagerTab(title, builder)))
        }

        override fun addTab(titleRes: Int, builder: () -> androidx.fragment.app.Fragment) {
            this.addTab(ctx.getString(titleRes), builder)
        }

        override fun addTab(title: String, builder: () -> androidx.fragment.app.Fragment) {
            this.addTab(title, null, builder)
        }

        override fun setDefaultItem(idx: Int) {
            // note: We're setting an atomic integer due to reassignment restrictions
            defaultIdx = idx
        }

        fun getTabs(): List<PagerIconTab> {
            return tabs
        }
    }

    /**
     * Internal fragment used to render the actual tab view.
     * Used by the BasePagerActivity.
     */
    internal class InnerPagerFragment : Fragment() {

        lateinit var tabLayout: TabLayout
        lateinit var viewPager: androidx.viewpager.widget.ViewPager

        private var initialized = false

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val v = inflater.inflate(R.layout.activity_pager, container, false)

            tabLayout = v.findViewById(R.id.tab_layout)
            viewPager = v.findViewById(R.id.pager)


            val activity = this.activity as BasePagerActivity

            // Setup tabs - the user should configure via the activity's onAddTabs function
            val adder = InnerTabAdder(context!!.applicationContext)
            activity.onAddTabs(adder)

            // get results from the onAddTabs call via the adder.
            val tabs = adder.getTabs()
            val defaultIdx = adder.defaultIdx

            if (!tabs.isEmpty()) {
                resetTabs(tabs, defaultIdx)
            }

            // Lets certain functions know that initialization has occured.
            initialized = true

            // Update the tab's icon color when the tab changes
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    styleTabIcon(tab, isSelected = true)
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    styleTabIcon(tab, isSelected = false)
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    onTabSelected(tab)
                }
            })

            return v
        }

        /**
         * Function to reset pager tabs using a list of PagerTab objects.
         */
        fun resetTabs(tabs: List<PagerIconTab>, selectedTabIdx: Int) {
            // Initialize ViewPager (tab behavior)
            viewPager.adapter = GenericPagerAdapter(this, tabs.map { it.tab })

            val parentActivity = activity as BasePagerActivity
            if (parentActivity.hideTabsIfSingularFlag && tabs.size <= 1) {
                // if there's only one tab and the flag is set, hide the tabs
                tabLayout.visibility = View.GONE
            } else {
                tabLayout.setupWithViewPager(viewPager)

                // check if we're over 4 tabs. If so, make scrollable.
                updateTabBehavior()

                if (selectedTabIdx > 0) {
                    viewPager.currentItem = selectedTabIdx
                }

                // Bind icons. Must be done after the viewpager is set up
                for ((idx, tab) in tabs.withIndex()) {
                    tabLayout.getTabAt(idx)?.icon = tab.icon
                    styleTabIcon(tabLayout.getTabAt(idx), isSelected = false)
                }

                styleTabIcon(tabLayout.getTabAt(selectedTabIdx), isSelected = true)
            }
        }

        /**
         * Internal helper to style the tab's icon based on selection state.
         * Note: If tab icon opacity can be controlled by a style, do that instead.
         */
        private fun styleTabIcon(tab: TabLayout.Tab?, isSelected: Boolean) = when (isSelected) {
            // mutate prevents changes to the cached icon version. Re-mutating does nothing, so this is safe
            true -> tab?.icon?.mutate()?.alpha = 255
            false -> tab?.icon?.mutate()?.alpha = 160 // todo: don't use magic number?
        }

        /**
         * Internal function to update certain behaviors based on the internal state.
         * Currently used to control whether the tabs should be fixed or scrolling.
         */
        private fun updateTabBehavior() {
            val parentActivity = activity as BasePagerActivity
            val type = parentActivity.behavior

            tabLayout.tabMode = when (type) {
                TabBehavior.FIXED -> TabLayout.MODE_FIXED
                TabBehavior.SCROLLING -> TabLayout.MODE_SCROLLABLE
                TabBehavior.AUTO -> when (tabLayout.tabCount > 4) {
                    true -> TabLayout.MODE_SCROLLABLE
                    false -> TabLayout.MODE_FIXED
                }
            }
        }

        /**
         * Sets the currently selected tab to the tab index.
         */
        fun setSelectedTab(tabIndex: Int) {
            viewPager.currentItem = tabIndex

            // if initialized, we need to update the tab behavior,
            // otherwise we can wait, it'll get updated later
            if (initialized) {
                updateTabBehavior()
            }
        }
    }
}
