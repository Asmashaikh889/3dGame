package com.notbestlord.core.rpg.stats;

import java.awt.*;

public enum Stat {
	health(new Color(255, 108, 108), "Health"),
	health_max(new Color(255, 0, 0), "Max Health"),
	health_regeneration(new Color(105, 44, 44), "Health Regeneration"),
	endurance(new Color(180, 180, 180), "Endurance"),
	speed(new Color(255, 255, 255), "Speed"),
	stamina(new Color(158, 255, 150), "Stamina"),
	stamina_max(new Color(87, 178, 80), "Max Stamina"),
	stamina_recovery(new Color(66, 131, 61), "Stamina Recovery Rate"),
	mana(new Color(0, 164, 255), "Mana"),
	mana_max(new Color(0, 142, 220), "Max Mana"),
	mana_recovery(new Color(0, 69, 110), "Mana Recovery Rate"),
	dexterity(new Color(255, 228, 92), "Dexterity"),
	strength(new Color(255, 89, 171), "Strength"),
	brutality(new Color(255, 145, 0), "Brutality"),
	critical_damage(new Color(0, 255, 255), "Critical Damage"),
	critical_chance(new Color(0,255,255), "Critical Chance"),
	luck(new Color(59, 122, 0), "Luck"),
	inventory_capacity(new Color(142, 255, 210), "Inventory Capacity"),
	deteriorated_endurance(new Color(112, 112, 112), "Deteriorated Endurance", DisplayType.never),
	insanity(new Color(53, 75, 61), "Insanity", DisplayType.notZero),
	stealth(new Color(80,80,100), "Stealth", DisplayType.notZero),
	//gap(new Color(220,220,255), "Gap", DisplayType.notZero),
	qi(new Color(50,220,250), "External Qi", DisplayType.notZero),
	qi_max(new Color(50,205,250), "Inner Qi", DisplayType.notZero),
	qi_recovery(new Color(50,195,250), "Qi Recovery Speed", DisplayType.notZero),
	qi_cultivation_rate(new Color(50,195,250), "Qi Cultivation Speed", DisplayType.never),
	;
	private enum DisplayType {
		always,never,notZero
	}


	private final Color color;
	private final String str;
	private final boolean displayed;
	private final boolean displayedAbove0;

	Stat(Color color, String str) {
		this.color = color;
		this.str = str;
		this.displayed = true;
		this.displayedAbove0 = false;
	}
	Stat(Color color, String str, DisplayType displayType) {
		this.color = color;
		this.str = str;
		this.displayed = displayType == DisplayType.always;
		this.displayedAbove0 = displayType == DisplayType.notZero;
	}

	public Color getColor() {
		return color;
	}

	public String getStr() {
		return str;
	}

	public boolean isDisplayed(float n) {
		return displayed || (displayedAbove0 && n > 0);
	}
}
