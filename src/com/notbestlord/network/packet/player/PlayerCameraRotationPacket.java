package com.notbestlord.network.packet.player;

import org.joml.Vector3f;

public class PlayerCameraRotationPacket {
	private String uuid;
	private Vector3f cameraRotation;

	public PlayerCameraRotationPacket() {}

	public PlayerCameraRotationPacket(String uuid, Vector3f cameraRotation) {
		this.uuid = uuid;
		this.cameraRotation = cameraRotation;
	}

	public String getUuid() {
		return uuid;
	}

	public Vector3f getCameraRotation() {
		return cameraRotation;
	}
}
