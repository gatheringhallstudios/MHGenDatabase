package com.ghstudios.android.ui.detail;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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
public class WeaponDetailAmmoFragment extends Fragment implements
        LoaderCallbacks<Weapon> {
    private static final String ARG_WEAPON_ID = "WEAPON_ID";
    private long mWeaponId;
    Weapon mWeapon;
    TextView[] mAmmoTextViews;

    public static WeaponDetailAmmoFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(ARG_WEAPON_ID, weaponId);
        WeaponDetailAmmoFragment f = new WeaponDetailAmmoFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.bowgun_ammo_fragment, getArguments(), this);
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
        View view = inflater.inflate(R.layout.fragment_weapon_bowgun_ammo,
                container, false);
        mAmmoTextViews = new TextView[]{
				(TextView) view.findViewById(R.id.normal1),
				(TextView) view.findViewById(R.id.normal2),
				(TextView) view.findViewById(R.id.normal3),
				(TextView) view.findViewById(R.id.pierce1),
				(TextView) view.findViewById(R.id.pierce2),
				(TextView) view.findViewById(R.id.pierce3),
				(TextView) view.findViewById(R.id.pellet1),
				(TextView) view.findViewById(R.id.pellet2),
				(TextView) view.findViewById(R.id.pellet3),
				(TextView) view.findViewById(R.id.crag1),
				(TextView) view.findViewById(R.id.crag2),
				(TextView) view.findViewById(R.id.crag3),
				(TextView) view.findViewById(R.id.clust1),
				(TextView) view.findViewById(R.id.clust2),
				(TextView) view.findViewById(R.id.clust3),
				(TextView) view.findViewById(R.id.flaming),
				(TextView) view.findViewById(R.id.water),
				(TextView) view.findViewById(R.id.thunder),
				(TextView) view.findViewById(R.id.freeze),
				(TextView) view.findViewById(R.id.dragon),
				(TextView) view.findViewById(R.id.poison1),
				(TextView) view.findViewById(R.id.poison2),
				(TextView) view.findViewById(R.id.para1),
				(TextView) view.findViewById(R.id.para2),
				(TextView) view.findViewById(R.id.sleep1),
				(TextView) view.findViewById(R.id.sleep2),
				(TextView) view.findViewById(R.id.exhaust1),
				(TextView) view.findViewById(R.id.exhaust2)
        };
        return view;
    }

    void updateUI(){
        String[] ammos = mWeapon.getAmmo().split("\\|");

        TextView ammoView;

        for(int i=0;i<mAmmoTextViews.length;i++) {
            ammoView = mAmmoTextViews[i];
            setAmmoText(ammos[i], ammoView);
        }
    }

    private void setAmmoText(String a, TextView ammoView) {
        Boolean loadUp = false;

        if (a.contains("*"))
        {
            loadUp = true;
            a = a.substring(0,a.length()-1);
        }

        ammoView.setText(a);

        if (loadUp) {
            ammoView.setTypeface(null, Typeface.BOLD);
            ammoView.setTextColor(ContextCompat.getColor(getContext(),R.color.text_color_focused));
        }
    }
}
