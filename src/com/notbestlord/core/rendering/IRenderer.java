package com.notbestlord.core.rendering;

import com.notbestlord.core.Camera;
import com.notbestlord.core.entity.RawModel;
import com.notbestlord.core.lighting.DirectionalLight;
import com.notbestlord.core.lighting.PointLight;
import com.notbestlord.core.lighting.SpotLight;

public interface IRenderer<T> {

    public void init() throws Exception;

    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

    abstract void bind(RawModel model);

    public void unbind();

    public void prepare(T t, Camera camera);

    public void cleanup();
}
