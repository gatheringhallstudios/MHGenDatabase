package com.ghstudios.android.features.combining;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.GenericActivity;
import com.ghstudios.android.MenuSection;

public class CombiningListActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.combining);

        // Tag as top level activity
        super.setAsTopLevel();
    }

    @Override
    protected int getSelectedSection() {
        return MenuSection.COMBINING;
    }

    @Override
    protected Fragment createFragment() {
        return new CombiningListFragment();
    }

}
