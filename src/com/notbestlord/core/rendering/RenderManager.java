package com.notbestlord.core.rendering;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.Camera;
import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.ShaderManager;
import com.notbestlord.core.WindowManager;
import com.notbestlord.core.entity.Entity;
import com.notbestlord.core.entity.Terrain;
import com.notbestlord.core.lighting.DirectionalLight;
import com.notbestlord.core.lighting.PointLight;
import com.notbestlord.core.lighting.SpotLight;
import com.notbestlord.core.particle.Particle;
import com.notbestlord.core.utils.Consts;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderManager {
    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private ParticleRenderer particleRenderer;

    public RenderManager(){
        window = ClientLauncher.getWindow();
    }

    public void init(ObjectLoader loader) throws Exception{
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();
        particleRenderer = new ParticleRenderer(loader);
        entityRenderer.init();
        terrainRenderer.init();
        particleRenderer.init();
    }

    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, ShaderManager shader){
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);
        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i=0;i<numLights;i++){
            shader.setUniform("spotLights", spotLights[i], i);
        }
        numLights = pointLights != null ? pointLights.length : 0;
        for(int i=0;i<numLights;i++){
            shader.setUniform("pointLights", pointLights[i], i);
        }
        shader.setUniform("directionalLight", directionalLight);
    }

    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights){
        clear();
        entityRenderer.render(camera, pointLights, spotLights, directionalLight);
        terrainRenderer.render(camera, pointLights, spotLights, directionalLight);
        particleRenderer.render(camera,pointLights,spotLights,directionalLight);
    }

    public void processEntities(Entity entity){
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
        else {
            List<Entity> newEntityList = new ArrayList<>(Arrays.asList(entity));
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }
    public void processTerrain(Terrain terrain){
        terrainRenderer.getTerrains().add(terrain);
    }

    public void generateParticle(Particle particle){
        particleRenderer.addParticle(particle);
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
        particleRenderer.cleanup();
    }
}
