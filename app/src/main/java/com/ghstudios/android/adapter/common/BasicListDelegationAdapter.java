package com.ghstudios.android.adapter.common;

import androidx.annotation.NonNull;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * This is a general {@link RecyclerView} adapter that you
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
public class BasicListDelegationAdapter<T> extends ListDelegationAdapter<List<? extends T>> {
    @SafeVarargs
    public BasicListDelegationAdapter(@NonNull AdapterDelegate<? extends List<? extends T>>... delegates) {
        for (AdapterDelegate<? extends List<? extends T>> delegate : delegates) {
            delegatesManager.addDelegate((AdapterDelegate<List<? extends T>>)delegate);
        }
    }

    public void setItems(List<? extends T> items) {
        super.setItems(Collections.unmodifiableList(items));
    }
}
