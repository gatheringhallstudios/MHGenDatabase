package com.ghstudios.android.features.weapons.detail;

import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghstudios.android.SectionArrayAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBladeAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBowAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListBowgunAdapter;
import com.ghstudios.android.adapter.WeaponExpandableListGeneralAdapter;
import com.ghstudios.android.components.WeaponListEntry;
import com.ghstudios.android.data.classes.Weapon;
import com.ghstudios.android.mhgendatabase.R;

import java.util.List;

public class WeaponTreeFragment extends ListFragment{
    private static final String ARG_WEAPON_ID = "WEAPON_ID";
    private long mWeaponId;
    
    public static WeaponTreeFragment newInstance(long weaponId) {
        Bundle args = new Bundle();
        args.putLong(ARG_WEAPON_ID, weaponId);
        WeaponTreeFragment f = new WeaponTreeFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeaponDetailViewModel viewModel = new ViewModelProvider(getActivity()).get(WeaponDetailViewModel.class);

        viewModel.getFamilyTreeData().observe(this, this::populateFamilyTree);
    }

    void populateFamilyTree(List<WeaponFamilyWrapper> items){
        setListAdapter(new WeaponTreeListAdapter(getContext(),items));
    }

    /**
     * Internal adapter used to display the Weapon family trees.
     * The title of the WeaponFamilyWrapper decides the section the entry is displayed in.
     */
    private static class WeaponTreeListAdapter extends SectionArrayAdapter<WeaponFamilyWrapper> {

        public WeaponTreeListAdapter(Context context, List<WeaponFamilyWrapper> items) {
            super(context, items, R.layout.listview_header_strong);
        }

        @Override
        public String getGroupName(WeaponFamilyWrapper item) {
            return item.getGroup();
        }

        @Override
        public void bindView(View view, Context context, WeaponFamilyWrapper item) {
            // Get the weapon for the current row
            Weapon weapon = item.getWeapon();
            WeaponListEntry entry = new WeaponListEntry(weapon);
            if(item.getShowLevel()){
                entry.setIndentation(10);
                TextView level = view.findViewById(R.id.level);
                level.setText("Lv"+Long.toString(weapon.getParentId() & 0xFF));
                level.setVisibility(View.VISIBLE);
            }else{
                view.findViewById(R.id.level).setVisibility(View.INVISIBLE);
            }
            ((WeaponExpandableListGeneralAdapter.WeaponViewHolder) view.getTag()).bindView(context,entry);
        }

        @Override
        public View newView(Context context, WeaponFamilyWrapper item, ViewGroup parent) {
            // Use a layout inflater to get a row view
            LayoutInflater inflater = LayoutInflater.from(context);
            View v;

            Weapon w = item.getWeapon();
            if (w.getWtype().equals(Weapon.BOW)) {
                v = inflater.inflate(R.layout.fragment_weapon_tree_item_bow, parent, false);
                v.setTag(new WeaponExpandableListBowAdapter.WeaponBowViewHolder(v));
            }
            else if (w.getWtype().equals(Weapon.HEAVY_BOWGUN) || w.getWtype().equals(Weapon.LIGHT_BOWGUN)) {
                v = inflater.inflate(R.layout.fragment_weapon_tree_item_bowgun, parent, false);
                v.setTag(new WeaponExpandableListBowgunAdapter.WeaponBowgunViewHolder(v));
            }
            else {
                v = inflater.inflate(R.layout.fragment_weapon_tree_item_blademaster, parent, false);
                v.setTag(new WeaponExpandableListBladeAdapter.WeaponBladeViewHolder(v));
            }
            return v;
        }
    }

}
