package com.notbestlord.core.rpg;

import com.notbestlord.core.rpg.abilites.*;
import com.notbestlord.core.rpg.quests.QuestHandler;
import com.notbestlord.core.rpg.skills.SkillHandler;
import com.notbestlord.core.rpg.skills.SkillRecord;
import com.notbestlord.core.rpg.stats.StatHandler;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.List;

public class PlayerRPGHandler {
	public final StatHandler statHandler;
	public final SkillHandler skillHandler;
	public final QuestHandler questHandler;
	public final AbilityHandler abilityHandler;
	private ServerPlayer owner;

	public PlayerRPGHandler(ServerPlayer owner) {
		this.owner = owner;
		statHandler = new StatHandler(owner.getUuid());
		skillHandler = new SkillHandler(owner);
		questHandler = new QuestHandler();
		questHandler.init(owner);
		abilityHandler = new AbilityHandler(owner);
	}
	public PlayerRPGHandler(StatHandler statHandler, SkillHandler skills, QuestHandler questHandler, AbilityHandler abilities) {
		this.statHandler = statHandler;
		this.skillHandler = skills;
		// change to save
		this.questHandler = questHandler;
		abilityHandler = abilities;
	}

	public void init(ServerPlayer owner){
		this.owner = owner;
		statHandler.init(owner);
		skillHandler.init(owner);
		abilityHandler.init(owner);
	}

	public void initQuestHandler(){
		questHandler.init(owner);
	}

	public void frameUpdate(){
		statHandler.frameUpdate();
		abilityHandler.frameUpdate();
		questHandler.tickUpdate();
	}
}
