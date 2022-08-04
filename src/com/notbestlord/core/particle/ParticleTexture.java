package com.notbestlord.core.particle;

public enum ParticleTexture {
	fire("particle_fire_4x4",4,true),
	hit("particle_hit_3x3",3,false),
	star("particle_star",1,true),
	swipe("particle_swipe_3x3", 3, false);

	private final String textureId;
	private final int numberOfRows;
	private final boolean stretched;

	ParticleTexture(String textureId, int numberOfRows, boolean stretched) {
		this.textureId = textureId;
		this.numberOfRows = numberOfRows;
		this.stretched = stretched;
	}

	public String getTextureId() {
		return textureId;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public boolean isStretched() {
		return stretched;
	}
}
