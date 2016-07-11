package com.ghstudios.android.ui.detail;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghstudios.android.data.classes.ItemToMaterial;
import com.ghstudios.android.data.database.DataManager;
import com.ghstudios.android.data.database.ItemToMaterialCursor;
import com.ghstudios.android.loader.ItemToMaterialListCursorLoader;
import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.ui.ClickListeners.ArmorClickListener;
import com.ghstudios.android.ui.ClickListeners.DecorationClickListener;
import com.ghstudios.android.ui.ClickListeners.ItemClickListener;
import com.ghstudios.android.ui.ClickListeners.WeaponClickListener;

import java.io.IOException;

/**
 * Created by Joseph on 7/7/2016.
 */
public class MaterialDetailItemFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_ITEM_ID = "COMPONENT_ID";

    public static MaterialDetailItemFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, id);
        MaterialDetailItemFragment f = new MaterialDetailItemFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the loader to load the list of runs
        getLoaderManager().initLoader(R.id.material_item_list_fragment, getArguments(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_generic_list, container,false);
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // You only ever load the runs, so assume this is the case
        long mId = -1;
        if (args != null) {
            mId = args.getLong(ARG_ITEM_ID);
        }
        return new ItemToMaterialListCursorLoader(getActivity(), mId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Create an adapter to point at this cursor
        MaterialListCursorAdapter adapter = new MaterialListCursorAdapter(
                getActivity(), (ItemToMaterialCursor) cursor);
        setListAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the cursor (via the adapter)
        setListAdapter(null);
    }

    class MaterialListCursorAdapter extends CursorAdapter{
        ItemToMaterialCursor _cursor;

        public MaterialListCursorAdapter(Context c, ItemToMaterialCursor cur){
            super(c,cur,0);
            _cursor = cur;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.fragment_material_item_listitem,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ItemToMaterial mat = _cursor.GetItemToMaterial();
            LinearLayout itemLayout = (LinearLayout) view
                    .findViewById(R.id.listitem);
            ImageView itemImageView = (ImageView) view.findViewById(R.id.item_image);
            TextView itemTextView = (TextView) view.findViewById(R.id.item);
            TextView amtTextView = (TextView) view.findViewById(R.id.amt);

            String nameText = mat.getItem().getName();
            String amtText = "" + mat.getAmount();

            itemTextView.setText(nameText);
            amtTextView.setText(amtText);

            Drawable i = null;
            String cellImage;

            String sub_type = mat.getItem().getSubType();

            switch(sub_type){
                case "Head":
                    cellImage = "icons_armor/icons_head/head" + mat.getItem().getRarity() + ".png";
                    break;
                case "Body":
                    cellImage = "icons_armor/icons_body/body" + mat.getItem().getRarity() + ".png";
                    break;
                case "Arms":
                    cellImage = "icons_armor/icons_arms/arms" + mat.getItem().getRarity() + ".png";
                    break;
                case "Waist":
                    cellImage = "icons_armor/icons_waist/waist" + mat.getItem().getRarity() + ".png";
                    break;
                case "Legs":
                    cellImage = "icons_armor/icons_legs/legs" + mat.getItem().getRarity() + ".png";
                    break;
                case "Great Sword":
                    cellImage = "icons_weapons/icons_great_sword/great_sword" + mat.getItem().getRarity() + ".png";
                    break;
                case "Long Sword":
                    cellImage = "icons_weapons/icons_long_sword/long_sword" + mat.getItem().getRarity() + ".png";
                    break;
                case "Sword and Shield":
                    cellImage = "icons_weapons/icons_sword_and_shield/sword_and_shield" + mat.getItem().getRarity() + ".png";
                    break;
                case "Dual Blades":
                    cellImage = "icons_weapons/icons_dual_blades/dual_blades" + mat.getItem().getRarity() + ".png";
                    break;
                case "Hammer":
                    cellImage = "icons_weapons/icons_hammer/hammer" + mat.getItem().getRarity() + ".png";
                    break;
                case "Hunting Horn":
                    cellImage = "icons_weapons/icons_hunting_horn/hunting_horn" + mat.getItem().getRarity() + ".png";
                    break;
                case "Lance":
                    cellImage = "icons_weapons/icons_lance/lance" + mat.getItem().getRarity() + ".png";
                    break;
                case "Gunlance":
                    cellImage = "icons_weapons/icons_gunlance/gunlance" + mat.getItem().getRarity() + ".png";
                    break;
                case "Switch Axe":
                    cellImage = "icons_weapons/icons_switch_axe/switch_axe" + mat.getItem().getRarity() + ".png";
                    break;
                case "Charge Blade":
                    cellImage = "icons_weapons/icons_charge_blade/charge_blade" + mat.getItem().getRarity() + ".png";
                    break;
                case "Insect Glaive":
                    cellImage = "icons_weapons/icons_insect_glaive/insect_glaive" + mat.getItem().getRarity() + ".png";
                    break;
                case "Light Bowgun":
                    cellImage = "icons_weapons/icons_light_bowgun/light_bowgun" + mat.getItem().getRarity() + ".png";
                    break;
                case "Heavy Bowgun":
                    cellImage = "icons_weapons/icons_heavy_bowgun/heavy_bowgun" + mat.getItem().getRarity() + ".png";
                    break;
                case "Bow":
                    cellImage = "icons_weapons/icons_bow/bow" + mat.getItem().getRarity() + ".png";
                    break;
                default:
                    cellImage = "icons_items/" + mat.getItem().getFileLocation();
            }
            try {
                i = Drawable.createFromStream(
                        context.getAssets().open(cellImage), null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            itemImageView.setImageDrawable(i);

            long id = mat.getItem().getId();
            itemLayout.setTag(id);

            String itemtype = mat.getItem().getType();
            switch(itemtype){
                case "Weapon":
                    itemLayout.setOnClickListener(new WeaponClickListener(context, id));
                    break;
                case "Armor":
                    itemLayout.setOnClickListener(new ArmorClickListener(context, id));
                    break;
                case "Decoration":
                    itemLayout.setOnClickListener(new DecorationClickListener(context, id));
                    break;
                default:
                    itemLayout.setOnClickListener(new ItemClickListener(context, id));
                    break;
            }
        }
    }


}
