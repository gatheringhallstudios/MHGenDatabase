package com.ghstudios.android.adapter.common;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.List;

/**
 * This is a general {@link android.support.v7.widget.RecyclerView} adapter that you
 * populate with more specific {@link AdapterDelegate}s. Accepts multiple AdapterDelegates
 * and creates an Adapter capable of displaying the appropriate any object of type T into a
 * RecyclerView as long as the appropriate AdapterDelegate is given.
 *
 * Example:
 * <pre>
 * {@code
 * // Setup Adapter to display Rewards, SectionHeaders, and SubHeaders.
 *      MonsterRewardAdapterDelegate rewardDelegate =
 *              new MonsterRewardAdapterDelegate(this::handleRewardSelection);
 *      SectionHeaderAdapterDelegate sectionHeaderDelegate =
 *              new SectionHeaderAdapterDelegate(this::handleSectionHeaderSelection);
 *      SubHeaderAdapterDelegate subHeaderDelegate =
 *              new SubHeaderAdapterDelegate(this::handleSubHeaderSelection);
 *
 *      adapter = new BasicListDelegationAdapter<>(rewardDelegate, sectionHeaderDelegate, subHeaderDelegate);
 * } </pre>
 */
public class BasicListDelegationAdapter<T> extends ListDelegationAdapter<List<T>> {
    public BasicListDelegationAdapter(AdapterDelegate<List<T>>... delegates) {
        for (AdapterDelegate<List<T>> delegate : delegates) {
            delegatesManager.addDelegate(delegate);
        }
    }
}
