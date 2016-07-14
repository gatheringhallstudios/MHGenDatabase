package com.ghstudios.android.data.classes;

/*
 * Class for Gathering
 */
public class Gathering {

	private long id;			// Gathering id
	private Item item;			// Item gathered
	private Location location;	// Location gathered
	private String area;		// Area # of location
	private String site;		// Type of gathering node; bug, mine, fish, etc.
	private String rank;		// Quest Rank found in
    private float rate;         // Gather rate

	int group;					//What group is it a part of, (Unique within an area)
	boolean fixed;				//is this a fixed gathering point
	boolean rare;				//Is it a rare point

	int quantity;
	
	/* Default Constructor */
	public Gathering() {
		this.id = -1;
		this.item = null;
		this.location = null;
		this.area = "";
		this.site = "";
		this.rank = "";
        this.rate = 0;
		this.group = 0;
		this.fixed = true;
		this.rare = false;
		this.quantity = 1;
	}

	/* Getters and Setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

	public void setGroup(int group){this.group = group;}
	public int getGroup(){return this.group;}
	public void setFixed(boolean fixed){this.fixed = fixed;}
	public boolean isFixed(){return this.fixed;}
	public void setRare(boolean rare){this.rare = rare;}
	public boolean isRare(){return this.rare;}
	public void setQuantity(int quantity){this.quantity=quantity;}
	public int getQuantity(){return this.quantity;}
}
