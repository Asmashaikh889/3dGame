package com.notbestlord.network.packet.entity;

import java.util.Objects;

public class EntityRemovePacket {
	private String uuid;

	public EntityRemovePacket() {}

	public EntityRemovePacket(String uuid) {
		this.uuid = uuid;
	}

	public String uuid() {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (EntityRemovePacket) obj;
		return Objects.equals(this.uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public String toString() {
		return "EntityRemovePacket[" +
				"uuid=" + uuid + ']';
	}
}
