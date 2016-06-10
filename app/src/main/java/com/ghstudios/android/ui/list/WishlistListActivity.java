package com.ghstudios.android.ui.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.general.GenericActivity;
import com.ghstudios.android.ui.list.adapter.MenuSection;

public class WishlistListActivity extends GenericActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.wishlist);

        // Tag as top level activity
        super.setAsTopLevel();

		/*FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

		if (fragment == null) {
			fragment = new WishlistListFragment();
			fm.beginTransaction().add(R.id.fragment_container, fragment)
					.commit();
		}*/
    }

    @Override
    protected MenuSection getSelectedSection() {
        return MenuSection.WISH_LISTS;
    }

    @Override
    protected Fragment createFragment() {
        super.detail = new WishlistListFragment();
        return super.detail;
    }

}
