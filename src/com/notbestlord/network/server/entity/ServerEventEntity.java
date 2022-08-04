package com.notbestlord.network.server.entity;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.rpg.RPGUtils;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.data.MouseButton;
import org.joml.Vector3f;

import java.util.UUID;

public class ServerEventEntity extends ServerInteractEntity {
	private final String event;
	private final Collider collider;

	public ServerEventEntity(Vector3f position, float scale, String textureId, String event) {
		super(UUID.randomUUID().toString(), position, new Vector3f(), scale,"cube", textureId);
		collider = new Collider(new BoundingBox3f(new Vector3f(-scale,-scale,-scale), new Vector3f(scale, scale, scale)),1,1,1);
		this.event = event;
	}

	public ServerEventEntity(Vector3f position, float scale, String model, String textureId, String event) {
		super(UUID.randomUUID().toString(), position, new Vector3f(), scale,model, textureId);
		collider = new Collider(new BoundingBox3f(new Vector3f(-scale,-scale,-scale), new Vector3f(scale, scale, scale)),1,1,1);
		this.event = event;
	}

	@Override
	public void frameUpdate() {
		super.frameUpdate();
		//
		ServerLauncher.getGameServer().getPhysics().setPostCollisionPos(this, getPosition(), collider, 0);
	}

	@Override
	public void Interact(ServerPlayer player, MouseButton mouseButton) {
		if(mouseButton == MouseButton.right){
			RPGUtils.triggerEvent(player, event);
		}
	}

	@Override
	public Collider getCollider() {
		return collider;
	}
}
