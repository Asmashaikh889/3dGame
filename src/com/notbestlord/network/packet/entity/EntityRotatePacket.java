package com.notbestlord.network.packet.entity;

import org.joml.Vector3f;

public class EntityRotatePacket {
	private String uuid;
	private Vector3f rotation;

	public EntityRotatePacket() {}

	public EntityRotatePacket(String uuid, Vector3f position) {
		this.uuid = uuid;
		this.rotation = position;
	}

	public String getUuid() {
		return uuid;
	}

	public Vector3f getRotation() {
		return rotation;
	}
}
