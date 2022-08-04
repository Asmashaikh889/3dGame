package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.abilites.PassiveAbility;

public class UpdatePassiveAbilityStatePacket {
	private PassiveAbility ability;
	private boolean removed;

	public UpdatePassiveAbilityStatePacket() {}

	public UpdatePassiveAbilityStatePacket(PassiveAbility ability, boolean removed) {
		this.ability = new PassiveAbility(ability.getId(), ability.getDisplayName(), "", "", ability.description());
		this.removed = removed;
	}

	public PassiveAbility getAbility() {
		return ability;
	}

	public boolean isRemoved() {
		return removed;
	}
}
