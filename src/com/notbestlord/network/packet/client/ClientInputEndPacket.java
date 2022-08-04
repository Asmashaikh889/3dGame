package com.notbestlord.network.packet.client;

public class ClientInputEndPacket {
	private String playerUUID;
	private String input;

	public ClientInputEndPacket() {}

	public ClientInputEndPacket(String playerUUID, String input) {
		this.playerUUID = playerUUID;
		this.input = input;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public String getInput() {
		return input;
	}
}
