package com.ghstudios.android;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A class used to encapsulate tab's metadata for a pager.
 * Used by the GenericPagerAdapter
 */
public class PagerTab {
    private String title;
    private PagerTab.Factory builder;

    public PagerTab(@NonNull String title, @NonNull PagerTab.Factory builder) {
        if (title == null) {
            throw new NullPointerException("PagerTab title cannot be null");
        }
        if (builder == null) {
            throw new NullPointerException("builder cannot be null");
        }

        this.title = title;
        this.builder = builder;
    }

    /**
     * Returns this tab's title.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Builds and returns the fragment that this tab will display
     * @return
     */
    public Fragment buildFragment() {
        return builder.build();
    }

    /**
     * Defines an interface for a class that builds a fragment for a tab.
     */
    public interface Factory {
        /**
         * Executed to build the interface
         * @return
         */
        Fragment build();
    }
}
