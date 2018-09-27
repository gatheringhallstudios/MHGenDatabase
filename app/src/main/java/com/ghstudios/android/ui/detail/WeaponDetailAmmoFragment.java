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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.loader.WeaponLoader;
import com.ghstudios.android.mhgendatabaseold.R;

/**
 * Created by Joseph on 7/1/2016.
 */
public class WeaponDetailAmmoFragment extends Fragment implements
        LoaderCallbacks<Weapon> {
    private static final String ARG_WEAPON_ID = "WEAPON_ID";
    private long mWeaponId;
    Weapon mWeapon;
    TextView[] mAmmoTextViews;

    TextView[] _internalTextViews;
    TextView[] _rapidTextViews;
    TextView _rapidTitle;
    LinearLayout _internalLayout;

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

        _internalLayout = (LinearLayout)view.findViewById(R.id.internal_ammo_layout);
        _rapidTitle = (TextView)view.findViewById(R.id.detail_rapid_text);

        _rapidTextViews = new TextView[]{(TextView)view.findViewById(R.id.rapid_ammo_1),
                (TextView)view.findViewById(R.id.rapid_ammo_2),
                (TextView)view.findViewById(R.id.rapid_ammo_3),
                (TextView)view.findViewById(R.id.rapid_ammo_4),
                (TextView)view.findViewById(R.id.rapid_ammo_5)};


        _internalTextViews = new TextView[]{(TextView)view.findViewById(R.id.internal_ammo_1),
                (TextView)view.findViewById(R.id.internal_ammo_2),
                (TextView)view.findViewById(R.id.internal_ammo_3),
                (TextView)view.findViewById(R.id.internal_ammo_4),
                (TextView)view.findViewById(R.id.internal_ammo_5)};

        return view;
    }

    void updateUI(){
        String[] ammos = mWeapon.getAmmo().split("\\|");

        TextView ammoView;

        for(int i=0;i<mAmmoTextViews.length;i++) {
            ammoView = mAmmoTextViews[i];
            setAmmoText(ammos[i], ammoView);
        }

        _internalLayout.setVisibility(View.VISIBLE);

        String[] internal = mWeapon.getSpecialAmmo().split("\\*");
        String[] rapid = mWeapon.getRapidFire().split("\\*");

        //If there are no internal shots
        if(internal[0].length()==0) {
            _internalTextViews[0].setText(R.string.ammo_none);
            _internalTextViews[0].setVisibility(View.VISIBLE);
        }
        else {
            for (int i = 0; i < internal.length; i++) {
                String[] s = internal[i].split(":");
                _internalTextViews[i].setText(s[0] + this.getString(R.string.ammo_divider) + s[1] + this.getString(R.string.ammo_divider) + s[2]);
                _internalTextViews[i].setVisibility(View.VISIBLE);
            }
        }

        if(mWeapon.getWtype().equals("Light Bowgun")) {
            if(rapid[0].length()==0){
                _rapidTextViews[0].setText(R.string.ammo_none);
                _rapidTextViews[0].setVisibility(View.VISIBLE);
            }
            else {
                for (int i = 0; i < rapid.length; i++) {
                    String[] s = rapid[i].split(":");
                    _rapidTextViews[i].setText(s[0] + this.getString(R.string.ammo_divider) + s[1] + this.getString(R.string.ammo_divider) + s[2] + this.getString(R.string.ammo_divider) + getWaitString(Integer.parseInt(s[3])));
                    _rapidTextViews[i].setVisibility(View.VISIBLE);
                }
            }
        }
        else{
            _rapidTitle.setText(R.string.siege_mode);
            if(rapid[0].length()==0){
                _rapidTextViews[0].setText(R.string.ammo_none);
                _rapidTextViews[0].setVisibility(View.VISIBLE);
            }
            else {
                for (int i = 0; i < rapid.length; i++) {
                    String[] s = rapid[i].split(":");
                    _rapidTextViews[i].setText(s[0] + this.getString(R.string.ammo_divider) + s[1]);
                    _rapidTextViews[i].setVisibility(View.VISIBLE);
                }
            }
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

    String getWaitString(int wait){
        switch (wait){
            case 0:return this.getString(R.string.rapid_fire_short_wait);
            case 1:return this.getString(R.string.rapid_fire_medium_wait);
            case 2:return this.getString(R.string.rapid_fire_long_wait);
            case 3:return this.getString(R.string.rapid_fire_very_long_wait);
            default:return "";
        }
    }

}
