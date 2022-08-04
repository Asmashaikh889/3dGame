package com.notbestlord.network.server.entity;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.PhysicsManager;
import com.notbestlord.core.entity.EntityType;
import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.rpg.PlayerRPGHandler;
import com.notbestlord.core.rpg.skills.SkillHandler;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.rpg.stats.StatHandler;
import com.notbestlord.core.rpg.stats.StatusEffect;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.core.utils.EntityFlags;
import com.notbestlord.network.data.MouseButton;
import com.notbestlord.network.packet.player.LogMessagePacket;
import com.notbestlord.network.server.player.ServerPlayerInventory;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ServerPlayer extends ServerEntity implements IRPGEntity {
	private Vector3f cameraRotation;

	private Collider collider;

	private PlayerRPGHandler RPGHandler;

	private ServerPlayerInventory inventory;

	private EntityFlags flags;

	public final List<String> clientInputs = new ArrayList<>();

	public ServerPlayer(String UUID){
		super(UUID, new Vector3f(2,5,2), new Vector3f(0), 1f, "cube", "blue");
		cameraRotation = new Vector3f(0);

		collider = new Collider(new BoundingBox3f(new Vector3f(-1.0375f,-1.0375f,-1.0375f), new Vector3f(1, 1, 1)),1,1,1);

		RPGHandler = new PlayerRPGHandler(this);
		StatHandler stats = RPGHandler.statHandler;
		stats.setStat(Stat.dexterity, 2.5f);
		stats.setStat(Stat.stamina_max, 10);
		stats.setStat(Stat.inventory_capacity, 8);
		stats.setStat(Stat.mana_max, 10);
		stats.setStat(Stat.strength, 5);

		inventory = new ServerPlayerInventory(this,8);

		flags = new EntityFlags();

		flags.initCooldown("Ability1Cooldown");
		flags.initCooldown("Ability2Cooldown");
		flags.initCooldown("Ability3Cooldown");
		flags.initCooldown("Ability4Cooldown");
		flags.initCooldown("Ability5Cooldown");
		flags.initFlag("manaPoisoningState");
		flags.initFlag("CanJump");
	}

	public ServerPlayer(String uuid, Vector3f position, PlayerRPGHandler rpgHandler, ServerPlayerInventory inventory, EntityFlags flags) {
		super(uuid, position, new Vector3f(0), 1f, "cube", "blue");

		cameraRotation = new Vector3f(0);

		collider = new Collider(new BoundingBox3f(new Vector3f(-1.0375f,-1.0375f,-1.0375f), new Vector3f(1, 1, 1)),1,1,1);

		this.RPGHandler = rpgHandler;
		rpgHandler.init(this);

		this.inventory = inventory;
		inventory.init(this);

		this.flags = flags;

		rpgHandler.initQuestHandler();
	}

	public void jump(){
		if(flags.getFlag("CanJump") == 0){
			getVelocity().y = Consts.GRAVITY_ACCEL * 40;
			flags.setFlag("CanJump", 1);
		}
	}

	public void worldInteract(MouseButton mouseButton, List<ServerEntity> entities){
		for(ServerEntity entity : entities){
			if(entity instanceof ServerInteractEntity && ((ServerInteractEntity) entity).getCollider() != null &&
			PhysicsManager.lineBoxOverlap(
					new Vector3f(getPosition()).add(0,Consts.PLAYER_EYE_HEIGHT,0),
					calcLookVector(3), ((ServerInteractEntity) entity).getCollider().getBoundingBox(),
					entity.getPosition())){
				((ServerInteractEntity) entity).Interact(this, mouseButton);
			}
		}
	}

	public void addToPhysics(PhysicsManager physicsManager){
		physicsManager.addEntity(getUuid(),collider,getPosition());
	}

	public void removeFromPhysics(PhysicsManager physicsManager){
		physicsManager.removeEntity(getUuid());
	}

	@Override
	public float getStat(Stat stat) {
		return RPGHandler.statHandler.getStat(stat);
	}

	@Override
	public void setStat(Stat stat, float n) {
		RPGHandler.statHandler.setStat(stat,n);
	}

	@Override
	public void addStatusEffect(StatusEffect statusEffect) {
		RPGHandler.statHandler.addStatusEffect(statusEffect);

	}

	@Override
	public void removeStatusEffect(StatusEffectType effectType) {
		RPGHandler.statHandler.removeStatusEffect(effectType);
	}

	@Override
	public List<StatusEffect> getStatusEffects() {
		return RPGHandler.statHandler.getStatusEffects();
	}

	public Vector3f getCameraRotation() {
		return cameraRotation;
	}

	public PlayerRPGHandler getRPGHandler() {
		return RPGHandler;
	}

	public ServerPlayerInventory getInventory() {
		return inventory;
	}

	public Collider getCollider() {
		return collider;
	}

	public EntityFlags getFlags() {
		return flags;
	}

	public StatHandler getStatHandler(){
		return RPGHandler.statHandler;
	}

	public SkillHandler getSkillHandler(){ return RPGHandler.skillHandler;}

	private Vector3f calculatePlayerPosition(Vector3f pos, float movementSpeed, PhysicsManager physicsManager){
		Vector3f position = new Vector3f(pos);
		Vector3f rotation = cameraRotation;
		float x = getVelocity().x * movementSpeed, y = getVelocity().y, z = getVelocity().z * movementSpeed;
		if(z != 0){
			position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
			position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
		}
		if(x != 0){
			position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
			position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
		}
		position.y += y;
		position = physicsManager.calcPostCollisionPlayerPos(this, position, 0.1f);

		if(physicsManager.doesUpwardsCollisionOccur((new Vector3f(position)).add(0,getVelocity().y,0), collider, 0.1f)){
			getVelocity().y = 0;
			flags.setFlag("CanJump", 0);
		}
		return position;
	}

	public void resetVelocity(){
		// reset horizontal velocity
		this.getVelocity().x = 0;
		this.getVelocity().z = 0;
		// activate inputs
		List<String> lst = new ArrayList<>(clientInputs);
		for(String input : lst){
			switch (input){
				case "forward" -> getVelocity().z = -1;
				case "backward" -> getVelocity().z = 1;
				case "left" -> getVelocity().x = -1;
				case "right" -> getVelocity().x = 1;
				case "jump" -> jump();
			}
		}
		// apply gravity
		getVelocity().y -= Consts.GRAVITY_ACCEL * Consts.deltaTime;
		if(getVelocity().y < -Consts.TERMINAL_VELOCITY_Y){
			getVelocity().y = -Consts.TERMINAL_VELOCITY_Y;
		}
	}
	public Vector3f calcLookVector(float mul){
		Vector3f position = new Vector3f(0,0,0);
		Vector3f rotation = cameraRotation;

		float d2size = (float) Math.cos(Math.toRadians(rotation.x));

		position.y += (float) Math.sin(Math.toRadians(rotation.x)) * -1f;
		position.x += (float) Math.sin(Math.toRadians(rotation.y)) * d2size;
		position.z += (float) Math.cos(Math.toRadians(rotation.y)) * -d2size;
		position.mul(mul);
		return position;
	}

	public void movePosition(PhysicsManager physicsManager){
		getPosition().set(calculatePlayerPosition(getPosition(),
				Consts.PLAYER_MOVE_SPEED * (1 + (getStatHandler().getStat(Stat.speed) / 100)) * Consts.deltaTime, physicsManager));
	}

	@Override
	public ServerEntity asEntity(){
		return new ServerEntity(getUuid(),getPosition(),getRotation(),getScale(),getModelID(),getTextureID());
	}

	@Override
	public void frameUpdate(){
		// update rpgHandler
		RPGHandler.frameUpdate();

		// mana poisoning effect handling
		if(getStatHandler().getStat(Stat.health_max) <= getStatHandler().getStat(Stat.mana_max) * 2.5f && flags.getFlag("manaPoisoningState") == 0){
			addStatusEffect(new StatusEffect(StatusEffectType.mana_poisoning, -1));
			flags.setFlag("manaPoisoningState", 1);
		}
		else if(getStatHandler().getStat(Stat.health_max) > getStatHandler().getStat(Stat.mana_max) * 2.5f && flags.getFlag("manaPoisoningState") == 1){
			removeStatusEffect(StatusEffectType.mana_poisoning);
			flags.setFlag("manaPoisoningState", 0);
		}
	}

	public void sendLogMessage(String msg){
		ServerLauncher.getGameServer().sendPacketToPlayer(getUuid(), new LogMessagePacket(msg));
	}

	@Override
	public EntityType getEntityType(){
		return EntityType.player;
	}

}
