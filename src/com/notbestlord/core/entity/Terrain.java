package com.notbestlord.core.entity;

import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.entity.Material;
import com.notbestlord.core.entity.RawModel;
import com.notbestlord.core.entity.Texture;
import org.joml.Vector3f;

public class Terrain {
    private static final int VERTEX_COUNT = 256;

    private float SIZE;
    private Vector3f position;
    private RawModel model;

    public Terrain(float size, Vector3f position, ObjectLoader loader, Material material) {
        this.SIZE = size;
        this.position = position;
        this.model = generateTerrain(loader);
        this.model.setMaterial(material);
    }

    public RawModel generateTerrain(ObjectLoader loader){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count*3];
        float[] normals = new float[count*3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;

        for(int i=0; i < VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT ; j++){
                vertices[vertexPointer * 3] = j / (VERTEX_COUNT - 1.0f) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0; // height Map
                vertices[vertexPointer * 3 + 2] = i / (VERTEX_COUNT - 1.0f) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = j / (VERTEX_COUNT - 1.0f) * SIZE;
                textureCoords[vertexPointer * 2 + 1] = i / (VERTEX_COUNT - 1.0f) * SIZE;
                vertexPointer ++;
            }
        }
        int pointer = 0;
        for(int z = 0; z < VERTEX_COUNT - 1.0f; z++){
            for(int x = 0; x < VERTEX_COUNT - 1.0f; x++){
                int topLeft = (z * VERTEX_COUNT) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * VERTEX_COUNT) + x;
                int bottonRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottonRight;
            }
        }
        return loader.loadModel(vertices, textureCoords, normals, indices, new Material());
    }

    public Vector3f getPosition() {
        return position;
    }

    public RawModel getModel() {
        return model;
    }

    public Material getMaterial() {
        return model.getMaterial();
    }

    public Texture getTexture() {
        return model.getTexture();
    }
}
