package com.notbestlord.network.packet.entity;

import com.notbestlord.network.server.entity.ServerEntity;

public class EntityAddPacket {
	private ServerEntity entity;

	public EntityAddPacket() {}

	public EntityAddPacket(ServerEntity entity) {
		this.entity = entity.asEntity();
	}

	public ServerEntity getEntity() {
		return entity;
	}
}
