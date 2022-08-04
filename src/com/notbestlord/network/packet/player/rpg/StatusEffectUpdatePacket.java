package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.stats.StatusEffectType;

public class StatusEffectUpdatePacket {
	private StatusEffectType type;
	private float duration;

	public StatusEffectUpdatePacket() {}

	public StatusEffectUpdatePacket(StatusEffectType type, float duration) {
		this.type = type;
		this.duration = duration;
	}

	public StatusEffectType getType() {
		return type;
	}

	public float getDuration() {
		return duration;
	}
}
