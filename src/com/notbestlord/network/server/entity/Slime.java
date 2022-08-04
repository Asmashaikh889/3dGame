package com.notbestlord.network.server.entity;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.entity.EntityType;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.loot.EntityLootTable;
import com.notbestlord.core.loot.ItemLoot;
import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.rpg.stats.Stat;
import com.notbestlord.core.rpg.stats.StatHandler;
import com.notbestlord.core.rpg.stats.StatusEffect;
import com.notbestlord.core.rpg.stats.StatusEffectType;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.core.utils.Range;
import com.notbestlord.network.data.MouseButton;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Slime extends ServerInteractEntity implements IRPGEntity {
	private Collider collider;

	private StatHandler statHandler;

	private EntityLootTable lootTable;
	private boolean lootApplied = false;
	private ServerPlayer lastAttacker;

	public Slime(Vector3f position) {
		super(UUID.randomUUID().toString(), position, new Vector3f(), 0.2f, "cube", "yellow");
		statHandler = new StatHandler();
		statHandler.setStat(Stat.dexterity, 5f);
		statHandler.setStat(Stat.stamina_max, 10);
		statHandler.setStat(Stat.mana_max, 10);
		statHandler.setStat(Stat.strength, 10);
		collider = new Collider(new BoundingBox3f(new Vector3f(-0.2075f,-0.2075f,-0.2075f), new Vector3f(0.2f, 0.2f, 0.2f)),1,1,1);
		lootTable = new EntityLootTable(new Range<>(50f,75f), new Range<>(50,100),
				new ArrayList<>(List.of(new ItemLoot(1, 5, new ItemRegistry("shard_yellow_0")))));
	}


	@Override
	public void frameUpdate() {
		super.frameUpdate();
		statHandler.frameUpdate();
		//
		ServerLauncher.getGameServer().getPhysics().setPostCollisionPos(this, getPosition(), collider, 0);
		//
		if(statHandler.getStat(Stat.health) <= 0){
			handleDeath();
		}
	}

	public void handleDeath(){
		if(!lootApplied) {
			if(spawner != null){
				spawner.entityDeath();
			}
			if(getPosition().y > -1) {
				lastAttacker.getRPGHandler().questHandler.updateSlayQuests(this);
				lootTable.applyPlayerLoot(lastAttacker.getInventory(), lastAttacker.getStat(Stat.luck));
			}
			lootApplied = true;
		}
	}

	@Override
	public float getStat(Stat stat) {
		return statHandler.hasStat(stat) ? statHandler.getStat(stat) : -1;
	}

	@Override
	public StatHandler getStatHandler() {
		return statHandler;
	}

	@Override
	public void setStat(Stat stat, float n) {
		statHandler.setStat(stat, n);
	}

	@Override
	public void addStatusEffect(StatusEffect statusEffect) {
		statHandler.addStatusEffect(statusEffect);
	}

	@Override
	public void removeStatusEffect(StatusEffectType effectType) {
		statHandler.removeStatusEffect(effectType);
	}

	@Override
	public List<StatusEffect> getStatusEffects() {
		return statHandler.getStatusEffects();
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.monster_slime;
	}

	@Override
	public void Interact(ServerPlayer player, MouseButton mouseButton) {
		if(mouseButton != MouseButton.left) return;
		float damage = statHandler.calcIntakeDamage(
				player.getInventory().getEquipment().getAttackAttribute(),
				player.getInventory().getEquipment().getDamage(),
				player.getStatHandler());
		statHandler.incStat(Stat.health, -damage);
		player.sendLogMessage("Slime: " + getStat(Stat.health) + " hp.");
		lastAttacker = player;
	}

	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public boolean isDead() {
		if(getStat(Stat.health) <= 0f || getPosition().y < -1){
			handleDeath();
			return true;
		}
		return false;
	}

	@Override
	public ServerInteractEntity duplicate() {
		Slime slime = new Slime(new Vector3f());
		return slime;
	}
}
