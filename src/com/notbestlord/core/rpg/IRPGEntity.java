package com.notbestlord.core.rpg;

import com.notbestlord.core.rpg.stats.StatHandler;
import com.notbestlord.core.rpg.stats.StatusEffect;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.rpg.stats.Stat;
import org.joml.Vector3f;

import java.util.List;

public interface IRPGEntity {

	float getStat(Stat stat);

	StatHandler getStatHandler();

	void setStat(Stat stat, float n);

	void addStatusEffect(StatusEffect statusEffect);

	void removeStatusEffect(StatusEffectType effectType);

	List<StatusEffect> getStatusEffects();

	String getUuid();

	Vector3f getPosition();
}
