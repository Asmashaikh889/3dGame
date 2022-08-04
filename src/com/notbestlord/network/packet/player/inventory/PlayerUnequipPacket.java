package com.notbestlord.network.packet.player.inventory;

import com.notbestlord.core.inventory.equipment.EquipmentSlot;

public class PlayerUnequipPacket {
	private String uuid;
	private EquipmentSlot slot;

	public PlayerUnequipPacket() {}

	public PlayerUnequipPacket(String uuid, EquipmentSlot slot) {
		this.uuid = uuid;
		this.slot = slot;
	}

	public String getUuid() {
		return uuid;
	}

	public EquipmentSlot getSlot() {
		return slot;
	}
}
