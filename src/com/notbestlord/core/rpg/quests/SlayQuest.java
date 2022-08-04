package com.notbestlord.core.rpg.quests;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.entity.EntityType;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.packet.player.rpg.QuestProgressUpdatePacket;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SlayQuest implements IQuest{
	private QuestID questID;
	private String rawDescription;

	private final Map<EntityType, Integer> slayEntities;
	private Map<EntityType, Integer> slayedEntities;

	@Expose(serialize = false)
	private ServerPlayer questOwner;

	@Expose(serialize = false)
	private Consumer<ServerPlayer> rewardEvent;

	private Quest followUpQuest = Quest.none;

	public SlayQuest(QuestID questID, String rawDescription, Map<EntityType, Integer> slayEntities, Consumer<ServerPlayer> rewardEvent) {
		this.questID = questID;
		this.rawDescription = rawDescription;
		this.slayEntities = slayEntities;
		this.rewardEvent = rewardEvent;
	}

	public SlayQuest(QuestID questID, String rawDescription, Map<EntityType, Integer> slayEntities, Consumer<ServerPlayer> rewardEvent, Quest followUpQuest) {
		this.questID = questID;
		this.rawDescription = rawDescription;
		this.slayEntities = slayEntities;
		this.rewardEvent = rewardEvent;
		this.followUpQuest = followUpQuest;
	}

	public SlayQuest(QuestID questID, String rawDescription, Consumer<ServerPlayer> rewardEvent) {
		this.questID = questID;
		this.rawDescription = rawDescription;
		this.slayEntities = new HashMap<>();
		this.rewardEvent = rewardEvent;
	}

	public SlayQuest(SlayQuest quest, ServerPlayer questOwner) {
		this.questID = quest.questID;
		this.rawDescription = quest.rawDescription;
		this.slayEntities = quest.slayEntities;
		this.rewardEvent = quest.rewardEvent;
		this.questOwner = questOwner;
		this.followUpQuest = quest.followUpQuest;
		this.slayedEntities = new HashMap<>();
		slayEntities.keySet().forEach(entityType -> slayedEntities.put(entityType, 0));
	}


	public void init(ServerPlayer questOwner, Quest quest){
		this.questOwner = questOwner;
		this.rewardEvent = ((SlayQuest) quest.getQuest()).rewardEvent;
		System.out.println(this);
	}

	public void addEntity(EntityType type, int amount){
		slayEntities.put(type, amount);
	}

	public void sendProgress() {
		ServerLauncher.getGameServer().sendPacketToPlayer(questOwner.getUuid(), new QuestProgressUpdatePacket(questID, getDescription(), getCompletePercentage()));
	}

	@Override
	public boolean updateProgress(Object o) {
		if(o instanceof EntityType && slayEntities.containsKey((EntityType) o) && slayedEntities.get((EntityType) o) < slayEntities.get((EntityType) o)){
			slayedEntities.put((EntityType) o, slayedEntities.get((EntityType) o) + 1);
			sendProgress();
			return true;
		}
		return false;
	}

	@Override
	public void completeQuest() {
		if(isQuestComplete() && rewardEvent != null){
			rewardEvent.accept(questOwner);
			questOwner.getRPGHandler().questHandler.addQuest(followUpQuest.getQuest());
		}
	}

	@Override
	public boolean isQuestComplete() {
		for (EntityType entityType : slayedEntities.keySet()) {
			if (slayEntities.get(entityType) < slayedEntities.get(entityType)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public float getCompletePercentage() {
		float percent = 0;
		for (EntityType entityType : slayedEntities.keySet()) {
			if(slayEntities.get(entityType) == 0) continue;
			percent += slayedEntities.get(entityType) / (float) slayEntities.get(entityType);
		}
		return percent / slayEntities.size();
	}

	@Override
	public String getDescription() {
		String out = rawDescription + "\n";

		for (EntityType entityType : slayedEntities.keySet()) {
			out += "[" + Utils.UpperCaseStart(entityType.getDisplayString()) + "] ";
			out += slayedEntities.get(entityType) + "/" + slayEntities.get(entityType) + "\n";
		}
		return out;
	}

	@Override
	public String getRawDescription() {
		String out = rawDescription + "\n";

		for (EntityType entityType : slayEntities.keySet()) {
			out += "[" + Utils.UpperCaseStart(entityType.getDisplayString()) + "] x" + slayEntities.get(entityType) + "\n";
		}
		return out;
	}

	@Override
	public QuestID getQuestID() {
		return questID;
	}

	@Override
	public String toString() {
		return slayEntities.toString() + "," + slayedEntities.toString();
	}
}
