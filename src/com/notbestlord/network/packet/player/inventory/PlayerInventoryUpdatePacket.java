package com.notbestlord.network.packet.player.inventory;

import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.equipment.Equipment;
import com.notbestlord.network.server.player.ServerPlayerInventory;

public class PlayerInventoryUpdatePacket {
	private int coins;
	private Item[] items;
	private Equipment equipment;

	public PlayerInventoryUpdatePacket() {}

	public PlayerInventoryUpdatePacket(ServerPlayerInventory inventory) {
		coins = inventory.getCoins();
		items = inventory.getContents();
		equipment = new Equipment(inventory.getEquipment());
	}

	public int getCoins() {
		return coins;
	}

	public Item[] getItems() {
		return items;
	}

	public Equipment getEquipment() {
		return equipment;
	}
}
