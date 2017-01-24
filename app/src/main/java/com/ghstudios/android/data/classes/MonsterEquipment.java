package com.ghstudios.android.data.classes;

/**
 * A view of an item derived by crafting a particular monster drop
 * Created by Carlos on 1/24/2017.
 */
public class MonsterEquipment {
    private Item item;
    private String rank;        // Quest rank

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
