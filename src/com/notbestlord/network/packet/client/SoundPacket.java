package com.notbestlord.network.packet.client;

import org.joml.Vector3f;

public class SoundPacket {
	private String soundId;
	private Vector3f location;

	public SoundPacket() {}

	public SoundPacket(String soundId, Vector3f location) {
		this.soundId = soundId;
		this.location = location;
	}

	public SoundPacket(String soundId) {
		this.soundId = soundId;
	}

	public String getSoundId() {
		return soundId;
	}

	public Vector3f getLocation() {
		return location;
	}
}
