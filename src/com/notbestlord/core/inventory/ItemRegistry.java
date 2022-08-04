package com.notbestlord.core.inventory;

public class ItemRegistry {
	private String itemType;
	private int amount;

	public ItemRegistry() {}

	public ItemRegistry(String itemType, int amount) {
		this.itemType = itemType;
		this.amount = amount;
	}
	public ItemRegistry(String itemType) {
		this.itemType = itemType;
		this.amount = 1;
	}

	public Item getAsItem(){
		return ItemManager.getItem(itemType, amount);
	}

	public int getAmount() {
		return amount;
	}

	public String getItemType() {
		return itemType;
	}

	@Override
	public String toString() {
		return "itemType:" + itemType + ", amount:" + amount;
	}
}
