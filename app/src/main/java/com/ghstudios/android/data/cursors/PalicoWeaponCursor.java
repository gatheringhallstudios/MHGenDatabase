package com.ghstudios.android.data.cursors;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ghstudios.android.data.classes.Item;
import com.ghstudios.android.data.classes.ItemType;
import com.ghstudios.android.data.classes.PalicoWeapon;
import com.ghstudios.android.data.database.S;

/**
 * Created by Joseph on 7/9/2016.
 */
public class PalicoWeaponCursor extends CursorWrapper {

    public PalicoWeaponCursor(Cursor cursor) {
        super(cursor);
    }

    public PalicoWeapon getWeapon(){
        if (isBeforeFirst() || isAfterLast())
            return null;

        PalicoWeapon weapon = new PalicoWeapon();
        weapon.setId(getLong(getColumnIndex(S.COLUMN_PALICO_WEAPONS_ID)));
        weapon.setCreation_cost(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_CREATION_COST)));
        weapon.setAttackMelee(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_ATTACK_MELEE)));
        weapon.setAttackRanged(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_ATTACK_RANGED)));
        weapon.setElement(getString(getColumnIndex(S.COLUMN_PALICO_WEAPONS_ELEMENT)));
        weapon.setElementMelee(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_ELEMENT_MELEE)));
        weapon.setElementRanged(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_ELEMENT_RANGED)));
        weapon.setDefense(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_DEFENSE)));
        weapon.setSharpness(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_SHARPNESS)));
        weapon.setAffinityMelee(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_AFFINITY_MELEE)));
        weapon.setAffinityRanged(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_AFFINITY_RANGED)));
        weapon.setBlunt(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_BLUNT))==1);
        weapon.setBalance(getInt(getColumnIndex(S.COLUMN_PALICO_WEAPONS_BALANCE)));

        Item i = new Item();
        i.setId(getLong(getColumnIndex(S.COLUMN_ITEMS_ID)));
        i.setName(getString(getColumnIndex(S.COLUMN_ITEMS_NAME)));
        i.setRarity(getInt(getColumnIndex(S.COLUMN_ITEMS_RARITY)));
        i.setDescription(getString(getColumnIndex(S.COLUMN_ITEMS_DESCRIPTION)));
        i.setFileLocation(getString(getColumnIndex(S.COLUMN_ITEMS_ICON_NAME)));
        i.setIconColor(getInt(getColumnIndex(S.COLUMN_ITEMS_ICON_COLOR)));
        i.setType(ItemType.PALICO_WEAPON);

        weapon.setItem(i);
        return weapon;
    }
}
