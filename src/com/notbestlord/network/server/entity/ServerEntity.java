package com.notbestlord.network.server.entity;

import com.notbestlord.core.entity.EntityType;
import com.notbestlord.core.utils.Consts;
import org.joml.Vector3f;

public class ServerEntity {
	private String uuid;
	private Vector3f position;
	private Vector3f rotation;
	private float scale;
	private String modelID;
	private String textureID;
	private Vector3f velocity = new Vector3f();

	public ServerEntity() {}

	public ServerEntity(String uuid, Vector3f position, Vector3f rotation, float scale, String modelID, String textureID) {
		this.uuid = uuid;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.modelID = modelID;
		this.textureID = textureID;
	}
	public ServerEntity(String uuid, Vector3f position, Vector3f rotation) {
		this.uuid = uuid;
		this.position = position;
		this.rotation = rotation;
		this.scale = 1;
		this.modelID = "";
		this.textureID = "";
	}
	public ServerEntity(String uuid, Vector3f position, Vector3f rotation, String modelID) {
		this.uuid = uuid;
		this.position = position;
		this.rotation = rotation;
		this.scale = 1;
		this.modelID = modelID;
		this.textureID = "";
	}

	public String getUuid() {
		return uuid;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public String getModelID() {
		return modelID;
	}

	public String getTextureID() {
		return textureID;
	}

	public EntityType getEntityType(){
		return EntityType.entity;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public ServerEntity asEntity(){
		return new ServerEntity(uuid, position, rotation, scale, modelID, textureID);
	}

	public void frameUpdate() {
		getVelocity().y -= Consts.GRAVITY_ACCEL * Consts.deltaTime;
		if(getVelocity().y < -Consts.TERMINAL_VELOCITY_Y){
			getVelocity().y = -Consts.TERMINAL_VELOCITY_Y;
		}
		//
		getPosition().x += getVelocity().x;
		getPosition().y += getVelocity().y;
		getPosition().z += getVelocity().z;
	}
}
