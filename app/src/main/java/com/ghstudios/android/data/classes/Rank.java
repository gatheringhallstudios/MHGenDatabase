package com.ghstudios.android.data.classes;

public enum Rank {
    LOW(1, 3),
    HIGH(4, 10),
    G(4, 10);       //JOE: If someone has used G before we removed it, it will work just like HIGH

    int armorMinimumRarity;
    int armorMaximumRarity;

    Rank(int armorMinimumRarity, int armorMaximumRarity) {
        this.armorMinimumRarity = armorMinimumRarity;
        this.armorMaximumRarity = armorMaximumRarity;
    }

    public int getArmorMinimumRarity() {
        return armorMinimumRarity;
    }

    public int getArmorMaximumRarity() {
        return armorMaximumRarity;
    }
}
