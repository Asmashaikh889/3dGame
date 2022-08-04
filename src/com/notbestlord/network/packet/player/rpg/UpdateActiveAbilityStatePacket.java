package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.abilites.ActiveAbility;

public class UpdateActiveAbilityStatePacket {
	private ActiveAbility ability;
	private boolean removed;

	public UpdateActiveAbilityStatePacket() {}

	public UpdateActiveAbilityStatePacket(ActiveAbility ability, boolean removed) {
		this.ability = new ActiveAbility(ability.getId(), ability.getDisplayName(), "", ability.duration(), "", ability.cooldown(), ability.description(), ability.textureID());
		this.removed = removed;
	}

	public ActiveAbility getAbility() {
		return ability;
	}

	public boolean isRemoved() {
		return removed;
	}
}
