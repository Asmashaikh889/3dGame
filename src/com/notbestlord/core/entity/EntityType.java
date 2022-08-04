package com.notbestlord.core.entity;

public enum EntityType {
	entity("Entity"), player("Player"), npc("Npc"), monster_slime("Slime");

	private final String displayString;

	EntityType(String displayString) {
		this.displayString = displayString;
	}

	public String getDisplayString() {
		return displayString;
	}
}
