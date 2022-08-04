package com.notbestlord.network.packet.player.rpg;

import com.notbestlord.core.rpg.stats.Stat;

public class StatUpdatePacket {
	private Stat stat;
	private float n;

	public StatUpdatePacket() {}

	public StatUpdatePacket(Stat stat, float n) {
		this.stat = stat;
		this.n = n;
	}

	public Stat getStat() {
		return stat;
	}

	public float getN() {
		return n;
	}
}
