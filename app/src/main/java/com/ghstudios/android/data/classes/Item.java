package com.ghstudios.android.data.classes;

/*
 * Class for Item
 */
public class  Item {

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

	public String getItemImage(){
		String cellImage;
		switch(this.getSubType()){
			case "Head":
				cellImage = "icons_armor/icons_head/head" + this.getRarity() + ".png";
				break;
			case "Body":
				cellImage = "icons_armor/icons_body/body" + this.getRarity() + ".png";
				break;
			case "Arms":
				cellImage = "icons_armor/icons_arms/arms" + this.getRarity() + ".png";
				break;
			case "Waist":
				cellImage = "icons_armor/icons_waist/waist" + this.getRarity() + ".png";
				break;
			case "Legs":
				cellImage = "icons_armor/icons_legs/legs" + this.getRarity() + ".png";
				break;
			case Weapon.GREAT_SWORD:
				cellImage = "icons_weapons/icons_great_sword/great_sword" + this.getRarity() + ".png";
				break;
			case Weapon.LONG_SWORD:
				cellImage = "icons_weapons/icons_long_sword/long_sword" + this.getRarity() + ".png";
				break;
			case Weapon.SWORD_AND_SHIELD:
				cellImage = "icons_weapons/icons_sword_and_shield/sword_and_shield" + this.getRarity() + ".png";
				break;
			case Weapon.DUAL_BLADES:
				cellImage = "icons_weapons/icons_dual_blades/dual_blades" + this.getRarity() + ".png";
				break;
			case Weapon.HAMMER:
				cellImage = "icons_weapons/icons_hammer/hammer" + this.getRarity() + ".png";
				break;
			case Weapon.HUNTING_HORN:
				cellImage = "icons_weapons/icons_hunting_horn/hunting_horn" + this.getRarity() + ".png";
				break;
			case Weapon.LANCE:
				cellImage = "icons_weapons/icons_lance/lance" + this.getRarity() + ".png";
				break;
			case Weapon.GUN_LANCE:
				cellImage = "icons_weapons/icons_gunlance/gunlance" + this.getRarity() + ".png";
				break;
			case Weapon.SWITCH_AXE:
				cellImage = "icons_weapons/icons_switch_axe/switch_axe" + this.getRarity() + ".png";
				break;
			case Weapon.CHARGE_BLADE:
				cellImage = "icons_weapons/icons_charge_blade/charge_blade" + this.getRarity() + ".png";
				break;
			case Weapon.INSECT_GLAIVE:
				cellImage = "icons_weapons/icons_insect_glaive/insect_glaive" + this.getRarity() + ".png";
				break;
			case Weapon.LIGHT_BOWGUN:
				cellImage = "icons_weapons/icons_light_bowgun/light_bowgun" + this.getRarity() + ".png";
				break;
			case Weapon.HEAVY_BOWGUN:
				cellImage = "icons_weapons/icons_heavy_bowgun/heavy_bowgun" + this.getRarity() + ".png";
				break;
			case Weapon.BOW:
				cellImage = "icons_weapons/icons_bow/bow" + this.getRarity() + ".png";
				break;
			default: {
				if(this.type.equals("Palico Weapon"))
					cellImage = "icons_weapons/"+this.getFileLocation();
				else
					cellImage = "icons_items/" + this.getFileLocation();
			}
		}
		return cellImage;
	}

	public void setAccount(boolean account){
		this.account = account;
	}

	public boolean IsAccountItem(){return account;}

}

