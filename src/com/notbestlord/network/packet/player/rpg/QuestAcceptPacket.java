package com.notbestlord.network.packet.player.rpg;

public class QuestAcceptPacket {
	private String playerUuid;
	private boolean accept;

	public QuestAcceptPacket() {}

	public QuestAcceptPacket(String playerUuid, boolean accept) {
		this.playerUuid = playerUuid;
		this.accept = accept;
	}

	public String getPlayerUuid() {
		return playerUuid;
	}

	public boolean isAccept() {
		return accept;
	}
}
