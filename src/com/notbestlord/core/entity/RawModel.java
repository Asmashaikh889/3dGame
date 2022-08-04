package com.notbestlord.core.entity;

public class RawModel {

    private int id;
    private int vertexCount;
    private Material material;


    public RawModel(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material();
    }

    public RawModel(int id, int vertexCount, Material material) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = material;
    }

    public RawModel(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
    }

    public RawModel(RawModel model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
        material.setTexture(texture);
    }
    public RawModel(RawModel model) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.material = model.getMaterial();
    }

	public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Texture getTexture() {
        return this.material.getTexture();
    }
    public void setTexture(Texture texture) {
         this.material.setTexture(texture);
    }
    public void setTexture(Texture texture, float reflectance) {
        this.material.setTexture(texture);
        this.material.setReflectance(reflectance);
    }

}
