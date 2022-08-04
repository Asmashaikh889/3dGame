package com.notbestlord.core.rpg.abilites;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.rpg.IRPGEntity;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.packet.player.rpg.ActiveAbilityActivatePacket;
import com.notbestlord.network.packet.player.rpg.ActiveAbilityCooldownEndPacket;
import com.notbestlord.network.packet.player.rpg.UpdateActiveAbilityStatePacket;
import com.notbestlord.network.packet.player.rpg.UpdatePassiveAbilityStatePacket;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.*;

public class AbilityHandler {
	private final Map<String, Float> invokedActiveAbilities;
	private final Map<String, Float> activeAbilityCooldowns;
	private final List<String> activeAbilities;
	private final List<String> passiveAbilities;
	private int maxUsedAbilities = 5, usedAbilities = 0;
	@Expose(serialize = false)
	private IRPGEntity owner;

	public AbilityHandler(IRPGEntity owner){
		this.owner = owner;
		activeAbilities = new ArrayList<>();
		invokedActiveAbilities = new HashMap<>();
		activeAbilityCooldowns = new HashMap<>();
		passiveAbilities = new ArrayList<>();
	}

	public void init(IRPGEntity owner){
		this.owner = owner;
	}

	public void frameUpdate(){
		List<String> activeAbilityActiveSet = new ArrayList<>(invokedActiveAbilities.keySet());
		for(String active : activeAbilityActiveSet){
			invokedActiveAbilities.put(active, invokedActiveAbilities.get(active) - Consts.trueDeltaTime);
			if(invokedActiveAbilities.get(active) <= 0){
				AbilityManager.getActiveAbility(active).end(owner);
				invokedActiveAbilities.remove(active);
				activeAbilityCooldowns.put(active, AbilityManager.getActiveAbility(active).cooldown());
			}
		}
		List<String> activeAbilityCooldownSet = new ArrayList<>(activeAbilityCooldowns.keySet());
		for (String active : activeAbilityCooldownSet){
			activeAbilityCooldowns.put(active, activeAbilityCooldowns.get(active) - Consts.trueDeltaTime);
			if(activeAbilityCooldowns.get(active) <= 0){
				activeAbilityCooldowns.remove(active);
				usedAbilities--;
				if(owner instanceof ServerPlayer player) {
					ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new ActiveAbilityCooldownEndPacket(active));
				}
			}
		}
	}

	public void addPassiveAbility(String name) {
		if(!hasPassiveAbility(name)) {
			PassiveAbility passive = AbilityManager.getPassiveAbility(name);
			passive.start(owner);
			passiveAbilities.add(name);
			if (owner instanceof ServerPlayer player) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new UpdatePassiveAbilityStatePacket(passive, false));
			}
		}
	}

	public void removePassiveAbility(String name) {
		if(passiveAbilities.contains(name)) {
			AbilityManager.getPassiveAbility(name).end(owner);
			passiveAbilities.remove(name);
			if(owner instanceof ServerPlayer player) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new UpdatePassiveAbilityStatePacket(AbilityManager.getPassiveAbility(name), true));
			}
		}
	}

	public void playerLogin(){
		if(owner instanceof ServerPlayer player) {
			for(String active : activeAbilities) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new UpdateActiveAbilityStatePacket(AbilityManager.getActiveAbility(active), false));
			}
			for(String passive : passiveAbilities) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new UpdatePassiveAbilityStatePacket(AbilityManager.getPassiveAbility(passive), false));
			}
		}
	}

	public void addActiveAbility(String name) {
		if(!hasActiveAbility(name)) {
			ActiveAbility active = AbilityManager.getActiveAbility(name);
			activeAbilities.add(name);
			if(owner instanceof ServerPlayer player) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new UpdateActiveAbilityStatePacket(active, false));
			}
		}

	}

	public void removeActiveAbility(String name) {
		if(activeAbilities.contains(name)) {
			activeAbilities.remove(name);
			AbilityManager.getActiveAbility(name).end(owner);
			if(owner instanceof ServerPlayer player) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new UpdateActiveAbilityStatePacket(AbilityManager.getActiveAbility(name), true));
			}
		}
	}

	public void invokeActiveAbility(String active){
		if(maxUsedAbilities <= usedAbilities || invokedActiveAbilities.containsKey(active) || activeAbilityCooldowns.containsKey(active) ||
				!activeAbilities.contains(active) || AbilityManager.getActiveAbility(active) == null) {
			return;
		}
		if (AbilityManager.getActiveAbility(active).start(owner)) {
			invokedActiveAbilities.put(active, AbilityManager.getActiveAbility(active).getDuration());
			usedAbilities++;
			if(owner instanceof ServerPlayer player) {
				ServerLauncher.getGameServer().sendPacketToPlayer(player.getUuid(), new ActiveAbilityActivatePacket(player.getUuid(), active));
			}
		}
	}

	public boolean hasActiveAbility(String name){
		return name != null && activeAbilities.contains(name) && AbilityManager.getActiveAbility(name) != null;
	}

	public boolean hasPassiveAbility(String name){
		return name != null && passiveAbilities.contains(name) && AbilityManager.getPassiveAbility(name) != null;
	}

}
