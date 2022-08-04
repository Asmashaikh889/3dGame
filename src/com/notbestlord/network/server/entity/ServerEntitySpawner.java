package com.notbestlord.network.server.entity;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.core.utils.Range;
import org.joml.Vector3f;

public class ServerEntitySpawner {
	private BoundingBox3f box;
	private ServerInteractEntity entity;
	private int maxEntities, entities = 0;
	private float chance, timer;

	public ServerEntitySpawner(Vector3f min, Vector3f max,ServerInteractEntity entity, int maxEntities, float chance) {
		this.box = new BoundingBox3f(min,max);
		this.entity = entity;
		this.maxEntities = maxEntities;
		this.chance = chance;
	}

	public void tickUpdate(){
		timer += Consts.trueDeltaTime;
		if(timer >= 1f){
			timer -= 1f;
			if(Math.random() < chance && maxEntities - 1 > entities){
				entities++;
				ServerInteractEntity entity = this.entity.duplicate();
				entity.getPosition().set(Range.randomFloat(box.min.x,box.max.x), Range.randomFloat(box.min.y, box.max.y), Range.randomFloat(box.min.z,box.max.z));
				entity.spawner = this;
				ServerLauncher.getGameServer().addEntity(entity);
			}
		}
	}

	public void entityDeath(){
		entities--;
	}
}
