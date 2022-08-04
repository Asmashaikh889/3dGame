package com.notbestlord.core.inventory.equipment;

public enum EquipmentSlot {
	none,main_hand,off_hand,head,chest,back,legs,feet,necklace,ring_1,ring_2,ring_3,ring_4;
	public static boolean match(EquipmentSlot slot1, EquipmentSlot slot2){
		if((slot1 == ring_1 || slot1 == ring_2 || slot1 == ring_3 || slot1 == ring_4) && (slot2 == ring_1 || slot2 == ring_2 || slot2 == ring_3 || slot2 == ring_4)){
			return true;
		}
		return slot1 == slot2;
	}
}
