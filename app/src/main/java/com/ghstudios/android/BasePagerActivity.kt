package com.ghstudios.android

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.ghstudios.android.mhgendatabase.R

import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Abstract Base Activity for implementing screens with multiple tabs.
 * Subclass this and call addTab() to set up hub pages.
 * NOTE: This is adapted from the BasePagerFragment from the world app.
 * If switching to single activity, replace for the fragment version.
 * Everything else is the same.
 */

abstract class BasePagerActivity : GenericActivity() {

    /**
     * Called when the fragment wants the tabs, but after Butterknife
     * has binded the view
     * @param tabs
     */
    abstract fun onAddTabs(tabs: TabAdder)

    override fun createFragment(): Fragment {
        return InnerPagerFragment()
    }

    /**
     * Function to reset pager tabs using a list of PagerTab objects.
     */
    @JvmOverloads
    fun resetTabs(tabs: List<PagerTab>, idx: Int = -1) {
        val fragment = this.detail as InnerPagerFragment
        fragment.resetTabs(tabs, idx)
    }

    interface TabAdder {
        /**
         * Adds a tab to the fragment
         *
         * @param title   The title to display for the tab
         * @param builder A TabFactory or lambda that builds the tab fragment
         */
        //fun addTab(title: String, builder: PagerTab.Factory)

        /**
         * Adds a tab to the fragment.
         */
        fun addTab(title: String, builder: () -> Fragment)

        /**
         * Sets the default selected tab idx
         * @param idx
         */
        fun setDefaultItem(idx: Int)
    }

    interface TabAddingFunction {
        operator fun invoke(adder: TabAdder)
    }

    /** Internal only implementation of the TabAdder  */
    private class InnerTabAdder : TabAdder {
        var defaultIdx = -1
            private set
        private val tabs = ArrayList<PagerTab>()

        override fun addTab(title: String, builder: () -> Fragment) {
            tabs.add(PagerTab(title, builder))
        }

        override fun setDefaultItem(idx: Int) {
            // note: We're setting an atomic integer due to reassignment restrictions
            defaultIdx = idx
        }

        fun getTabs(): List<PagerTab> {
            return tabs
        }
    }

    class InnerPagerFragment: Fragment() {

        lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val v = inflater.inflate(R.layout.activity_pager, container, false)

            tabLayout = v.findViewById(R.id.tab_layout)
            viewPager = v.findViewById(R.id.pager)

            val activity = this.activity as BasePagerActivity

            // Setup tabs
            val adder = InnerTabAdder()
            activity.onAddTabs(adder)

            // get results
            val tabs = adder.getTabs()
            val defaultIdx = adder.defaultIdx

            if (!tabs.isEmpty()) {
                resetTabs(tabs, defaultIdx)
            }

            return v
        }

        /**
         * Function to reset pager tabs using a list of PagerTab objects.
         */
        fun resetTabs(tabs: List<PagerTab>, idx: Int) {
            // check if we're over 4 tabs. If so, make scrollable. This is the old app behavior
            if (tabs.size > 4) {
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            } else {
                tabLayout.tabMode = TabLayout.MODE_FIXED
            }

            // Initialize ViewPager (tab behavior)
            viewPager.adapter = GenericPagerAdapter(this, tabs)
            tabLayout.setupWithViewPager(viewPager)

            if (idx > 0) {
                viewPager.currentItem = idx
            }
        }
    }
}
