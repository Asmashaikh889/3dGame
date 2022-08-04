package com.notbestlord.network.data;

import org.joml.Vector3f;

public class TerrainData {
	private String textureID;
	private Vector3f position;
	private float size;

	public TerrainData() {}

	public TerrainData(String textureID, Vector3f position, float size) {
		this.textureID = textureID;
		this.position = position;
		this.size = size;
	}


	public String textureID() {
		return textureID;
	}

	public Vector3f position() {
		return position;
	}

	public float size() {
		return size;
	}

	@Override
	public String toString() {
		return "TerrainData[" +
				"textureID=" + textureID + ", " +
				"position=" + position + ", " +
				"size=" + size + ']';
	}

}
