package com.notbestlord.core.physics;

import com.notbestlord.core.utils.Utils;
import org.joml.Vector3f;

public class Collider {
    private BoundingBox3f boundingBox;
    private float bounciness;
    private float friction;
    private float density;

    public Collider(BoundingBox3f boundingBox, float bounciness, float friction) {
        this.boundingBox = boundingBox;
        this.bounciness = bounciness;
        this.friction = friction;
    }
    public Collider(BoundingBox3f boundingBox, float bounciness, float friction, float density) {
        this(boundingBox, bounciness, friction);
        this.density = density;
    }
    public Collider(BoundingBox3f boundingBox){
        this(boundingBox,0,0);
    }
    public Collider(){}
    public float getDensity() {
        return density;
    }

    public boolean doesOverlap(Vector3f thisPosition, Collider otherCollider, Vector3f otherPosition){
        return otherCollider.getBoundingBox().relativeBox(otherPosition).doesOverlap(this.getBoundingBox().relativeBox(thisPosition));
    }

    public Vector3f direction(Collider other, Vector3f otherPosition, Vector3f thisPosition){
        Vector3f direction = new Vector3f(0,0,0);
        BoundingBox3f otherBoundingBox = other.getBoundingBox().relativeBox(otherPosition);
        BoundingBox3f thisBoundingBox = this.boundingBox.relativeBox(thisPosition);
        float dXMin = Utils.difference(otherBoundingBox.max.x, thisBoundingBox.min.x);
        float dXMax = Utils.difference(otherBoundingBox.min.x, thisBoundingBox.max.x);
        float dYMin = Utils.difference(otherBoundingBox.max.y, thisBoundingBox.min.y);
        float dYMax = Utils.difference(otherBoundingBox.min.y, thisBoundingBox.max.y);
        float dZMin = Utils.difference(otherBoundingBox.max.z, thisBoundingBox.min.z);
        float dZMax = Utils.difference(otherBoundingBox.min.z, thisBoundingBox.max.z);

        float d = Math.min(Math.min(dXMax,dXMin), Math.min(Math.min(dYMax,dYMin), Math.min(dZMax,dZMin)));

        if(d == dYMin || d == dYMax) {
            direction.y = d == dYMax? 1f : -1f;
        }
        else if(d == dZMin || d == dZMax) {
            direction.z = d == dZMax ? 1f : -1f;
        }
        else if(d == dXMin || d == dXMax) {
            direction.x = d == dXMax ? 1f : -1f;
        }

        return direction;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public BoundingBox3f getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox3f boundingBox) {
        this.boundingBox = boundingBox;
    }

    public float getBounciness() {
        return bounciness;
    }

    public void setBounciness(float bounciness) {
        this.bounciness = bounciness;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }
}
