package com.ghstudios.android.features.palicos;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.AssetLoader;
import com.ghstudios.android.AssetRegistry;
import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.cursors.PalicoWeaponCursor;
import com.ghstudios.android.loader.PalicoWeaponListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ClickListeners.PalicoWeaponClickListener;
import com.ghstudios.android.components.DrawSharpness;

/**
 * Created by Joseph on 7/9/2016.
 */
public class PalicoWeaponListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generic_list, parent, false);
        //JOE:This list is never empty, so remove empty view to prevent flash
        View emptyView = v.findViewById(android.R.id.empty);
        ((ViewGroup)emptyView.getParent()).removeView(emptyView);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(R.id.palico_weapon_list_fragment, getArguments(), this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new PalicoWeaponListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(getListAdapter() == null) {
            PalicoWeaponCursorAdapter adapter = new PalicoWeaponCursorAdapter(getActivity(), cursor);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    class PalicoWeaponCursorAdapter extends CursorAdapter{

        PalicoWeaponCursor _cursor;

        public PalicoWeaponCursorAdapter(Context c, Cursor cursor){
            super(c,cursor,0);
            _cursor = (PalicoWeaponCursor) cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_palico_weapon_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            PalicoWeapon wep = _cursor.getWeapon();

            // Set up the text view
            TextView weaponNameTextView = view.findViewById(R.id.name_text);
            ImageView monsterImage = view.findViewById(R.id.item_image);

            TextView att_melee = view.findViewById(R.id.attack_melee_text);
            TextView att_ranged = view.findViewById(R.id.attack_ranged_text);

            ImageView element_melee = view.findViewById(R.id.element_melee_image);
            ImageView element_ranged = view.findViewById(R.id.element_ranged_image);
            TextView element_melee_text = view.findViewById(R.id.element_melee_text);
            TextView element_ranged_text = view.findViewById(R.id.element_ranged_text);
            element_melee.setVisibility(View.VISIBLE);
            element_ranged.setVisibility(View.VISIBLE);
            element_melee_text.setVisibility(View.VISIBLE);
            element_ranged_text.setVisibility(View.VISIBLE);

            TextView affinity_melee = view.findViewById(R.id.affinity_melee_text);
            TextView affinity_ranged = view.findViewById(R.id.affinity_ranged_text);
            affinity_melee.setVisibility(View.VISIBLE);
            affinity_ranged.setVisibility(View.VISIBLE);

            LinearLayout shaprnessLayout = view.findViewById(R.id.sharpness);

            TextView defense = view.findViewById(R.id.defense_text);
            defense.setVisibility(View.VISIBLE);

            TextView balance = view.findViewById(R.id.balance_text);
            balance.setText(wep.getBalanceString());

            LinearLayout  itemLayout = view.findViewById(R.id.clickable_layout);

            String cellText = wep.getItem().getName();
            weaponNameTextView.setText(cellText);

            AssetLoader.setIcon(monsterImage,wep.getItem());

            att_melee.setText(Integer.toString(wep.getAttackMelee()));
            att_ranged.setText(Integer.toString(wep.getAttackRanged()));


            if(wep.getElement().length()==0){
                element_melee.setVisibility(View.INVISIBLE);
                element_melee_text.setVisibility(View.INVISIBLE);
                element_ranged.setVisibility(View.INVISIBLE);
                element_ranged_text.setVisibility(View.INVISIBLE);
            }
            else{
                element_melee_text.setText(Integer.toString(wep.getElementMelee()));
                element_ranged_text.setText(Integer.toString(wep.getElementRanged()));

                int elementIconId = AssetRegistry.getElementRegistry().get(wep.getElementEnum());
                Drawable elementIcon = ContextCompat.getDrawable(context, elementIconId);

                element_melee.setImageDrawable(elementIcon);
                element_ranged.setImageDrawable(elementIcon);
            }

            if(wep.getAffinityMelee()==0)
                affinity_melee.setVisibility(View.INVISIBLE);
            else
                affinity_melee.setText(wep.getAffinityMelee()+"%");

            if(wep.getAffinityRanged()==0)
                affinity_ranged.setVisibility(View.INVISIBLE);
            else
                affinity_ranged.setText(wep.getAffinityRanged()+"%");

            if(wep.getDefense()==0)
                defense.setVisibility(View.GONE);
            else
                defense.setText("Def:"+Integer.toString(wep.getDefense()));

            int color = Color.BLACK;
            switch(wep.getSharpness()){
                case 1:
                    color = DrawSharpness.orangeColor;
                    break;
                case 2:
                    color= Color.YELLOW;
                    break;
                case 3:
                    color=Color.GREEN;
                    break;
                case 4:
                    color = DrawSharpness.blueColor;
                    break;
                case 5:
                    color = Color.WHITE;
                    break;
                case 6:
                    color = DrawSharpness.purpleColor;
                    break;
                default:
                    break;
            }

            shaprnessLayout.setBackgroundColor(color);

            itemLayout.setTag(wep.getId());
            itemLayout.setOnClickListener(new PalicoWeaponClickListener(context, wep.getId()));
        }
    }

}
