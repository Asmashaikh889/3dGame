package com.notbestlord.core;

import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.physics.DamageBox;
import com.notbestlord.core.physics.PhysicsBody;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.server.entity.ServerEntity;
import com.notbestlord.network.server.entity.ServerPlayer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsManager {
    private static class PhysicsData{
        private Collider collider;
        private Vector3f position;

        public PhysicsData(Collider collider, Vector3f position) {
            this.collider = collider;
            this.position = position;
        }

        public Collider getCollider() {
            return collider;
        }

        public void setCollider(Collider collider) {
            this.collider = collider;
        }

        public Vector3f getPosition() {
            return position;
        }

        public void setPosition(Vector3f position) {
            this.position = position;
        }
    }

    private final List<PhysicsData> PhysicalBodies;
    private final List<DamageBox> damageBoxes;
    private final Map<String, PhysicsData> PhysicalEntities;

    public PhysicsManager(){
        this.PhysicalBodies = new ArrayList<>();
        this.PhysicalEntities = new HashMap<>();
        this.damageBoxes = new ArrayList<>();
    }

    public void add(Collider collider, Vector3f position){
        PhysicalBodies.add(new PhysicsData(collider, position));
    }
    public void addEntity(String uuid, Collider collider, Vector3f position){
        PhysicsData data = new PhysicsData(collider, position);
        PhysicalBodies.add(data);
        PhysicalEntities.put(uuid, data);
    }
    public void removeEntity(String uuid){
        if(PhysicalEntities.containsKey(uuid)) {
            PhysicalBodies.remove(PhysicalEntities.get(uuid));
            PhysicalEntities.remove(uuid);
        }
    }

    public void addDamageBox(DamageBox box){
        damageBoxes.add(box);
    }

    public boolean doesUpwardsCollisionOccur(Vector3f otherPosition, Collider otherCollider, float minDensity){
        for(PhysicsData physicsData : PhysicalBodies) {
            Collider thisCollider = physicsData.getCollider();
            Vector3f thisPosition = physicsData.getPosition();
            if (thisCollider != otherCollider && thisPosition != otherPosition &&
                    thisCollider.getBoundingBox().relativeBox(thisPosition).doesOverlap(otherCollider.getBoundingBox().relativeBox(otherPosition)) && thisCollider.getDensity() >= minDensity) {
                Vector3f dir = thisCollider.direction(otherCollider, otherPosition, thisPosition);
                if(dir.y == 1) return true;
            }
        }
        return false;
    }
    
    private static boolean clipLine(int d, BoundingBox3f box, Vector3f v0, Vector3f v1, Float f_low, Float f_high){
        float f_dim_low, f_dim_high;

        f_dim_low = (box.min.get(d) - v0.get(d)) / (v1.get(d) - v0.get(d));
        f_dim_high = (box.max.get(d) - v0.get(d)) / (v1.get(d) - v0.get(d));

        if(f_dim_high < f_dim_low){
            float t = f_dim_high;
            f_dim_high = f_dim_low;
            f_dim_low = t;
        }
        if(f_dim_high < f_low){
            return false;
        }
        if(f_dim_low > f_high){
            return false;
        }

        f_low = Math.max(f_dim_low, f_low);
        f_high = Math.min(f_dim_high, f_high);

        if(f_low > f_high){
            return false;
        }

        return true;
    }
    
    public static boolean lineBoxOverlap(Vector3f lineStart, Vector3f lineEnd, BoundingBox3f boundingBox3f, Vector3f colliderPos){
        lineEnd.add(lineStart);
        BoundingBox3f box = boundingBox3f.relativeBox(colliderPos);
        Float f_low = 0f;
        Float f_high = 1f;

        if(!clipLine(0,box,lineStart,lineEnd,f_low,f_high)){
            return false;
        }

        if(!clipLine(1,box,lineStart,lineEnd,f_low,f_high)){
            return false;
        }

        if(!clipLine(2,box,lineStart,lineEnd,f_low,f_high)){
            return false;
        }

        return true;
    }

    public void setPostCollisionPos(ServerEntity entity, Vector3f entityPos, Collider entityCollider, float minDensity){
        Vector3f out = new Vector3f(entityPos);
        for(PhysicsData physicsData : PhysicalBodies) {
            Collider thisCollider = physicsData.getCollider();
            Vector3f thisPosition = physicsData.getPosition();
            if(thisCollider != entityCollider && thisPosition != entityPos && thisCollider.doesOverlap(thisPosition, entityCollider, out) && thisCollider.getDensity() >= minDensity){
                Vector3f dir = thisCollider.direction(entityCollider, out, thisPosition);
                BoundingBox3f entityBox = entityCollider.getBoundingBox().relativeBox(out);
                BoundingBox3f thisBox = thisCollider.getBoundingBox().relativeBox(thisPosition);

                if(dir.x != 0 && thisCollider.getDensity() > minDensity){
                    if(dir.x < 0) {
                        out.x -= Utils.difference(entityBox.max.x, thisBox.min.x);
                    }
                    else{
                        out.x += Utils.difference(entityBox.min.x, thisBox.max.x);
                    }
                }

                if (dir.y != 0 && thisCollider.getDensity() > minDensity) {
                    if(dir.y < 0) {
                        out.y -= Utils.difference(entityBox.max.y, thisBox.min.y);
                    }
                    else{
                        out.y += Utils.difference(entityBox.min.y, thisBox.max.y);
                    }
                }

                if (dir.z != 0 && thisCollider.getDensity() > minDensity) {
                    if (dir.z < 0) {
                        out.z -= Utils.difference(entityBox.max.z, thisBox.min.z);
                    } else {
                        out.z += Utils.difference(entityBox.min.z, thisBox.max.z);
                    }
                }

                if(entityCollider instanceof PhysicsBody && ((PhysicsBody) entityCollider).doesBounce()){
                    if(dir.x != 0 && thisCollider.getDensity() >= minDensity){
                        entity.getVelocity().x *= -Math.abs(dir.x);
                    }

                    if (dir.y != 0 && thisCollider.getDensity() >= minDensity &&
                            !Utils.isInRange(((PhysicsBody) entityCollider).getPotentialHeightEnergy(), -0.1f, 0.1f)) {
                        entity.getVelocity().y *= -(thisCollider.getBounciness() * Math.abs(dir.y));
                    }

                    if (dir.z != 0 && thisCollider.getDensity() >= minDensity) {
                        entity.getVelocity().z *= -Math.abs(dir.z);
                    }

                    if(dir.y != 0){
                        float deceleration = ((PhysicsBody) entityCollider).getGravity() * (1.0f / thisCollider.getFriction()) * Consts.deltaTime;
                        entity.getVelocity().x *= 1.0f - deceleration;
                        entity.getVelocity().z *= 1.0f - deceleration;
                    }
                }
                else{
                    if(dir.y != 0){
                        // normal bounce stopping
                        if ((entity.getVelocity().y <= 0.0025f && entity.getVelocity().y >= -0.0025f)) {
                            entity.getVelocity().y = 0;
                        }
                        // normal friction
                        entity.getVelocity().x *= 1.0f - (thisCollider.getFriction() * Consts.deltaTime);
                        entity.getVelocity().z *= 1.0f - (thisCollider.getFriction() * Consts.deltaTime);
                    }
                }
            }
        }
        entity.getPosition().set(out);
        BoundingBox3f entityBox = entityCollider.getBoundingBox().relativeBox(out);
        if(entity instanceof IRPGEntity rpgEntity){
            for(DamageBox damageBox : damageBoxes){
                damageBox.damageBoxEvent(rpgEntity, entityBox);
            }
        }
    }

    public Vector3f calcPostCollisionPlayerPos(ServerPlayer player, Vector3f otherPosition, float minDensity){
        Vector3f out = new Vector3f(otherPosition);
        Collider otherCollider = player.getCollider();
        for(int i=0; i<PhysicalBodies.size();i++) {
            PhysicsData physicsData = PhysicalBodies.get(i);
            Collider thisCollider = physicsData.getCollider();
            Vector3f thisPosition = physicsData.getPosition();
            if(thisCollider != otherCollider && thisPosition != otherPosition && thisCollider.doesOverlap(thisPosition, otherCollider, out) && thisCollider.getDensity() >= minDensity){
                Vector3f dir = thisCollider.direction(player.getCollider(), out, thisPosition);
                BoundingBox3f entityBox = otherCollider.getBoundingBox().relativeBox(out);
                BoundingBox3f thisBox = thisCollider.getBoundingBox().relativeBox(thisPosition);

                if (dir.y != 0) {
                    if(dir.y < 0 && player.getPosition().y < otherPosition.y) {
                        out.y -= Utils.difference(entityBox.max.y, thisBox.min.y);
                    }
                    else if(dir.y > 0){
                        out.y += Utils.difference(entityBox.min.y, thisBox.max.y);
                        player.getFlags().setFlag("CanJump", 0);
                    }
                }

                if(dir.x != 0){
                    if(dir.x < 0) {
                        out.x -= Utils.difference(entityBox.max.x, thisBox.min.x);
                    }
                    else{
                        out.x += Utils.difference(entityBox.min.x, thisBox.max.x);
                    }
                }

                if (dir.z != 0) {
                    if (dir.z < 0) {
                        out.z -= Utils.difference(entityBox.max.z, thisBox.min.z);
                    } else {
                        out.z += Utils.difference(entityBox.min.z, thisBox.max.z);
                    }
                }
            }
        }
        return out;
    }

}
