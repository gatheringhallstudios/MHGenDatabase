package com.ghstudios.android.ui.list;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.data.database.PalicoWeaponCursor;
import com.ghstudios.android.loader.PalicoWeaponListCursorLoader;
import com.ghstudios.android.loader.SQLiteCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.PalicoWeaponClickListener;
import com.ghstudios.android.ui.general.DrawSharpness;

import java.io.IOException;
import java.io.InputStream;

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
        PalicoWeaponCursorAdapter adapter = new PalicoWeaponCursorAdapter(getActivity(), cursor);
        setListAdapter(adapter);
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
            AssetManager manager = context.getAssets();

            // Set up the text view
            TextView weaponNameTextView = (TextView) view.findViewById(R.id.name_text);
            ImageView monsterImage = (ImageView) view.findViewById(R.id.item_image);

            TextView att_melee = (TextView)view.findViewById(R.id.attack_melee_text);
            TextView att_ranged = (TextView)view.findViewById(R.id.attack_ranged_text);

            ImageView element_melee = (ImageView)view.findViewById(R.id.element_melee_image);
            ImageView element_ranged = (ImageView)view.findViewById(R.id.element_ranged_image);
            TextView element_melee_text = (TextView)view.findViewById(R.id.element_melee_text);
            TextView element_ranged_text = (TextView)view.findViewById(R.id.element_ranged_text);
            element_melee.setVisibility(View.VISIBLE);
            element_ranged.setVisibility(View.VISIBLE);
            element_melee_text.setVisibility(View.VISIBLE);
            element_ranged_text.setVisibility(View.VISIBLE);

            TextView affinity_melee = (TextView)view.findViewById(R.id.affinity_melee_text);
            TextView affinity_ranged = (TextView)view.findViewById(R.id.affinity_ranged_text);
            affinity_melee.setVisibility(View.VISIBLE);
            affinity_ranged.setVisibility(View.VISIBLE);

            LinearLayout shaprnessLayout = (LinearLayout)view.findViewById(R.id.sharpness);

            TextView defense = (TextView)view.findViewById(R.id.defense_text);
            defense.setVisibility(View.VISIBLE);

            TextView balance = (TextView)view.findViewById(R.id.balance_text);
            balance.setText(wep.getBalanceString());

            LinearLayout  itemLayout = (LinearLayout) view.findViewById(R.id.clickable_layout);

            String cellText = wep.getItem().getName();
            String cellImage = "icons_weapons/" + wep.getItem().getFileLocation();

            weaponNameTextView.setText(cellText);

            // Read a Bitmap from Assets
            try {

                InputStream open = manager.open(cellImage);
                Bitmap bitmap = BitmapFactory.decodeStream(open);
                // Assign the bitmap to an ImageView in this layout
                monsterImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                monsterImage.setImageResource(android.R.color.transparent);
            }

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

                try {

                    InputStream open = manager.open("icons_monster_info/"+wep.getElement()+".png");
                    Bitmap bitmap = BitmapFactory.decodeStream(open);
                    element_melee.setImageBitmap(bitmap);
                    element_ranged.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    element_melee.setImageResource(android.R.color.transparent);
                    element_ranged.setImageResource(android.R.color.transparent);
                }
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
                default:
                    break;
            }

            shaprnessLayout.setBackgroundColor(color);

            itemLayout.setTag(wep.getId());
            itemLayout.setOnClickListener(new PalicoWeaponClickListener(context, wep.getId()));
        }
    }

}
