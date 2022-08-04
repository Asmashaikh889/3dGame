package com.notbestlord;

import com.notbestlord.core.*;
import com.notbestlord.core.entity.Entity;
import com.notbestlord.core.entity.RawModel;
import com.notbestlord.core.entity.LivingEntity;
import com.notbestlord.core.entity.Player;
import com.notbestlord.core.entity.Terrain;
import com.notbestlord.core.inventory.ItemManager;
import com.notbestlord.core.inventory.RecipeManager;
import com.notbestlord.core.rpg.skills.Skill;
import com.notbestlord.core.lighting.DirectionalLight;
import com.notbestlord.core.rendering.RenderManager;
import com.notbestlord.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShooterGame implements ILogic {

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;
    private final PhysicsManager physics;

    private List<Entity> staticEntities;
    private List<LivingEntity> livingEntities;
    private List<Terrain> terrains;
    private Player player;

    private DirectionalLight directionalLight;

    public ShooterGame(){
        renderer = new RenderManager();
        window = ClientLauncher.getWindow();
        loader = new ObjectLoader();
        physics = new PhysicsManager();
    }
    @Override
    public void init() throws Exception {
        //
        loader.registerTextures();
        renderer.init(loader);

        staticEntities = new ArrayList<>();
        livingEntities = new ArrayList<>();
        terrains = new ArrayList<>();

        // Init Handling
        ItemManager.init();
        RecipeManager.init();
        Skill.initSkillSystem();
        //

        RawModel modelBlue = loader.loadOBJModel("/models/bunny.obj");
        modelBlue.setTexture(ObjectLoader.texturesByString.get("blue"), 1f);

        Random rand = new Random();
        for(int i = 0; i < 25; i++){
            float x = rand.nextFloat() * 10 - 5;
            float y = rand.nextFloat() * 10 - 5;
            float z = rand.nextFloat() * -20;
            staticEntities.add(new Entity(modelBlue, new Vector3f(x,y,z), new Vector3f(rand.nextFloat()*180,rand.nextFloat()*180,0), 5));
        }

        RawModel projectileModel = loader.loadOBJModel("/models/Sphere.obj");
        projectileModel.setTexture(ObjectLoader.texturesByString.get("green"), 1f);

        RawModel cubeBlueModel = loader.loadOBJModel("/models/cube.obj");
        cubeBlueModel.setTexture(ObjectLoader.texturesByString.get("blue"), 1f);

        directionalLight = new DirectionalLight(new Vector3f(1,1,1), new Vector3f(0,90,0), 0.5f);

        /*player = new Player(new Entity(cubeBlueModel, new Vector3f(1, 2, 5), new Vector3f(0, 0, 0), 0.2f), new Camera(),
                new Projectile.ProjData(400f, 25f, 0.1f, new Entity(projectileModel, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0.2f)));
        player.setGravity(-Consts.GRAVITY_ACCEL);
        livingEntities.add(player);*/

        /*RawModel modelRed = new RawModel(loader.loadOBJModel("/models/Enemy.obj"), ObjectLoader.texturesByString.get("red"));
        livingEntities.add(new Enemy(new Entity(modelRed, new Vector3f(0,2f,-4f), new Vector3f(0,0,0), 1)));
        livingEntities.add(new Enemy(new Entity(modelRed, new Vector3f(0,3f,-5f), new Vector3f(0,0,0), 2)));
        livingEntities.add(new Enemy(new Entity(modelRed, new Vector3f(0,2f,-6f), new Vector3f(0,90,0), 0.5f)));

        RawModel cubeRedModel = loader.loadOBJModel("/models/cube.obj");
        cubeRedModel.setTexture(ObjectLoader.texturesByString.get("red"), 1f);
        Cube cube = new Cube(cubeRedModel, new Vector3f(1.25f,1f,1.25f), new Vector3f(0,0,0), 1, 0.8f,1,1f);
        staticEntities.add(cube);
        physics.add(cube.getCollider(), cube.getPos());

        RawModel mgc_crcl = loader.loadOBJModel("/models/flatRectangle.obj");
        mgc_crcl.setTexture(new Texture(ObjectLoader.getTexture("items_magic_1")));
        staticEntities.add(new Magic_Circle(mgc_crcl, new Vector3f(0,0.5f,0), new Vector3f(0,0,0), 2, new Vector3f(0,1,0)));


        RawModel cubeYelModel = loader.loadOBJModel("/models/cube.obj");
        cubeYelModel.setTexture(ObjectLoader.texturesByString.get("yellow"), 1f);
        livingEntities.add(new Enemy(new Entity(cubeYelModel, new Vector3f(3f,1f,6f), new Vector3f(0,0,0), 1f)));*/

    }

    @Override
    public void input() {
        /*player.getVelocity().set(0,player.getVelocity().y,0);
        Controls playerControls = Config.playerControls;
        if(window.isKeyPressed(playerControls.getKey("forward"))){
            player.getVelocity().z = -1;
        }
        if(window.isKeyPressed(playerControls.getKey("backward"))){
            player.getVelocity().z = 1;

        }

        if(window.isKeyPressed(playerControls.getKey("left"))){
            player.getVelocity().x = -1;
        }
        if(window.isKeyPressed(playerControls.getKey("right"))){
            player.getVelocity().x = 1;
        }

        if(window.isKeyPressed(playerControls.getKey("jump")) && player.getFlags().getFlag("CanJump") == 0){
            player.getVelocity().y = Consts.GRAVITY_ACCEL * 24f * Consts.PLAYER_JUMP_SPEED * Consts.deltaTime;
            player.getFlags().setFlag("CanJump", 1);
        }

        if(window.isGuiKeyPressed(playerControls.getKey("inventory"))){
            player.openInventory();
        }
        if(window.isGuiKeyPressed(playerControls.getKey("pause_menu"))){
            player.openPauseMenu();
        }

        if(window.isKeyPressed(GLFW.GLFW_KEY_K)){
            for(int i = 0; i < livingEntities.size(); i++){
                LivingEntity livingEntity = livingEntities.get(i);
                if(livingEntity instanceof  Projectile){
                    livingEntities.remove(livingEntity);
                }
            }
        }*/
    }

    @Override
    public void update(MouseInput mouseInput) {
        //player.applyGravity();

        if(!ClientLauncher.getWindow().isUiOpen()){
            Vector2f rotVec = mouseInput.getDisplayVec();
            player.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY);
        }

        for(Terrain terrain : terrains){
            renderer.processTerrain(terrain);
        }

        for(Entity entity : staticEntities){
            //if(entity instanceof UpdatableStaticEntity) ((UpdatableStaticEntity) entity).update();
            renderer.processEntities(entity);
        }

        /*if(mouseInput.isLeftButtonPress()){
            player.shootProjectile(livingEntities, Consts.PLAYER_PROJECTILE_SPEED);
        }

        player.movePosition(Consts.PLAYER_MOVE_SPEED, physics);*/


        for(int i = 0; i < livingEntities.size(); i++){
            LivingEntity livingEntity = livingEntities.get(i);
            if(!livingEntity.isRendered(physics)){
                livingEntities.remove(livingEntity);
            }
            else{
                renderer.processEntities(livingEntity.getEntityHandler());
            }
        }

    }

    @Override
    public void render() {
        if(window.isResize()){
            GL11.glViewport(0,0,window.getWidth(),window.getHeight());
            window.setResize(true);
        }
        renderer.render(player.getCamera(), directionalLight, null, null);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
