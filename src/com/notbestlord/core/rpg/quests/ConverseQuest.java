package com.notbestlord.core.rpg.quests;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.rpg.npcs.NPC_ID;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.packet.player.rpg.QuestProgressUpdatePacket;
import com.notbestlord.network.server.entity.ServerNPCEntity;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConverseQuest implements IQuest{
	private QuestID questID;
	private String rawDescription;

	private List<NPC_ID> NPCs;
	private List<NPC_ID> ConversedNPCs;

	@Expose(serialize = false)
	private ServerPlayer questOwner;

	@Expose(serialize = false)
	private Consumer<ServerPlayer> rewardEvent;

	private Quest followUpQuest = null;

	public ConverseQuest(QuestID questID, String rawDescription, List<NPC_ID> NPCs, Consumer<ServerPlayer> rewardEvent) {
		this.questID = questID;
		this.rawDescription = rawDescription;
		this.NPCs = NPCs;
		this.rewardEvent = rewardEvent;
	}
	public ConverseQuest(QuestID questID, String rawDescription, List<NPC_ID> NPCs, Consumer<ServerPlayer> rewardEvent, Quest followUpQuest) {
		this.questID = questID;
		this.rawDescription = rawDescription;
		this.NPCs = NPCs;
		this.rewardEvent = rewardEvent;
		this.followUpQuest = followUpQuest;
	}

	public ConverseQuest(QuestID questID, String rawDescription, Consumer<ServerPlayer> rewardEvent) {
		this.questID = questID;
		this.rawDescription = rawDescription;
		this.NPCs = new ArrayList<>();
		this.rewardEvent = rewardEvent;
	}

	public void init(ServerPlayer questOwner, Quest quest){
		this.questOwner = questOwner;
		this.rewardEvent = ((ConverseQuest) quest.getQuest()).rewardEvent;
	}

	public void addNPC(NPC_ID id){
		NPCs.add(id);
	}

	public ConverseQuest(ConverseQuest quest, ServerPlayer questOwner){
		this.questID = quest.questID;
		this.rawDescription = quest.rawDescription;;
		this.NPCs = quest.NPCs;
		this.rewardEvent = quest.rewardEvent;
		this.questOwner = questOwner;
		this.ConversedNPCs = new ArrayList<>();
		this.followUpQuest = quest.followUpQuest;
	}

	public void sendProgress(){
		ServerLauncher.getGameServer().sendPacketToPlayer(questOwner.getUuid(), new QuestProgressUpdatePacket(questID, getDescription(), getCompletePercentage()));
	}

	@Override
	public boolean updateProgress(Object o) {
		if(o instanceof ServerNPCEntity){
			if(NPCs.contains(((ServerNPCEntity) o).getNpc_id()) && !ConversedNPCs.contains(((ServerNPCEntity) o).getNpc_id())) {
				ConversedNPCs.add(((ServerNPCEntity) o).getNpc_id());
				((ServerNPCEntity) o).InteractWContext(questOwner, questID.name());
				sendProgress();
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}

	@Override
	public void completeQuest() {
		if(isQuestComplete() && rewardEvent != null) {
			rewardEvent.accept(questOwner);
			questOwner.getRPGHandler().questHandler.addQuest(followUpQuest.getQuest());
		}
	}

	@Override
	public boolean isQuestComplete() {
		for(NPC_ID id : NPCs){
			if(!ConversedNPCs.contains(id)){
				return false;
			}
		}
		return true;
	}

	@Override
	public float getCompletePercentage() {
		float percent = 0;
		for(NPC_ID id : NPCs){
			percent += ConversedNPCs.contains(id) ? 1 : 0;
		}
		return percent / NPCs.size();
	}

	@Override
	public String getDescription() {
		String out = rawDescription + "\n";
		for(NPC_ID id : NPCs){
			out += Utils.UpperCaseStart(id.name()) + " ";
			out += (ConversedNPCs.contains(id) ? "V" : "X") + "\n";
		}
		return out;
	}

	@Override
	public String getRawDescription() {
		String out = rawDescription + "\n";
		for(NPC_ID id : NPCs){
			out += " " + Utils.UpperCaseStart(id.name()) + "\n";
		}
		return out;
	}

	@Override
	public QuestID getQuestID() {
		return questID;
	}
}
