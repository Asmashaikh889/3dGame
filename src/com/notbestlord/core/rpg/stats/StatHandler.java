package com.notbestlord.core.rpg.stats;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.inventory.equipment.Attribute;
import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.packet.player.rpg.StatUpdatePacket;
import com.notbestlord.network.packet.player.rpg.StatusEffectUpdatePacket;

import java.util.*;

public class StatHandler {
	private String uuid;
	private Map<Stat, Float> stats;
	private Map<Stat, Float> statMuls;
	private Map<Attribute, Float> attributeResistances;
	private Map<StatusEffectType, StatusEffect> statusEffects;

	@Expose(serialize = false)
	private IRPGEntity owner;

	public StatHandler() {
		this.uuid = null;
		stats = new HashMap<>();
		for(Stat stat : Stat.values()){
			stats.put(stat, 0f);
		}
		stats.put(Stat.health_max, 100f);
		stats.put(Stat.health, 100f);
		stats.put(Stat.health_regeneration, 100f);
		stats.put(Stat.mana_recovery, 100f);
		stats.put(Stat.stamina_recovery, 100f);
		statMuls = new HashMap<>();
		for(Stat stat : Stat.values()){
			statMuls.put(stat, 1f);
		}
		attributeResistances = new HashMap<>();
		for(Attribute attribute : Attribute.values()){
			attributeResistances.put(attribute, 0f);
		}
		statusEffects = new HashMap<>();
	}

	public StatHandler(String uuid) {
		this.uuid = uuid;
		stats = new HashMap<>();
		for(Stat stat : Stat.values()){
			stats.put(stat, 0f);
		}
		stats.put(Stat.health_max, 100f);
		stats.put(Stat.health, 100f);
		stats.put(Stat.health_regeneration, 100f);
		stats.put(Stat.mana_recovery, 100f);
		stats.put(Stat.stamina_recovery, 100f);
		statMuls = new HashMap<>();
		for(Stat stat : Stat.values()){
			statMuls.put(stat, 1f);
		}
		attributeResistances = new HashMap<>();
		for(Attribute attribute : Attribute.values()){
			attributeResistances.put(attribute, 0f);
		}
		statusEffects = new HashMap<>();
	}

	public void init(IRPGEntity owner){
		for(Stat stat : Stat.values()){
			if(!stats.containsKey(stat)){
				stats.put(stat, 0f);
			}
			if(!statMuls.containsKey(stat)){
				statMuls.put(stat, 1f);
			}
		}
		for(StatusEffect effect : statusEffects.values()){
			ServerLauncher.getGameServer().sendPacketToPlayer(uuid, new StatusEffectUpdatePacket(effect.getEffectType(), effect.getDuration()));
		}
		this.owner = owner;
	}

	public void sendStatUpdatePacket(Stat stat){
		if(uuid != null) {
			ServerLauncher.getGameServer().sendPacketToPlayer(uuid, new StatUpdatePacket(stat, stats.get(stat)));
		}
	}

	public float getStat(Stat stat){
		return stats.get(stat);
	}

	public void setStat(Stat stat, float n){
		stats.put(stat,n);
		sendStatUpdatePacket(stat);
	}

	public void incStat(Stat stat, float n){
		stats.put(stat,stats.get(stat) + (n * statMuls.get(stat)));
		sendStatUpdatePacket(stat);
	}

	public float getStatMul(Stat stat){
		return statMuls.get(stat);
	}

	public void setStatMul(Stat stat, float n){
		setStat(stat, getStat(stat) / statMuls.get(stat));
		statMuls.put(stat,n); setStat(stat, getStat(stat) * statMuls.get(stat));
		sendStatUpdatePacket(stat);
	}

	public void incStatMul(Stat stat, float n){
		setStatMul(stat, statMuls.get(stat) * n);
		sendStatUpdatePacket(stat);
	}
	public boolean hasStat(Stat stat){
		return stats.containsKey(stat);
	}

	public float getAttributeRes(Attribute attribute){
		return attributeResistances.get(attribute);
	}

	public void setAttributeRes(Attribute attribute, float n){
		attributeResistances.put(attribute,n);
	}

	public float calcIntakeDamage(Attribute damageAttribute, float damage, StatHandler damager){
		if(Math.random() < stats.get(Stat.dexterity) / 1000) return 0;
		float initial = damage *  (1 + damager.getStat(Stat.strength)/50);
		float attributeMul = 1f - attributeResistances.get(damageAttribute);
		float mul = damageAttribute == Attribute.magic ? (1 + stats.get(Stat.mana_max) / 500) : (1 + stats.get(Stat.endurance) / 250);
		float Final = 1 + (Math.random() < damager.getStat(Stat.critical_chance) / 100 ? damager.getStat(Stat.critical_damage) / 100 : 0) + getBrutalityMul(damager.getStat(Stat.brutality));
		return (initial * attributeMul / mul) * Final;
	}

	public float calcIntakeDamage(Attribute damageAttribute, float damage){
		if(Math.random() < stats.get(Stat.dexterity) / 1000) return 0;
		float attributeMul = 1f - attributeResistances.get(damageAttribute);
		float mul = damageAttribute == Attribute.magic ? (1 + stats.get(Stat.mana_max) / 500) : (1 + stats.get(Stat.endurance) / 250);
		return damage * attributeMul / mul;
	}

	public float getBrutalityMul(float n){
		if(n <= 0) return 0;
		return (Math.random() < n / 100 ? 1 : 0) + getBrutalityMul(n - 100);
	}

	public void frameUpdate(){
		// Handle status effects
		List<StatusEffectType> effectTypes = new ArrayList<>(statusEffects.keySet());
		for(StatusEffectType effectType : effectTypes){
			if(statusEffects.get(effectType).frameUpdate(owner)){
				statusEffects.remove(effectType);
				effectType.applyEndEffect(owner);
				ServerLauncher.getGameServer().sendPacketToPlayer(uuid, new StatusEffectUpdatePacket(effectType, 0));
			}
		}

		if(getStat(Stat.health) < getStat(Stat.health_max)){
			incStat(Stat.health, getStat(Stat.health_max) * 0.1f * (getStat(Stat.health_regeneration) / 100) * Consts.deltaTime/100);
			if(getStat(Stat.health) > getStat(Stat.health_max)){
				setStat(Stat.health, getStat(Stat.health_max));
			}
		}

		if(getStat(Stat.mana) < getStat(Stat.mana_max)){
			incStat(Stat.mana, getStat(Stat.mana_max) * 0.1f * (getStat(Stat.mana_recovery) / 100) * Consts.deltaTime/100);
			if(getStat(Stat.mana) > getStat(Stat.mana_max)){
				setStat(Stat.mana, getStat(Stat.mana_max));
			}
		}

		if(getStat(Stat.stamina) < getStat(Stat.stamina_max)){
			incStat(Stat.stamina, getStat(Stat.stamina_max) * 0.1f * (getStat(Stat.stamina_recovery) / 100) * Consts.deltaTime/100);
			if(getStat(Stat.stamina) > getStat(Stat.stamina_max)){
				setStat(Stat.stamina, getStat(Stat.stamina_max));
			}
		}

		if(getStat(Stat.qi_max) != 0 && getStat(Stat.qi) < getStat(Stat.qi_max)){
			incStat(Stat.qi, getStat(Stat.qi_max) * 0.01f * (1 + (getStat(Stat.qi_recovery) / 100)) * Consts.deltaTime/100);
			if(getStat(Stat.qi) > getStat(Stat.qi_max)){
				setStat(Stat.qi, getStat(Stat.qi_max));
			}
		}
	}

	public void addStatusEffect(StatusEffect effect){
		if(statusEffects.containsKey(effect.getEffectType())){
			if(statusEffects.get(effect.getEffectType()).getDuration() < effect.getDuration()){
				statusEffects.get(effect.getEffectType()).end(owner);
				statusEffects.put(effect.getEffectType(), effect);
				effect.start(owner);
				ServerLauncher.getGameServer().sendPacketToPlayer(uuid, new StatusEffectUpdatePacket(effect.getEffectType(), effect.getDuration()));
			}
		}
		else{
			statusEffects.put(effect.getEffectType(), effect);
			effect.start(owner);
			ServerLauncher.getGameServer().sendPacketToPlayer(uuid, new StatusEffectUpdatePacket(effect.getEffectType(), effect.getDuration()));
		}
	}

	public void removeStatusEffect(StatusEffectType effectType){
		if(statusEffects.containsKey(effectType)) {
			statusEffects.remove(effectType);
			effectType.applyEndEffect(owner);
			ServerLauncher.getGameServer().sendPacketToPlayer(uuid, new StatusEffectUpdatePacket(effectType, 0));
		}
	}

	public List<StatusEffect> getStatusEffects(){
		List<StatusEffect> out = new ArrayList<>();
		for(StatusEffect effect : statusEffects.values()){
			out.add(new StatusEffect(effect));
		}
		return out;
	}

	public boolean hasStatusEffect(StatusEffectType type){
		return statusEffects.containsKey(type);
	}

	public void incInnerQi(float inc){
		incStat(Stat.qi_max, inc * getStat(Stat.qi_cultivation_rate));
	}

	@Override
	public String toString() {
		return "StatHandler{" +
				"stats=" + stats +
				", \nattributeResistances=" + attributeResistances +
				'}';
	}
}
