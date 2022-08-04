package com.notbestlord.network.packet.entity;

import org.joml.Vector3f;

import java.util.Objects;

public class EntityMovementPacket {
	private String uuid;
	private Vector3f position;

	public EntityMovementPacket() {}

	public EntityMovementPacket(String uuid, Vector3f position) {
		this.uuid = uuid;
		this.position = new Vector3f(position);
	}

	public String uuid() {
		return uuid;
	}

	public Vector3f position() {
		return position;
	}

	@Override
	public String toString() {
		return "EntityMovementPacket[" +
				"uuid=" + uuid + ", " +
				"position=" + position + ']';
	}
}
