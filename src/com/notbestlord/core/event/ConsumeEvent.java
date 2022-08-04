package com.notbestlord.core.event;

public class ConsumeEvent extends RPGEvent{
	private final String itemType;

	public ConsumeEvent(String event, String itemType) {
		super("consumable", event);
		this.itemType = itemType;
	}

	public String getItemType() {
		return itemType;
	}
}
