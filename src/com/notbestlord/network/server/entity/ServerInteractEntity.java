package com.notbestlord.network.server.entity;

import com.notbestlord.core.physics.Collider;
import com.notbestlord.network.client.IWorldInteract;
import com.notbestlord.network.data.MouseButton;
import org.joml.Vector3f;

public class ServerInteractEntity extends ServerEntity implements IWorldInteract {
	public ServerEntitySpawner spawner;
	public ServerInteractEntity() {
	}

	public ServerInteractEntity(String uuid, Vector3f position, Vector3f rotation, float scale, String modelID, String textureID) {
		super(uuid, position, rotation, scale, modelID, textureID);
	}

	public ServerInteractEntity(String uuid, Vector3f position, Vector3f rotation) {
		super(uuid, position, rotation);
	}

	public ServerInteractEntity(String uuid, Vector3f position, Vector3f rotation, String modelID) {
		super(uuid, position, rotation, modelID);
	}

	@Override
	public void Interact(ServerPlayer player, MouseButton mouseButton) {}

	@Override
	public void frameUpdate() {}

	@Override
	public Collider getCollider() {
		return null;
	}

	public boolean isDead(){
		return false;
	}
	public ServerInteractEntity duplicate() {
		return new ServerInteractEntity(getUuid(), getPosition(), getRotation(), getScale(), getModelID(), getTextureID());
	}
}
