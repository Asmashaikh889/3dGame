package com.notbestlord.core.rendering;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.Camera;
import com.notbestlord.core.ShaderManager;
import com.notbestlord.core.entity.Entity;
import com.notbestlord.core.entity.RawModel;
import com.notbestlord.core.lighting.DirectionalLight;
import com.notbestlord.core.lighting.PointLight;
import com.notbestlord.core.lighting.SpotLight;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.core.utils.Transformation;
import com.notbestlord.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements IRenderer<Entity>{
    ShaderManager shader;
    private Map<RawModel, List<Entity>> entities;

    public EntityRenderer() throws Exception{
        entities = new HashMap<>();
        shader = new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/entity_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createPointLightListUniform("pointLights", Consts.MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights", Consts.MAX_SPOT_LIGHTS);
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        shader.bind();
        shader.setUniform("projectionMatrix", ClientLauncher.getWindow().updateProjectionMatrix());
        RenderManager.renderLights(pointLights,spotLights,directionalLight, shader);
        for(RawModel model : entities.keySet()){
            bind(model);
            List<Entity> entityList = entities.get(model);
            for(Entity entity : entityList){
                prepare(entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(),GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        entities.clear();
        shader.unbind();
    }

    @Override
    public void bind(RawModel model) {
        GL31.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shader.setUniform("material", model.getMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL31.glBindVertexArray(0);
    }

    @Override
    public void prepare(Entity entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

    public Map<RawModel, List<Entity>> getEntities() {
        return entities;
    }
}
