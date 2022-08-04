package com.notbestlord.network.client;

import com.notbestlord.core.rpg.quests.QuestID;

public class ClientQuestData {
	private QuestID questID;
	private String description;
	private float percent;

	public ClientQuestData() {}

	public ClientQuestData(QuestID questID, String description, float percent) {
		this.questID = questID;
		this.description = description;
		this.percent = percent;
	}

	public QuestID getQuestID() {
		return questID;
	}

	public void setQuestID(QuestID questID) {
		this.questID = questID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}
}
