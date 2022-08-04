package com.notbestlord.core.physics;

public class PhysicsBody extends Collider{
    private float Gravity;
    private float PotentialHeightEnergy;
    private boolean bounce;
    public PhysicsBody(BoundingBox3f boundingBox, float gravity, float bounciness, float friction, float density) {
        super(boundingBox, bounciness, friction, density);
        this.Gravity = gravity;
        this.PotentialHeightEnergy = 0;
        this.bounce = true;
    }
    public PhysicsBody(BoundingBox3f boundingBox, float gravity, float bounciness, float friction, float density, boolean bounce) {
        super(boundingBox, bounciness, friction, density);
        this.Gravity = gravity;
        this.PotentialHeightEnergy = 0;
        this.bounce = bounce;
    }

    public boolean doesBounce() { return bounce; }

    public float getGravity() {
        return Gravity;
    }

    public void UpdatePotentialHeightEnergy(float height){
        if(PotentialHeightEnergy < height){
            this.PotentialHeightEnergy = height;
        }
    }
    public float getPotentialHeightEnergy(){
        return this.PotentialHeightEnergy;
    }
    public void setPotentialHeightEnergy(float height){
        this.PotentialHeightEnergy = height;
    }
}
