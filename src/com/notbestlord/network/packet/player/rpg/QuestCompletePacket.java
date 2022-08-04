package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.quests.QuestID;

public class QuestCompletePacket {
	private QuestID questID;

	public QuestCompletePacket() {}

	public QuestCompletePacket(QuestID questID) {
		this.questID = questID;
	}

	public QuestID getQuestID() {
		return questID;
	}
}
