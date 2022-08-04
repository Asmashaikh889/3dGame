package com.notbestlord.core.inventory.equipment;

import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.rpg.stats.Stat;

import java.util.HashMap;
import java.util.Map;

public class EquipmentItem extends Item {
	private Map<Stat, Float> itemStats;
	private Map<Stat, Float> itemStatMuls;
	private Attribute attribute;

	public EquipmentItem() {}

	public EquipmentItem(String displayName, String type, int amount, int maxStackSize) {
		super(displayName, type, amount, maxStackSize);
		itemStats = new HashMap<>();
		itemStatMuls = new HashMap<>();
		attribute = Attribute.none;
	}

	public EquipmentItem(Item item) {
		super(item);
		itemStats = new HashMap<>();
		itemStatMuls = new HashMap<>();
		attribute = Attribute.none;
	}

	public EquipmentItem(EquipmentItem item) {
		super(item);
		itemStats = new HashMap<>(item.getItemStats());
		itemStatMuls = new HashMap<>(item.getItemStatMuls());
		attribute = item.getAttribute();
	}

	public Map<Stat, Float> getItemStats() {
		return new HashMap<>(itemStats);
	}

	public Map<Stat, Float> getItemStatMuls() {
		return new HashMap<>(itemStatMuls);
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public EquipmentItem setAttribute(Attribute attribute) {
		this.attribute = attribute;
		return this;
	}

	public EquipmentItem addStat(Stat stat, float n){
		itemStats.put(stat, n);
		return this;
	}
	public float getStat(Stat stat){
		return itemStats.get(stat);
	}

	public EquipmentItem addStatMul(Stat stat, float n){
		itemStatMuls.put(stat, n);
		return this;
	}
	public float getStatMul(Stat stat){
		return itemStatMuls.get(stat);
	}

	@Override
	public String toString() {
		return super.toString() + "\nEquipmentItem[" +
				"itemStats=" + itemStats +
				", attribute=" + attribute +
				']';
	}
}
