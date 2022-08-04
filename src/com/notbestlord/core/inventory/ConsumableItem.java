package com.notbestlord.core.inventory;

public class ConsumableItem extends Item{
	private String consumableEvent;

	public ConsumableItem() {}

	public ConsumableItem(String displayName, String type, int amount, int maxStackSize, String consumableEvent) {
		super(displayName, type, amount, maxStackSize);
		this.consumableEvent = consumableEvent;
	}

	public ConsumableItem(Item item) {
		super(item);
	}

	public ConsumableItem(ConsumableItem consumable) {
		super(consumable);
		this.consumableEvent = consumable.consumableEvent;
	}

	public String getConsumableEvent() {
		return consumableEvent;
	}
}
