package com.notbestlord.network.server.player;

import com.google.gson.annotations.Expose;
import com.notbestlord.core.inventory.equipment.*;
import com.notbestlord.core.rpg.stats.Stat;

public class ServerPlayerEquipment extends Equipment {
	@Expose(serialize = false)
	private ServerPlayerInventory parentInventory;

	public ServerPlayerEquipment(ServerPlayerInventory parentInventory, Equipment equipment) {
		super(equipment);
		this.parentInventory = parentInventory;
	}
	public ServerPlayerEquipment(ServerPlayerInventory parentInventory) {
		super();
		this.parentInventory = parentInventory;
	}

	public void init(ServerPlayerInventory parentInventory){
		this.parentInventory = parentInventory;
	}

	public ServerPlayerInventory getPlayerInventory() {
		return parentInventory;
	}

	@Override
	public EquipmentItem replaceEquipment(EquipmentSlot slot, EquipmentItem item) {
		EquipmentItem prev = null;
		switch (slot){
			case head -> {prev = getHead(); setHead(item);}
			case chest -> {prev = getChest(); setChest(item);}
			case back -> {prev = getBack(); setBack(item);}
			case legs -> {prev = getLegs(); setLegs(item);}
			case feet -> {prev = getFeet(); setFeet(item);}
			case main_hand -> {prev = getMain_hand(); setMain_hand(item);}
			case off_hand -> {prev = getOff_hand(); setOff_hand(item);}
			case necklace -> {prev = getNecklace(); setNecklace(item);}
			case ring_1 -> {prev = getRing_1(); setRing_1(item);}
			case ring_2 -> {prev = getRing_2(); setRing_2(item);}
			case ring_3 -> {prev = getRing_3(); setRing_3(item);}
			case ring_4 -> {prev = getRing_4(); setRing_4(item);}
		}
		boolean reequip = false;
		if(item != null && !addItemStats(item)){
			reequip = true;
		}
		if(prev != null && !removeItemStats(prev)){
			reequip = true;
		}
		return reequip ? replaceEquipment(slot, prev) : prev;
	}

	private boolean addItemStats(EquipmentItem item){
		for(Stat stat : item.getItemStats().keySet())
			parentInventory.getOwner().getStatHandler().incStat(stat, item.getStat(stat));
		for(Stat stat : item.getItemStatMuls().keySet())
			parentInventory.getOwner().getStatHandler().incStatMul(stat, item.getStatMul(stat));
		if(item.getItemStats().containsKey(Stat.inventory_capacity) || item.getItemStatMuls().containsKey(Stat.inventory_capacity)){
			if(!parentInventory.canUpdateCapacity((int) parentInventory.getOwner().getStatHandler().getStat(Stat.inventory_capacity))) return false;
			parentInventory.updateCapacity((int) parentInventory.getOwner().getStatHandler().getStat(Stat.inventory_capacity));
		}
		return true;
	}

	private boolean removeItemStats(EquipmentItem item){
		for(Stat stat : item.getItemStats().keySet())
			parentInventory.getOwner().getStatHandler().incStat(stat, -item.getStat(stat));
		for(Stat stat : item.getItemStatMuls().keySet())
			parentInventory.getOwner().getStatHandler().incStatMul(stat, 1.0f / item.getStatMul(stat));
		if(item.getItemStats().containsKey(Stat.inventory_capacity) || item.getItemStatMuls().containsKey(Stat.inventory_capacity)){
			if(!parentInventory.canUpdateCapacity((int) parentInventory.getOwner().getStatHandler().getStat(Stat.inventory_capacity) - 1)) return false;
			parentInventory.updateCapacity((int) parentInventory.getOwner().getStatHandler().getStat(Stat.inventory_capacity));
		}
		return true;
	}

	public Attribute getAttackAttribute(){
		if(getMain_hand() != null) return getMain_hand().getAttribute();
		return Attribute.none;
	}

	public float getDamage(){
		float d = 10;
		if(getMain_hand() != null && getMain_hand() instanceof EquipmentWeapon) d += ((EquipmentWeapon) getMain_hand()).getDamageStat();
		if(getOff_hand() != null && getOff_hand() instanceof EquipmentWeapon) d += ((EquipmentWeapon) getOff_hand()).getDamageStat();
		return d;
	}
}
