package com.notbestlord.core.inventory.equipment;

public class Equipment {
	private EquipmentItem head;
	private EquipmentItem chest;
	private EquipmentItem legs;
	private EquipmentItem feet;
	private EquipmentItem main_hand;
	private EquipmentItem off_hand;
	private EquipmentItem back;
	private EquipmentItem necklace;
	private EquipmentItem ring_1;
	private EquipmentItem ring_2;
	private EquipmentItem ring_3;
	private EquipmentItem ring_4;

	public Equipment() {}

	public Equipment(Equipment equipment) {
		head = equipment.head;
		chest = equipment.chest;
		legs = equipment.legs;
		feet = equipment.feet;
		main_hand = equipment.main_hand;
		off_hand = equipment.off_hand;
		back = equipment.back;
		necklace = equipment.necklace;
		ring_1 = equipment.ring_1;
		ring_2 = equipment.ring_2;
		ring_3 = equipment.ring_3;
		ring_4 = equipment.ring_4;
	}

	public EquipmentSlot getSlotOfItem(EquipmentItem item){
		if(item == head) return EquipmentSlot.head;
		if(item == chest) return EquipmentSlot.chest;
		if(item == legs) return EquipmentSlot.legs;
		if(item == feet) return EquipmentSlot.feet;
		if(item == back) return EquipmentSlot.back;
		if(item == main_hand) return EquipmentSlot.main_hand;
		if(item == off_hand) return EquipmentSlot.off_hand;
		if(item == necklace) return EquipmentSlot.necklace;
		if(item == ring_1) return EquipmentSlot.ring_1;
		if(item == ring_2) return EquipmentSlot.ring_2;
		if(item == ring_3) return EquipmentSlot.ring_3;
		if(item == ring_4) return EquipmentSlot.ring_4;
		return EquipmentSlot.none;
	}

	public EquipmentItem replaceEquipment(EquipmentSlot slot, EquipmentItem item){
		EquipmentItem prev = null;
		switch (slot){
			case head -> {prev = head; head = item;}
			case chest -> {prev = chest; chest = item;}
			case back -> {prev = back; back = item;}
			case legs -> {prev = legs; legs = item;}
			case feet -> {prev = feet; feet = item;}
			case main_hand -> {prev = main_hand; main_hand = item;}
			case off_hand -> {prev = off_hand; off_hand = item;}
			case necklace -> {prev = necklace; necklace = item;}
			case ring_1 -> {prev = ring_1; ring_1 = item;}
			case ring_2 -> {prev = ring_2; ring_2 = item;}
			case ring_3 -> {prev = ring_3; ring_3 = item;}
			case ring_4 -> {prev = ring_4; ring_4 = item;}
		}
		return prev;
	}

	public EquipmentItem getItemInSlot(EquipmentSlot slot){
		switch (slot){
			case head -> {return head;}
			case chest -> {return chest;}
			case back -> {return back;}
			case legs -> {return legs;}
			case feet -> {return feet;}
			case main_hand -> {return main_hand;}
			case off_hand -> {return off_hand;}
			case necklace -> {return necklace;}
			case ring_1 -> {return ring_1;}
			case ring_2 -> {return ring_2;}
			case ring_3 -> {return ring_3;}
			case ring_4 -> {return ring_4;}
		}
		return null;
	}

	public EquipmentItem getHead() {
		return head;
	}

	public EquipmentItem getChest() {
		return chest;
	}

	public EquipmentItem getLegs() {
		return legs;
	}

	public EquipmentItem getFeet() {
		return feet;
	}

	public EquipmentItem getMain_hand() {
		return main_hand;
	}

	public EquipmentItem getOff_hand() {
		return off_hand;
	}

	public EquipmentItem getBack() {
		return back;
	}

 	public void setHead(EquipmentItem head) {
		this.head = head;
	}

	public void setChest(EquipmentItem chest) {
		this.chest = chest;
	}

	public void setLegs(EquipmentItem legs) {
		this.legs = legs;
	}

	public void setFeet(EquipmentItem feet) {
		this.feet = feet;
	}

	public void setMain_hand(EquipmentItem main_hand) {
		this.main_hand = main_hand;
	}

	public void setOff_hand(EquipmentItem off_hand) {
		this.off_hand = off_hand;
	}

	public void setBack(EquipmentItem back) {
		this.back = back;
	}

	public EquipmentItem getNecklace() {
		return necklace;
	}

	public void setNecklace(EquipmentItem necklace) {
		this.necklace = necklace;
	}

	public EquipmentItem getRing_1() {
		return ring_1;
	}

	public void setRing_1(EquipmentItem ring_1) {
		this.ring_1 = ring_1;
	}

	public EquipmentItem getRing_2() {
		return ring_2;
	}

	public void setRing_2(EquipmentItem ring_2) {
		this.ring_2 = ring_2;
	}

	public EquipmentItem getRing_3() {
		return ring_3;
	}

	public void setRing_3(EquipmentItem ring_3) {
		this.ring_3 = ring_3;
	}

	public EquipmentItem getRing_4() {
		return ring_4;
	}

	public void setRing_4(EquipmentItem ring_4) {
		this.ring_4 = ring_4;
	}

	public EquipmentItem[] getArmorContents(){
		return new EquipmentItem[]{head,chest,back,legs,feet};
	}
	public EquipmentItem[] getEquipment(){
		return new EquipmentItem[]{head,chest,back,legs,feet, main_hand,off_hand};
	}
}
