package com.notbestlord.network.server;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityBuffer {
	private final int bufferSize;
	private final Map<String, List<Vector3f>> positions = new HashMap<>();
	private final Map<String, List<Vector3f>> rotations = new HashMap<>();

	public EntityBuffer(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public Vector3f pushToPositionBuffer(String uuid, Vector3f position){
		if(!positions.containsKey(uuid)){
			positions.put(uuid, new ArrayList<>());
		}
		positions.get(uuid).add(0, new Vector3f(position));
		if(positions.get(uuid).size() > bufferSize + 1){
			Vector3f lastPos = positions.get(uuid).remove(positions.get(uuid).size()-1);
			return lastPos.equals(positions.get(uuid).get(positions.get(uuid).size()-1)) ? null : positions.get(uuid).get(positions.get(uuid).size()-1);
		}
		return null;
	}

	public Vector3f pushToRotationBuffer(String uuid, Vector3f rotation){
		if(!rotations.containsKey(uuid)){
			rotations.put(uuid, new ArrayList<>());
		}
		rotations.get(uuid).add(0, new Vector3f(rotation));
		if(rotations.get(uuid).size() > bufferSize + 1){
			Vector3f lastRot = rotations.get(uuid).remove(rotations.get(uuid).size()-1);
			return lastRot.equals(rotations.get(uuid).get(rotations.get(uuid).size()-1)) ? null : rotations.get(uuid).get(rotations.get(uuid).size()-1);
		}
		return null;
	}

	public void removeFromBuffer(String uuid){
		positions.get(uuid).clear();
		positions.remove(uuid);
		rotations.get(uuid).clear();
		rotations.remove(uuid);
	}
}
