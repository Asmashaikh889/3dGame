package com.notbestlord.network.packet.player.inventory;

public class PlayerInteractItemPacket {
	private String uuid;
	private String itemId;

	public PlayerInteractItemPacket() {}

	public PlayerInteractItemPacket(String uuid, String itemId) {
		this.uuid = uuid;
		this.itemId = itemId;
	}

	public String getUuid() {
		return uuid;
	}

	public String getItemId() {
		return itemId;
	}
}
