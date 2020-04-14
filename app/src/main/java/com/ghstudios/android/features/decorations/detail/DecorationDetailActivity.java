package com.ghstudios.android.features.decorations.detail;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.MenuSection;

public class DecorationDetailActivity extends GenericActivity {
    /**
     * A key for passing a decoration ID as a long
     */
    public static final String EXTRA_DECORATION_ID =
            "com.daviancorp.android.android.ui.detail.decoration_id";

    private static final int REQUEST_ADD = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.type_decoration);
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.DECORATION;
    }

    @Override
    protected Fragment createFragment() {
        long decorationId = getIntent().getLongExtra(EXTRA_DECORATION_ID, -1);
        return DecorationDetailFragment.newInstance(decorationId);
    }
}
