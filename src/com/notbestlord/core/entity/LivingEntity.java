package com.notbestlord.core.entity;

import com.notbestlord.core.PhysicsManager;
import com.notbestlord.core.physics.Collider;
import org.joml.Vector3f;

public class LivingEntity {
    private Entity entity;
    private Vector3f Velocity;

    public Vector3f getVelocity() {
        return Velocity;
    }

    public void setVelocity(Vector3f velocity) {
        Velocity = velocity;
    }

    public LivingEntity(Entity entity, Vector3f velocity) {
        this.entity = entity;
        this.Velocity = velocity;
    }
    public Vector3f getPosition(){
        return entity.getPos();
    }
    public void setPosition(Vector3f posVec){
        entity.setPos(posVec);
    }
    public void incPosition(Vector3f moveVec){
        entity.incPos(moveVec.x, moveVec.y, moveVec.z);
    }

    public void update(PhysicsManager physicsManager){
        return;
    }

    public boolean isRendered(PhysicsManager physicsManager){
        update(physicsManager);
        return true;
    }

    public Collider getCollider(){
        return null;
    }

    public void damage(float damage){

    }
    public void damage(float damage, LivingEntity damager){

    }

    public Vector3f getRotation(){
        return entity.getRotation();
    }
    public void setRotation(Vector3f posVec){
        entity.setRotation(posVec);
    }
    public void incRotation(Vector3f moveVec){
        entity.incRotation(moveVec.x, moveVec.y, moveVec.z);
    }

    public Entity getEntityHandler(){
        return entity;
    }
}
