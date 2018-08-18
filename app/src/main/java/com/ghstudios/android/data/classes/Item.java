package com.ghstudios.android.data.classes;

import com.ghstudios.android.ITintedIcon;
import com.ghstudios.android.mhgendatabase.R;

import org.jetbrains.annotations.NotNull;

/*
 * Class for Item
 */
public class  Item implements ITintedIcon {

	private long id;						// Item id
	private String name;					// Item name
	private String jpn_name;				// Japanese name; unused at the moment
	private String type;					// Item type
    private String sub_type;			    // Item sub type
	private int rarity;						// Rarity; 1-10
	private int carry_capacity;				// Carry capacity in backpack
	private int buy;						// Buy amount
	private int sell;						// Sell amount
	private String description;				// Item description
	private String file_location;			// File location for image
	private boolean account;
	private int icon_color;
	
	/* Default Constructor */
	public Item() {
		this.id = -1;
		this.name = "";
		this.jpn_name = "";
		this.type = "";
        this.sub_type = "";
		this.rarity = -1;
		this.carry_capacity = -1;
		this.buy = -1;
		this.sell = -1;
		this.description = "";
		this.file_location = "";
	}

	/* Getters and Setters */
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getJpnName() {
		return jpn_name;
	}


	public void setJpnName(String jpn_name) {
		this.jpn_name = jpn_name;
	}


	public String getType() {
		return type;
	}


	public void setSubType(String sub_type) {
		this.sub_type = sub_type;
	}


    public String getSubType() {
        return sub_type;
    }


    public void setType(String type) {
        this.type = type;
    }


	public int getRarity() {
		return rarity;
	}

	public String getRarityString(){
		if(rarity==11) return "X";
		return Integer.toString(rarity);
	}


	public void setRarity(int rarity) {
		this.rarity = rarity;
	}


	public int getCarryCapacity() {
		return carry_capacity;
	}


	public void setCarryCapacity(int carry_capacity) {
		this.carry_capacity = carry_capacity;
	}


	public int getBuy() {
		return buy;
	}


	public void setBuy(int buy) {
		this.buy = buy;
	}


	public int getSell() {
		return sell;
	}


	public void setSell(int sell) {
		this.sell = sell;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getFileLocation() {
		return file_location;
	}


	public void setFileLocation(String file_location) {
		this.file_location = file_location;
	}

	public void setAccount(boolean account){
		this.account = account;
	}

	public boolean IsAccountItem(){return account;}

	public void setIconColor(int col){ this.icon_color = col;}
	public int getIconColor(){return icon_color;}

	@NotNull
	@Override
	public String getIconResourceString() {
		return getFileLocation();
	}

	boolean usesRarity() {
		switch (type){
			case "Weapon":
			case "Armor":
			case "Palico Weapon":
			case "Palico Armor":
				return true;
			default:return false;
		}
	}

	@Override
	public int getIconColorIndex() {
		if(usesRarity()) return rarity-1;
		return icon_color;
	}

	@Override
	public int getColorArrayId() {
		if(usesRarity()) return R.array.rare_colors;
		return R.array.item_colors;
	}
}

