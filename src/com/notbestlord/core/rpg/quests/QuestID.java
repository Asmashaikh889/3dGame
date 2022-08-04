package com.notbestlord.core.rpg.quests;

public enum QuestID {
	blacksmith_john_0("Help John! (0)"),blacksmith_john_1("Help John! (1)"),;

	private final String displayString;

	QuestID(String displayString) {
		this.displayString = displayString;
	}

	@Override
	public String toString() {
		return displayString;
	}
}
