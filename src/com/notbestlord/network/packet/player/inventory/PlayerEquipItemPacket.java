package com.notbestlord.network.packet.player.inventory;

import com.notbestlord.core.inventory.equipment.EquipmentSlot;

public class PlayerEquipItemPacket {
	private String uuid;
	private EquipmentSlot targetSlot;
	private String itemId;

	public PlayerEquipItemPacket() {}

	public PlayerEquipItemPacket(String uuid, EquipmentSlot targetSlot, String itemId) {
		this.uuid = uuid;
		this.targetSlot = targetSlot;
		this.itemId = itemId;
	}

	public String getUuid() {
		return uuid;
	}

	public EquipmentSlot getTargetSlot() {
		return targetSlot;
	}

	public String getItemId() {
		return itemId;
	}
}
