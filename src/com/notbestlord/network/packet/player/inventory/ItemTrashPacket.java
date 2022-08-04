package com.notbestlord.network.packet.player.inventory;

public class ItemTrashPacket {
	private String uuid;
	private String itemUuid;

	public ItemTrashPacket() {}

	public ItemTrashPacket(String uuid, String itemUuid) {
		this.uuid = uuid;
		this.itemUuid = itemUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public String getItemUuid() {
		return itemUuid;
	}
}
