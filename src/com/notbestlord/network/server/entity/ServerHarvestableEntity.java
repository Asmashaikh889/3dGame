package com.notbestlord.network.server.entity;

import com.notbestlord.ServerLauncher;
import com.notbestlord.core.inventory.ItemRegistry;
import com.notbestlord.core.physics.BoundingBox3f;
import com.notbestlord.core.physics.Collider;
import com.notbestlord.core.rpg.RPGUtils;
import com.notbestlord.network.data.MouseButton;
import com.notbestlord.network.packet.client.SoundPacket;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerHarvestableEntity extends ServerInteractEntity{
	private final Map<String, Integer> playerClicks = new HashMap<>();
	private final String requirements;
	private int totalUses;
	private final int requiredClicks;
	private final Collider collider;
	private final ItemRegistry result;
	private final String clickSound, reqSound;

	public ServerHarvestableEntity(Vector3f position, float scale, String textureId, int requiredClicks, int totalUses, String requirements, ItemRegistry result, String clickSound, String reqSound) {
		super(UUID.randomUUID().toString(), position, new Vector3f(), scale,"cube", textureId);
		collider = new Collider(new BoundingBox3f(new Vector3f(-scale,-scale,-scale), new Vector3f(scale, scale, scale)),1,1,1);
		this.requiredClicks = requiredClicks;
		this.totalUses = totalUses;
		this.requirements = requirements;
		this.result = result;
		this.clickSound = clickSound;
		this.reqSound = reqSound;
	}

	public ServerHarvestableEntity(Vector3f position, float scale, String textureId, int requiredClicks, String requirements, ItemRegistry result, String clickSound, String reqSound) {
		super(UUID.randomUUID().toString(), position, new Vector3f(), scale,"cube", textureId);
		collider = new Collider(new BoundingBox3f(new Vector3f(-scale,-scale,-scale), new Vector3f(scale, scale, scale)),1,1,1);
		this.result = result;
		this.requiredClicks = requiredClicks;
		this.clickSound = clickSound;
		this.reqSound = reqSound;
		this.totalUses = -1;
		this.requirements = requirements;
	}

	@Override
	public void Interact(ServerPlayer player, MouseButton mouseButton) {
		if(mouseButton == MouseButton.left && RPGUtils.doesMeetRequirements(player, requirements)){
			if(!playerClicks.containsKey(player.getUuid())){
				playerClicks.put(player.getUuid(), 0);
			}
			playerClicks.put(player.getUuid(), playerClicks.get(player.getUuid()) + 1);
			if(playerClicks.get(player.getUuid()) == requiredClicks){
				playerClicks.put(player.getUuid(), 0);
				player.getInventory().addItem(result.getAsItem());
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new SoundPacket(reqSound, getPosition()));
				if(totalUses > 0){
					totalUses--;
				}
			}
			else {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new SoundPacket(clickSound, getPosition()));
			}
		}
	}

	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public boolean isDead() {
		return totalUses == 0;
	}
}
