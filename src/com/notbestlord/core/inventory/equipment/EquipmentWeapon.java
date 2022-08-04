package com.notbestlord.core.inventory.equipment;

import com.notbestlord.core.inventory.Item;

import java.util.ArrayList;
import java.util.List;

public class EquipmentWeapon extends EquipmentItem{
	private float damage = 1;
	private List<WeaponType> weaponTypes;

	public EquipmentWeapon() {}

	public EquipmentWeapon(String displayName, String type, int amount, int maxStackSize, float damage) {
		super(displayName, type, amount, maxStackSize);
		this.damage = damage;
		this.weaponTypes = new ArrayList<>(List.of(WeaponType.none));
	}
	public EquipmentWeapon(String displayName, String type, int amount, int maxStackSize, float damage, List<WeaponType> weaponTypes) {
		super(displayName, type, amount, maxStackSize);
		this.damage = damage;
		this.weaponTypes = weaponTypes;
	}

	public EquipmentWeapon(Item item) {
		super(item);
	}

	public EquipmentWeapon(EquipmentItem item) {
		super(item);
	}

	public EquipmentWeapon(EquipmentWeapon item) {
		super(item);
		damage = item.damage;
		this.weaponTypes = new ArrayList<>(item.weaponTypes);
	}

	public float getDamageStat() {
		return damage;
	}

	public void setDamageStat(float damage) {
		this.damage = damage;
	}

	public List<WeaponType> getWeaponTypes(){
		return this.weaponTypes;
	}
}
