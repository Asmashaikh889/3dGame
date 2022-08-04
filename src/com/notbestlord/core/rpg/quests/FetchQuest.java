package com.notbestlord.core.rpg.quests;

import com.google.gson.annotations.Expose;
import com.notbestlord.ServerLauncher;
import com.notbestlord.core.inventory.Item;
import com.notbestlord.network.packet.player.rpg.QuestProgressUpdatePacket;
import com.notbestlord.network.server.entity.ServerPlayer;

import java.util.List;
import java.util.function.Consumer;

public class FetchQuest implements IQuest{
	private QuestID questID;
	private String rawDescription;
	private List<Item> items;

	@Expose(serialize = false)
	private ServerPlayer questOwner;

	@Expose(serialize = false)
	private Consumer<ServerPlayer> rewardEvent;

	private Quest followUpQuest = Quest.none;

	private float lastPercent = 0;

	public FetchQuest(QuestID questID, List<Item> items, String rawDescription, Consumer<ServerPlayer> rewardEvent) {
		this.questID = questID;
		this.items = items;
		this.rawDescription = rawDescription;
		this.rewardEvent = rewardEvent;
	}

	public FetchQuest(QuestID questID, List<Item> items, String rawDescription, Consumer<ServerPlayer> rewardEvent, Quest followUpQuest) {
		this.questID = questID;
		this.items = items;
		this.rawDescription = rawDescription;
		this.rewardEvent = rewardEvent;
		this.followUpQuest = followUpQuest;
	}

	public FetchQuest(FetchQuest quest, ServerPlayer player){
		this.questID = quest.questID;
		this.items = quest.items;
		this.rawDescription = quest.rawDescription;
		this.rewardEvent = quest.rewardEvent;;
		this.questOwner = player;
		this.followUpQuest = quest.followUpQuest;
	}


	public void init(ServerPlayer questOwner, Quest quest){
		this.questOwner = questOwner;
		this.rewardEvent = ((FetchQuest) quest.getQuest()).rewardEvent;
	}

	public void sendProgress(){
		ServerLauncher.getGameServer().sendPacketToPlayer(questOwner.getUuid(), new QuestProgressUpdatePacket(questID, getDescription(), getCompletePercentage()));
	}

	@Override
	public boolean updateProgress(Object o) {
		if(lastPercent != getCompletePercentage()){
			sendProgress();
		}
		lastPercent = getCompletePercentage();
		return false;
	}

	@Override
	public void completeQuest() {
		if(!isQuestComplete() || rewardEvent == null) return;
		for (Item item : items) {
			questOwner.getInventory().removeItem(item.getType(), item.getAmount());
		}
		rewardEvent.accept(questOwner);
		questOwner.getRPGHandler().questHandler.addQuest(followUpQuest.getQuest());
	}

	@Override
	public boolean isQuestComplete() {
		for (Item item : items) {
			if(!questOwner.getInventory().containsAtLeast(item.getType(), item.getAmount())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public float getCompletePercentage() {
		float percent = 0;
		float temp;
		for (Item item : items) {
			temp = questOwner.getInventory().getItemAmount(item) / (float) item.getAmount();
			if(temp > 1) temp = 1;
			percent += temp;
		}
		return percent / items.size();
	}

	@Override
	public String getDescription() {
		StringBuilder out = new StringBuilder(rawDescription + "\n");
		int amount;
		for (Item item : items) {
			amount = Math.min(questOwner.getInventory().getItemAmount(item), item.getAmount());
			out.append("[" + item.getDisplayName() + "]").append(" ").append(amount).append("/").append(item.getAmount()).append("\n");
		}
		return out.toString();
	}

	@Override
	public String getRawDescription() {
		StringBuilder out = new StringBuilder(rawDescription + "\n");
		for (Item item : items) {
			out.append("[" + item.getDisplayName() + "]").append(" x").append(item.getAmount()).append("\n");
		}
		return out.toString();
	}

	@Override
	public QuestID getQuestID() {
		return questID;
	}
}
