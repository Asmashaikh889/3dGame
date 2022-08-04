package com.notbestlord.network.packet.client;

import com.notbestlord.core.inventory.ItemInformation;

import java.util.Map;

public class ItemInfoListPacket {
	private Map<String, ItemInformation> itemInformationByItemType;

	public ItemInfoListPacket() {}

	public ItemInfoListPacket(Map<String, ItemInformation> itemInformationByItemType) {
		this.itemInformationByItemType = itemInformationByItemType;
	}

	public Map<String, ItemInformation> getItemInformationByItemType() {
		return itemInformationByItemType;
	}
}
