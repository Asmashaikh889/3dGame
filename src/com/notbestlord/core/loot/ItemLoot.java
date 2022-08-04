package com.notbestlord.core.loot;

import com.notbestlord.core.inventory.Item;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.inventory.equipment.EquipmentItem;
import com.notbestlord.core.utils.Range;

public class ItemLoot {
	private Range<Integer> range;
	private ItemRegistry item;
	private float chance;

	public ItemLoot(Range<Integer> range, ItemRegistry item) {
		this.range = range;
		this.item = item;
		this.chance = 0;
	}
	public ItemLoot(int min, int max, ItemRegistry item) {
		this.range = new Range<>(min, max);
		this.item = item;
		this.chance = 0;
	}
	public ItemLoot(int min, ItemRegistry item, float chance) {
		this.range = new Range<>(min, min + 1);
		this.item = item;
		this.chance = chance;
	}
	public float getChance(){ return chance; }
	public Item generateLoot(){
		Item i = item.getAsItem();
		if(chance != 0f){
			int amount = range.getMin();
			if(Math.random() < chance){
				amount++;
			}
			i.setAmount(amount);
		}
		else {
			i.setAmount(Range.randomInt(range.getMin(), range.getMax()));
		}
		return i;
	}
	public Item generateLoot(float luck){
		Item i = item.getAsItem();
		if(chance != 0){
			int amount = range.getMin();
			if(Math.random() < chance){
				amount++;
			}
			else if(Math.random() < luck && Math.random() < chance){
				amount++;
			}
			i.setAmount(amount);
		}
		else {
			i.setAmount(Range.randomInt(range.getMin(), range.getMax()) +
					(Math.random() < luck ? Range.randomInt(range.getMin(), range.getMax()) : 0));
		}
		return i;
	}
}
