package com.notbestlord.core.loot;

import com.notbestlord.core.inventory.IInventory;
import com.notbestlord.core.utils.Range;
import com.notbestlord.network.server.player.ServerPlayerInventory;

import java.util.List;

public class EntityLootTable {
	private Range<Float> exp;
	private Range<Integer> coins;
	private List<ItemLoot> items;

	public EntityLootTable(Range<Float> exp, Range<Integer> coins, List<ItemLoot> items) {
		this.exp = exp;
		this.coins = coins;
		this.items = items;
	}
	public void applyLoot(IInventory inv){
		items.forEach(itemLoot -> {inv.addItem(itemLoot.generateLoot());});
	}
	public void applyPlayerLoot(ServerPlayerInventory inv){
		applyLoot(inv);
		inv.setCoins(inv.getCoins() + Range.randomInt(coins.getMin(),coins.getMax()));
	}
	public void applyPlayerLoot(ServerPlayerInventory inv, float luck){
		items.forEach(itemLoot -> {inv.addItem(itemLoot.generateLoot(luck / 100));});
		inv.setCoins(inv.getCoins() + Range.randomInt(coins.getMin(),coins.getMax()));
		inv.getOwner().getSkillHandler().incCombatExp(Range.randomFloat(exp.getMin(),  exp.getMax()), inv.getEquipment());
	}
}
