package com.ghstudios.android.ui.detail;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.loader.WeaponLoader;
import com.ghstudios.android.mhgendatabase.R;

/**
 * Created by Joseph on 7/1/2016.
 */
public class WeaponDetailCoatingFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Weapon> {
    private static final String ARG_WEAPON_ID = "WEAPON_ID";
    private long mWeaponId;
    Weapon mWeapon;
    TextView[] mCoatingTextViews;

    public static WeaponDetailCoatingFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(ARG_WEAPON_ID, weaponId);
        WeaponDetailCoatingFragment f = new WeaponDetailCoatingFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.bow_coating_fragment, getArguments(), this);
    }

    @Override
    public Loader<Weapon> onCreateLoader(int id, Bundle args) {
        // You only ever load the runs, so assume this is the case
        mWeaponId = args.getLong(ARG_WEAPON_ID, -1);
        return new WeaponLoader(getActivity(), mWeaponId);
    }

    @Override
    public void onLoadFinished(Loader<Weapon> loader, Weapon data) {
        mWeapon = data;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Weapon> loader) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weapon_coatings_fragment,
                container, false);
		mCoatingTextViews = new TextView[]{
				(TextView)view.findViewById(R.id.power_1_text),
				(TextView)view.findViewById(R.id.power_2_text),
				(TextView)view.findViewById(R.id.element_1_text),
				(TextView)view.findViewById(R.id.element_2_text),
				(TextView)view.findViewById(R.id.crange_text),
				(TextView)view.findViewById(R.id.poison_text),
				(TextView)view.findViewById(R.id.para_text),
				(TextView)view.findViewById(R.id.sleep_text),
				(TextView)view.findViewById(R.id.exhaust_text),
				(TextView)view.findViewById(R.id.blast_text),
				(TextView)view.findViewById(R.id.paint_text)
		};
        return view;
    }

    void updateUI(){
		/* Coatings */
		int coatings = Integer.parseInt(mWeapon.getCoatings());
		for(int i=10;i>=0;i--){
			boolean show = (coatings & (1 << i))>0;
			if(show){
				mCoatingTextViews[10-i].setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_focused));
				mCoatingTextViews[10-i].setTypeface(null,Typeface.BOLD);
			}
		}
    }
}
