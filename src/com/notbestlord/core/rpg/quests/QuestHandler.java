package com.notbestlord.core.rpg.quests;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.network.packet.player.rpg.QuestCompletePacket;
import com.notbestlord.network.packet.player.rpg.QuestRequestAcceptPacket;
import com.notbestlord.network.packet.player.rpg.QuestStartPacket;
import com.notbestlord.network.server.entity.ServerEntity;
import com.notbestlord.network.server.entity.ServerNPCEntity;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class QuestHandler {
	//
	@Expose(serialize = false)
	private ServerPlayer owner;
	//
	private List<ConverseQuest> converseQuests = new ArrayList<>();
	private List<SlayQuest> slayQuests = new ArrayList<>();
	private List<FetchQuest> fetchQuests = new ArrayList<>();
	//
	@Expose(serialize = false)
	private IQuest currentQuest;

	public void init(ServerPlayer player){
		this.owner = player;
		for(ConverseQuest quest : converseQuests){
			quest.init(player, Quest.valueOf(quest.getQuestID().name()));
			quest.sendProgress();
		}
		for(SlayQuest quest : slayQuests){
			quest.init(player, Quest.valueOf(quest.getQuestID().name()));
			quest.sendProgress();
		}
		for(FetchQuest quest : fetchQuests){
			quest.init(player, Quest.valueOf(quest.getQuestID().name()));
			quest.sendProgress();
		}
	}

	public void setCurrentAcceptingQuest(IQuest quest){
		if(quest == null || currentQuest == quest ||
				(quest instanceof ConverseQuest && converseQuests.contains(quest)) ||
				(quest instanceof SlayQuest && slayQuests.contains(quest)) ||
				(quest instanceof FetchQuest && fetchQuests.contains(quest))) {
			return;
		}
		currentQuest = quest;
		ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestRequestAcceptPacket(quest.getQuestID(), quest.getRawDescription()));
	}

	public void tickUpdate(){
		for(int i = 0; i < fetchQuests.size(); i++){
			fetchQuests.get(i).updateProgress(null);
		}
	}

	public void acceptQuest(boolean accept){
		if(!accept){
			currentQuest = null;
			return;
		}
		if(currentQuest == null) return;
		IQuest quest = currentQuest;
		if(quest instanceof ConverseQuest){
			converseQuests.add(new ConverseQuest((ConverseQuest) quest, owner));
		}
		else if(quest instanceof SlayQuest){
			slayQuests.add(new SlayQuest((SlayQuest) quest, owner));
		}
		else if(quest instanceof FetchQuest){
			fetchQuests.add(new FetchQuest((FetchQuest) quest, owner));
		}
		else{
			owner.sendLogMessage("Failed to accept quest.");
			currentQuest = null;
			return;
		}
		owner.sendLogMessage("Quest Accepted.");
		ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestStartPacket(quest.getQuestID(), quest.getRawDescription(), 0));
		currentQuest = null;
	}
	public void addQuest(IQuest quest){
		if(quest == null) return;
		if(quest instanceof ConverseQuest){
			ConverseQuest quest1 = new ConverseQuest((ConverseQuest) quest, owner);
			converseQuests.add(quest1);
			ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestStartPacket(quest1.getQuestID(), quest1.getRawDescription(), quest1.getCompletePercentage()));
		}
		else if(quest instanceof SlayQuest){
			SlayQuest quest1 = new SlayQuest((SlayQuest) quest, owner);
			slayQuests.add(quest1);
			ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestStartPacket(quest1.getQuestID(), quest1.getRawDescription(), quest1.getCompletePercentage()));
		}
		else if(quest instanceof FetchQuest){
			FetchQuest quest1 = new FetchQuest((FetchQuest) quest, owner);
			fetchQuests.add(quest1);
			ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestStartPacket(quest1.getQuestID(), quest1.getRawDescription(), quest1.getCompletePercentage()));
		}
	}

	public boolean updateConverseQuests(ServerNPCEntity npc){
		for (int i = 0, converseQuestsSize = converseQuests.size(); i < converseQuestsSize; i++) {
			ConverseQuest quest = converseQuests.get(i);
			if(quest.updateProgress(npc)){
				return true;
			}
		}
		return false;
	}

	public boolean updateSlayQuests(ServerEntity entity){
		boolean out = false;
		for (int i = 0; i < slayQuests.size(); i++) {
			SlayQuest quest = slayQuests.get(i);
			if(quest.updateProgress(entity.getEntityType())) {
				out = true;
			}
		}
		return out;
	}

	public void completeQuest(QuestID questID){
		for (int i = 0; i < converseQuests.size(); i++) {
			ConverseQuest quest = converseQuests.get(i);
			if(quest.getQuestID() == questID && quest.isQuestComplete()){
				quest.completeQuest();
				converseQuests.remove(quest);
				ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestCompletePacket(questID));
				return;
			}
		}
		for (int i = 0; i < slayQuests.size(); i++) {
			SlayQuest quest = slayQuests.get(i);
			if(quest.getQuestID() == questID && quest.isQuestComplete()){
				quest.completeQuest();
				slayQuests.remove(quest);
				ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestCompletePacket(questID));
				return;
			}
		}
		for (int i = 0; i < fetchQuests.size(); i++) {
			FetchQuest quest = fetchQuests.get(i);
			if(quest.getQuestID() == questID && quest.isQuestComplete()){
				quest.completeQuest();
				fetchQuests.remove(quest);
				ServerLauncher.getGameServer().sendPacketToPlayer(owner.getUuid(), new QuestCompletePacket(questID));
				return;
			}
		}
	}

}
