package com.ghstudios.android.data.classes;

/*
 * Class for MonsterToQuest
 */
public class MonsterToQuest {

	private long id;			// id
	private Monster monster;	// Monster
	private Quest quest;		// Quest
	Habitat habitat;			// Habitat
	private int unstable;	// Unstable or not
	
	/* Default Constructors */
	public MonsterToQuest() {
		this.id = -1;
		this.monster = null;
		this.quest = null;
		this.unstable = 0;
	}

	/* Getters and Setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Monster getMonster() {
		return monster;
	}

	public void setMonster(Monster monster) {
		this.monster = monster;
	}

	public Habitat getHabitat(){return habitat;}
	public void setHabitat(Habitat hab){habitat = hab;}

	public Quest getQuest() {
		return quest;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	public int getUnstable() {
		return unstable;
	}

	public void setUnstable(int unstable) {
		this.unstable = unstable;
	}
}
