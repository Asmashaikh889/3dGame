package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.quests.QuestID;

public class QuestRequestCompletePacket {
	private String playerUuid;
	private QuestID questID;

	public QuestRequestCompletePacket() {}

	public QuestRequestCompletePacket(String playerUuid, QuestID questID) {
		this.playerUuid = playerUuid;
		this.questID = questID;
	}

	public String getPlayerUuid() {
		return playerUuid;
	}

	public QuestID getQuestID() {
		return questID;
	}
}
