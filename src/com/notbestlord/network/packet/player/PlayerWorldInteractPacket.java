package com.notbestlord.network.packet.player;

import com.notbestlord.network.data.MouseButton;
import org.joml.Vector3f;

public class PlayerWorldInteractPacket {
	private String uuid;
	private Vector3f playerRotation;
	private MouseButton mouseButton;

	public PlayerWorldInteractPacket() {}

	public PlayerWorldInteractPacket(String uuid,Vector3f rotation,MouseButton mouseButton){
		this.playerRotation = rotation;
		this.mouseButton = mouseButton;
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	public Vector3f getPlayerRotation() {
		return playerRotation;
	}

	public MouseButton getMouseButton() {
		return mouseButton;
	}
}
