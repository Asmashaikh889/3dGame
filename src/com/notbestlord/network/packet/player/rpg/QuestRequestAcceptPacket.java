package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.quests.QuestID;

public class QuestRequestAcceptPacket {
	private QuestID questName;
	private String rawDescription;

	public QuestRequestAcceptPacket() {}

	public QuestRequestAcceptPacket(QuestID questName, String rawDescription) {
		this.questName = questName;
		this.rawDescription = rawDescription;
	}

	public QuestID getQuestName() {
		return questName;
	}

	public String getRawDescription() {
		return rawDescription;
	}
}
