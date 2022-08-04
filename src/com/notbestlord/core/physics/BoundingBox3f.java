package com.notbestlord.core.physics;

import org.joml.Vector3f;

public class BoundingBox3f {
    public Vector3f min;
    public Vector3f max;
    public BoundingBox3f(){
        min = new Vector3f();
        max = new Vector3f();
    }
    public BoundingBox3f(Vector3f min, Vector3f max){
        this.min = min;
        this.max = max;
    }
    public boolean doesOverlap(BoundingBox3f other){
        return other != null && (this.min.x <= other.max.x && this.max.x >= other.min.x &&
                                 this.min.y <= other.max.y && ( this.max.y >= other.min.y || (this.max.y < 0 && other.min.y < 0 &&  this.max.y <= other.min.y)) &&
                                 this.min.z <= other.max.z && this.max.z >= other.min.z);
    }
    public BoundingBox3f relativeBox(Vector3f position){
        return new BoundingBox3f(new Vector3f(this.min).add(position), new Vector3f(this.max).add(position));
    }

    public BoundingBox3f rotateX(float rot){
        BoundingBox3f box3f = new BoundingBox3f(min,max);
        box3f.min.rotateX(rot);
        box3f.min.rotateZ(90f - rot);
        box3f.max.rotateX(rot);
        box3f.max.rotateZ(90f - rot);
        return box3f;
    }

    public BoundingBox3f rotateY(float rot){
        BoundingBox3f box3f = new BoundingBox3f(min,max);
        box3f.min.rotateY(rot);
        box3f.max.rotateY(rot);
        return box3f;
    }

    public BoundingBox3f rotateXY(float rotX, float rotY){
        rotX = (float) Math.toRadians(rotX);
        rotY = (float) Math.toRadians(rotY);
        BoundingBox3f box3f = new BoundingBox3f(min,max);
        float minX = box3f.min.x, minZ = box3f.min.z;
        box3f.min.x = (float) ((Math.cos(rotX) * minX) + (Math.sin(rotX) * minZ));
        box3f.min.y *= Math.cos(rotY);
        box3f.min.z = (float) ((Math.sin(rotX) * minX) + (Math.cos(rotX) * minZ));

        float maxX = box3f.max.x, maxZ = box3f.max.z;
        box3f.max.x = (float) ((Math.cos(rotX) * maxX) + (Math.sin(rotX) * maxZ));
        box3f.max.y *= Math.cos(rotY);
        box3f.max.z = (float) ((Math.sin(rotX) * maxX) + (Math.cos(rotX) * maxZ));
        return box3f;
    }

    @Override
    public String toString() {
        return "min=" + min + ", max=" + max;
    }
}
