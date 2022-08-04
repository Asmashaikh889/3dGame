package com.notbestlord.network.packet.player.rpg;

public class ActiveAbilityCooldownEndPacket {
	private String ability;

	public ActiveAbilityCooldownEndPacket() {}

	public ActiveAbilityCooldownEndPacket(String ability) {
		this.ability = ability;
	}

	public String getAbility() {
		return ability;
	}
}
