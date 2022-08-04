package com.notbestlord.core.rendering;

import com.notbestlord.ClientLauncher;
import com.notbestlord.core.Camera;
import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.ShaderManager;
import com.notbestlord.core.entity.RawModel;
import com.notbestlord.core.lighting.DirectionalLight;
import com.notbestlord.core.lighting.PointLight;
import com.notbestlord.core.lighting.SpotLight;
import com.notbestlord.core.particle.Particle;
import com.notbestlord.core.particle.ParticleTexture;
import com.notbestlord.core.utils.Transformation;
import com.notbestlord.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.util.*;

public class ParticleRenderer implements IRenderer<Particle>{

	private Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
	private RawModel quad;
	private ShaderManager shader;

	public ParticleRenderer(ObjectLoader loader) throws Exception {
		quad = loader.loadModel(new float[]{-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f}, 2);
		shader = new ShaderManager();
	}

	@Override
	public void init() throws Exception {
		shader.createVertexShader(Utils.loadResource("/shaders/particle_vertex.vs"));
		shader.createFragmentShader(Utils.loadResource("/shaders/particle_fragment.fs"));
		shader.link();
		shader.createUniform("textureSampler");
		shader.createUniform("projectionMatrix");
		shader.createUniform("viewMatrix");
		shader.createUniform("texOffset1");
		shader.createUniform("texOffset2");
		shader.createUniform("texCoordInfo");
	}

	@Override
	public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
		//
		List<List<Particle>> particleLists = new ArrayList(particles.values());
		for(List<Particle> list : particleLists){
			for(int i = 0; i<list.size();i++){
				if(!list.get(i).update()){
					list.remove(i);
					i--;
				}
			}
		}
		//
		shader.bind();
		shader.setUniform("projectionMatrix", ClientLauncher.getWindow().updateProjectionMatrix());
		bind(quad);
		for(ParticleTexture texture : particles.keySet()){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ObjectLoader.getTexture(texture.getTextureId()));
			if(!texture.isStretched()) {
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			}
			for(Particle particle : particles.get(texture)){
				prepare(particle, camera);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP,0, quad.getVertexCount());
			}

		}
		unbind();
		shader.unbind();
	}

	private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix){
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(position);
		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		modelMatrix.rotate((float) Math.toRadians(rotation), new Vector3f(0,0,1));
		modelMatrix.scale(new Vector3f(scale,scale,scale));
		viewMatrix.mul(modelMatrix);
		shader.setUniform("viewMatrix", viewMatrix);
	}

	@Override
	public void bind(RawModel model) {
		GL31.glBindVertexArray(quad.getId());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDepthMask(false);
	}

	@Override
	public void unbind() {
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL31.glBindVertexArray(0);
	}

	@Override
	public void prepare(Particle particle, Camera camera) {
		shader.setUniform("textureSampler", 0);
		shader.setUniform("texOffset1", particle.getTexOffset1());
		shader.setUniform("texOffset2", particle.getTexOffset2());
		shader.setUniform("texCoordInfo", new Vector2f(particle.getTexture().getNumberOfRows(), particle.getBlend()));
		updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(), Transformation.getViewMatrix(camera));
	}

	@Override
	public void cleanup() {
		shader.cleanup();
	}

	public void addParticle(Particle particle){
		if(particles.containsKey(particle.getTexture())){
			particles.get(particle.getTexture()).add(particle);
		}
		else {
			particles.put(particle.getTexture(), new ArrayList<>(Arrays.asList(particle)));
		}
	}
}
