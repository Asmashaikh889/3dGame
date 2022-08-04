package com.notbestlord.network.client;

import com.notbestlord.core.inventory.Item;

import java.util.Map;

public class ItemsRegisterPacket {
	private Map<String, Item> items;
	private Map<String, String> itemTypes;

	public ItemsRegisterPacket() {}

	public ItemsRegisterPacket(Map<String, Item> items, Map<String, String> itemTypes) {
		this.items = items;
		this.itemTypes = itemTypes;
	}

	public Map<String, Item> getItems() {
		return items;
	}

	public Map<String, String> getItemTypes() {
		return itemTypes;
	}
}
