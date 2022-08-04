package com.notbestlord.core.rpg.stats;

import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.utils.Consts;

public class StatusEffect {
	private StatusEffectType effectType;
	private float duration;

	public StatusEffectType getEffectType() {
		return effectType;
	}

	public float getDuration() {
		return duration;
	}

	public StatusEffect(StatusEffectType effectType, float duration) {
		this.effectType = effectType;
		this.duration = duration;
	}

	public StatusEffect(StatusEffect effect) {
		this.effectType = effect.effectType;
		this.duration = effect.duration;
	}

	public void start(IRPGEntity entity){
		effectType.applyStartEffect(entity);
	}

	public void end(IRPGEntity entity){
		effectType.applyEndEffect(entity);
	}

	public boolean frameUpdate(IRPGEntity entity){
		if(duration == -1){
			effectType.applyEffect(entity);
			return false;
		}
		duration -= Consts.trueDeltaTime;
		if(duration <= 0){
			return true;
		}
		else{
			effectType.applyEffect(entity);
			return false;
		}
	}

}
