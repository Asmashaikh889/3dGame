package com.notbestlord.network.server.entity;

import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.physics.TerrainCollider;
import com.notbestlord.network.data.TerrainData;
import org.joml.Vector3f;

public class ServerTerrain {

	private float SIZE;
	private Vector3f position;
	private String textureID;
	private Collider collider;

	public ServerTerrain(float size, Vector3f position, String textureID, float bounciness, float friction, float density) {
		this.SIZE = size * 2.5f;
		this.position = position;
		this.textureID = textureID;
		this.collider = new TerrainCollider(new BoundingBox3f(new Vector3f(0,-5,0), new Vector3f(SIZE, 0, SIZE)), bounciness, friction, density);
	}

	public TerrainData getData(){
		return new TerrainData(textureID,position,SIZE);
	}

	public float getSize() {
		return SIZE;
	}

	public Vector3f getPosition() {
		return position;
	}

	public String getTextureID() {
		return textureID;
	}

	public Collider getCollider() {
		return collider;
	}
}
