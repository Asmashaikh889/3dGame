package com.notbestlord.network.packet.client;

import com.notbestlord.network.data.TerrainData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TerrainPacket {
	private List<TerrainData> terrains;

	public TerrainPacket() {
		this.terrains = new ArrayList<>();
	}

	public List<TerrainData> terrains() {
		return terrains;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (TerrainPacket) obj;
		return Objects.equals(this.terrains, that.terrains);
	}

	@Override
	public int hashCode() {
		return Objects.hash(terrains);
	}

	@Override
	public String toString() {
		return "TerrainPacket[" +
				"terrains=" + terrains + ']';
	}
}
