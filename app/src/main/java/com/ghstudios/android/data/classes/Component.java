package com.ghstudios.android.data.classes;

// note: to convert this to kotlin, many program changes would be required.
// TO avoid those program changes, component (the item) needs to be non-nullable.
// If converted to kotlin, also update ComponentCursor to pass the item in the constructor to ensure it


/**
 * Class for Component
 *
 * This is used for forging armor, weapons, and decorations
 */
public class Component {
	public static String TYPE_CREATE = "Create";
	public static String TYPE_IMPROVE = "Improve";

	private long id;			// Component id
	private Item created;		// Created Item
	private Item component;		// Component Item
	private int quantity;		// Amount needed for the component Item
	private String type;		// Creation method
	private int key;			// Is key for creation
	
	/* Default Constructor */
	public Component() {
		this.id = -1;
		this.created = null;
		this.component = null;
		this.quantity = -1;
		this.type = "";
		this.key = 0;
	}

	/* Getters and Setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Item getCreated() {
		return created;
	}

	public void setCreated(Item created) {
		this.created = created;
	}

	public Item getComponent() {
		return component;
	}

	public void setComponent(Item component) {
		this.component = component;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setKey(int k){this.key = k;}

	public int getKey(){return this.key;}

	public boolean isKey(){return this.key == 1;}
}
