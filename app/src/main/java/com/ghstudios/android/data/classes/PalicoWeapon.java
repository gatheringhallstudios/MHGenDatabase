package com.ghstudios.android.data.classes;

/**
 * Created by Joseph on 7/9/2016.
 */
public class PalicoWeapon {
    private long _id;
    private int attackMelee;
    private int attackRanged;
    private boolean blunt;
    private int balance;
    private int elementMelee;
    private int elementRanged;
    private String element;
    private int affinityMelee;
    private int affinityRanged;
    private int defense;
    private int creation_cost;
    int sharpness;
    Item item;

    public Item getItem(){
        return this.item;
    }

    public void setItem(Item i){item=i;}

    public int getSharpness() {
        return sharpness;
    }

    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
    }


    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public int getAttackMelee() {
        return attackMelee;
    }

    public void setAttackMelee(int attackMelee) {
        this.attackMelee = attackMelee;
    }

    public int getAttackRanged() {
        return attackRanged;
    }

    public void setAttackRanged(int attackRanged) {
        this.attackRanged = attackRanged;
    }

    public boolean isBlunt() {
        return blunt;
    }

    public void setBlunt(boolean blunt) {
        this.blunt = blunt;
    }

    public int getBalance() {
        return balance;
    }

    public String getBalanceString(){
        switch (balance){
            case 0:return "Balanced";
            case 1:return "Melee+";
            default:return "Boomerang+";
        }
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getElementMelee() {
        return elementMelee;
    }

    public void setElementMelee(int elementMelee) {
        this.elementMelee = elementMelee;
    }

    public int getElementRanged() {
        return elementRanged;
    }

    public void setElementRanged(int elementRanged) {
        this.elementRanged = elementRanged;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public int getAffinityMelee() {
        return affinityMelee;
    }

    public void setAffinityMelee(int affinityMelee) {
        this.affinityMelee = affinityMelee;
    }

    public int getAffinityRanged() {
        return affinityRanged;
    }

    public void setAffinityRanged(int affinityRanged) {
        this.affinityRanged = affinityRanged;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getCreation_cost() {
        return creation_cost;
    }

    public void setCreation_cost(int creation_cost) {
        this.creation_cost = creation_cost;
    }
}
