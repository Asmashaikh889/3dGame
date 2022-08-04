package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.quests.QuestID;

public class QuestStartPacket {
	private QuestID questID;
	private String description;
	private float progressPercent;

	public QuestStartPacket() {}

	public QuestStartPacket(QuestID questID,String description, float progressPercent) {
		this.questID = questID;
		this.description = description;
		this.progressPercent = progressPercent;
	}

	public QuestID getQuestID() {
		return questID;
	}

	public String getDescription() {
		return description;
	}

	public float getProgressPercent() {
		return progressPercent;
	}
}
