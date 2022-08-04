package com.notbestlord.network.packet.client;

public class ClientInputStartPacket {
	private String playerUUID;
	private String input;

	public ClientInputStartPacket() {}

	public ClientInputStartPacket(String playerUUID, String input) {
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
