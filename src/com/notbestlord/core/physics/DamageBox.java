package com.notbestlord.core.physics;

import com.notbestlord.core.PhysicsManager;
import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.rpg.RPGUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class DamageBox {
	private BoundingBox3f box;
	private Vector3f location;
	private String event;
	private float rotX, rotY;
	private List<String> damagedEntities;

	public DamageBox(BoundingBox3f box, Vector3f location, String event, float rotationX) {
		this.box = box;
		this.location = location;
		this.event = event;
		this.rotX = rotationX;
	}

	public DamageBox(DamageBoxType boxType, float scale, Vector3f location, String event, float rotationX, float rotationY) {
		this.box = boxType.getBoundingBox(scale);
		this.location = location;
		this.event = event;
		this.rotX = rotationX;
		this.rotY = rotationY;
		if(boxType.isOneTimeEvent()){
			damagedEntities = new ArrayList<>();
		}
	}

	public void damageBoxEvent(IRPGEntity entity, BoundingBox3f other){
		if((damagedEntities == null || !damagedEntities.contains(entity.getUuid()))){
			//
			BoundingBox3f box = this.box.relativeBox(location).rotateXY(rotX, rotY);
			Vector3f v1 = new Vector3f(box.min);
			v1.y = box.max.y;
			Vector3f v2 = new Vector3f(box.min);
			v1.x = box.max.x;
			Vector3f v3 = new Vector3f(box.min);
			v1.z = box.max.z;
			Vector3f v4 = new Vector3f(box.max);
			v1.y = box.min.y;
			Vector3f v5 = new Vector3f(box.max);
			v1.x = box.min.x;
			Vector3f v6 = new Vector3f(box.max);
			v1.z = box.min.z;

			boolean overlap = false;

			if(PhysicsManager.lineBoxOverlap(box.min, v1, other, entity.getPosition())){
				overlap = true;
			}
			else if(PhysicsManager.lineBoxOverlap(box.min, v2, other, entity.getPosition())){
				overlap = true;
			}
			else if(PhysicsManager.lineBoxOverlap(box.min, v3, other, entity.getPosition())){
				overlap = true;
			}
			else if(PhysicsManager.lineBoxOverlap(v4, box.max, other, entity.getPosition())){
				overlap = true;
			}
			else if(PhysicsManager.lineBoxOverlap(v5, box.max, other, entity.getPosition())){
				overlap = true;
			}
			else if(PhysicsManager.lineBoxOverlap(v6, box.max, other, entity.getPosition())){
				overlap = true;
			}
			//
			if(overlap) {
				System.out.println(event);
				RPGUtils.applyEventEffect(entity, event);
				if (damagedEntities != null) {
					damagedEntities.add(entity.getUuid());
				}
			}
		}
	}
}
