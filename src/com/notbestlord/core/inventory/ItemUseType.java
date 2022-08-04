package com.notbestlord.core.inventory;

import com.notbestlord.core.inventory.equipment.EquipmentSlot;
import com.notbestlord.core.utils.Utils;

public enum ItemUseType {
	none("None"),
	crafting_ingredient("Crafting Ingredient"), equipment_main_hand(EquipmentSlot.main_hand.name()), equipment_off_hand(EquipmentSlot.off_hand.name()),
	equipment_head(EquipmentSlot.head.name()), equipment_chest(EquipmentSlot.chest.name()),equipment_legs(EquipmentSlot.legs.name()),
	equipment_feet(EquipmentSlot.feet.name()),equipment_back(EquipmentSlot.back.name()), equipment_necklace(EquipmentSlot.necklace.name()),
	equipment_ring(EquipmentSlot.ring_1.name()), consumable("Consumable");

	private final String str;

	ItemUseType(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return Utils.UpperCaseStart(name().replace("_", " "));
	}
	public String getStr(){return str;}
}
