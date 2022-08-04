package com.notbestlord.core.physics;

public class TerrainCollider extends Collider{
    public TerrainCollider(){
        super(null,1,1,1);
    }
    public TerrainCollider(BoundingBox3f boundingBox, float bounciness, float friction, float density) {
        super(boundingBox, bounciness, friction, density);
    }
}
