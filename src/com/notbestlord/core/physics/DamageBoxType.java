package com.notbestlord.core.physics;

import org.joml.Vector3f;

import java.util.function.Function;

public enum DamageBoxType {
	thrust(scale -> {
		return new BoundingBox3f(new Vector3f(0,-0.25f,-0.2f), new Vector3f(scale * 0.75f,0.25f,0.2f));
	}, true),
	cleave(scale -> {
		return new BoundingBox3f(new Vector3f(0,-0.5f,-0.75f * scale), new Vector3f(0.25f * scale,0.5f,0.75f * scale));
	}, true);

	private final Function<Float, BoundingBox3f> boundingBox2fFunction;
	private final boolean oneTimeEvent;

	DamageBoxType(Function<Float, BoundingBox3f> boundingBox2fFunction, boolean oneTimeEvent) {
		this.boundingBox2fFunction = boundingBox2fFunction;
		this.oneTimeEvent = oneTimeEvent;
	}

	public BoundingBox3f getBoundingBox(float scale){
		return boundingBox2fFunction.apply(scale);
	}

	public boolean isOneTimeEvent() {
		return oneTimeEvent;
	}
}
