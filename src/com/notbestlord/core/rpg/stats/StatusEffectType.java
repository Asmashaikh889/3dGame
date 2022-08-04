package com.notbestlord.core.rpg.stats;

import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.rpg.RPGUtils;
import com.notbestlord.core.utils.Consts;

import java.util.function.Consumer;

public enum StatusEffectType {
	burn("stat.health - 0.5 dt", null, ""),
	electrocute("stat.health - 0.5 dt", null, ""),
	stun(null, null, ""),
	poison("stat.health - 0.5 dt", null, ""),
	freeze("stat.health - 0.5 dt", null, ""),
	bleed("stat.health - 0.5 dt", null, ""),
	mana_poisoning("stat.strength * 0.75",null,
			"stat.strength / 0.75", "Mana Poisoning:\ncaused by having MAX MANA\nlarger than 2.5x MAX HEALTH.\n-25% strength."),
	regeneration_1("stat.health_regeneration * 1.2", null, "stat.health_regeneration / 1.2", "Regeneration 1:\nincrease HEALTH REGENERATION \nby 20%."),
	regeneration_2("stat.health_regeneration * 1.4", null, "stat.health_regeneration / 1.4", "Regeneration 2:\nincrease HEALTH REGENERATION \nby 40%."),
	mana_generation_1("stat.mana_recovery * 1.2", null, "stat.mana_recovery / 1.2", "Mana Generation 2:\nincrease MANA RECOVERY \nby 20%."),
	mana_generation_2("stat.mana_recovery * 1.4", null, "stat.mana_recovery / 1.4", "Mana Generation 2:\nincrease MANA RECOVERY \nby 40%."),
	stamina_recovery_1("stat.stamina_recovery * 1.2", null, "stat.stamina_recovery / 1.2", "Stamina Recovery 2:\nincrease STAMINA RECOVERY \nby 20%."),
	stamina_recovery_2("stat.stamina_recovery * 1.4", null, "stat.stamina_recovery / 1.4", "Stamina Recovery 2:\nincrease STAMINA RECOVERY \nby 40%."),
	anvil("recipes.station.anvil add", null, "recipes.station.anvil remove", "Temporarily unlocks [Anvil] recipes."),
	smelter("recipes.station.smelter add", null, "recipes.station.smelter remove", "Temporarily unlocks [Smelter] recipes."),
	oven("recipes.station.oven add", null, "recipes.station.oven remove", "Temporarily unlocks [Oven] recipes."),
	crafting_bench("recipes.station.crafting_bench add", null, "recipes.station.crafting_bench remove", "Temporarily unlocks [Crafting Bench] recipes.");

	private final String start;
	private final String apply;
	private final String end;
	private final String description;

	StatusEffectType(String apply, String end, String description) {
		this.start = null;
		this.apply = apply;
		this.end = end;
		this.description = description;
	}
	StatusEffectType(String start, String apply, String end, String description) {
		this.start = start;
		this.apply = apply;
		this.end = end;
		this.description = description;
	}

	public void applyStartEffect(IRPGEntity entity){
		if(start != null) RPGUtils.triggerEvent(entity, start);
	}
	public void applyEffect(IRPGEntity entity){
		if(apply != null) RPGUtils.triggerEvent(entity, apply);
	}
	public void applyEndEffect(IRPGEntity entity){
		if(end != null) RPGUtils.triggerEvent(entity, end);
	}
	public String getDescription(){ return description; }
}
