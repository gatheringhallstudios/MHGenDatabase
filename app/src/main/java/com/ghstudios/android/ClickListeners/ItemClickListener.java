package com.ghstudios.android.ClickListeners;

import android.content.Context;
import android.view.View;

import com.ghstudios.android.data.classes.Item;

/**
 * A proxy listener that internally uses an actual listener for general item pages.
 * Created by Carlos on 1/22/2017.
 */
public class ItemClickListener implements View.OnClickListener {
    View.OnClickListener innerListener;

    public ItemClickListener(Context context, String type, Long id) {
        super();
        innerListener = constructTrueListener(context, type, id);
    }

    public ItemClickListener(Context context, Item item) {
        this(context, item.getType(), item.getId());
    }

    private View.OnClickListener constructTrueListener(Context c, String type, Long id) {
        switch(type){
            case "Weapon":
                return new WeaponClickListener(c, id);
            case "Armor":
                return new ArmorClickListener(c, id);
            case "Decoration":
                return new DecorationClickListener(c, id);
            case "Materials":
                return new MaterialClickListener(c,id);
            case "Palico Weapon":
                return new PalicoWeaponClickListener(c,id);
            default:
                return new BasicItemClickListener(c, id);
        }
    }

    @Override
    public void onClick(View v) {
        innerListener.onClick(v);
    }
}
