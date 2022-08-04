package com.notbestlord.network.packet.player.rpg;

public class ActiveAbilityActivatePacket {
	private String uuid;
	private String ability;

	public ActiveAbilityActivatePacket() {}

	public ActiveAbilityActivatePacket(String uuid, String ability) {
		this.uuid = uuid;
		this.ability = ability;
	}

	public String getUuid() {
		return uuid;
	}

	public String getAbility() {
		return ability;
	}
}
