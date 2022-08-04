package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.GameClient;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.rpg.quests.QuestID;
import com.notbestlord.core.utils.Utils;
import com.notbestlord.network.client.ClientQuestData;
import com.notbestlord.network.packet.player.rpg.QuestAcceptPacket;
import com.notbestlord.network.packet.player.rpg.QuestRequestCompletePacket;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerQuestGui implements IGui{
	private static class QuestAcceptPopup implements IGui{
		public String questId = "";
		public String description = "";
		@Override
		public int updateStyle() {
			ImGui.pushStyleColor(ImGuiCol.TitleBg, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.TitleBgActive, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.Border, Color.TRANSLUCENT);
			ImGui.pushStyleColor(ImGuiCol.BorderShadow, Color.TRANSLUCENT);
			return 5;
		}

		@Override
		public boolean renderGui(GuiManager gm) {
			ImGui.begin("Quest: " + questId, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse);
			ImGui.setWindowPos(ClientLauncher.getWindow().getWidth() / 2f - ImGui.getWindowWidth() / 2f,
					ClientLauncher.getWindow().getHeight() / 2f - ImGui.getWindowHeight() / 2f);
			ImGui.text(Utils.UpperCaseStart(questId) + ":\n");
			ImGui.textWrapped(description);
			ImGui.text("");
			if(ImGui.button("Accept")){
				// accept quest
				ClientLauncher.getGame().sendToServerTcp(new QuestAcceptPacket(GameClient.uuid, true));
				ClientLauncher.getWindow().getGuiManager().removeInteractGui(this);
			}
			ImGui.sameLine();
			ImGui.text("  ");
			ImGui.sameLine();
			if(ImGui.button("Decline")){
				// decline quest
				ClientLauncher.getGame().sendToServerTcp(new QuestAcceptPacket(GameClient.uuid, false));
				ClientLauncher.getWindow().getGuiManager().removeInteractGui(this);
			}
			ImGui.end();
			return false;
		}
	}

	private final QuestAcceptPopup questAcceptPopup = new QuestAcceptPopup();


	public void setAcceptRequestQuest(QuestID questID, String description){
		questAcceptPopup.questId = questID.toString();
		questAcceptPopup.description = description;
		ClientLauncher.getWindow().getGuiManager().toggleInteractGui(questAcceptPopup);
	}


	private final Map<QuestID, ClientQuestData> quests = new HashMap<>();

	public void clearQuests(){
		quests.clear();
	}

	@Override
	public int updateStyle() {
		return 0;
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		if(ClientLauncher.getWindow().isUiOpen()) {
			ImGui.begin("Quests:");
		}
		else{
			ImGui.begin("Quests:", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);
		}
		if(quests.values().isEmpty()){
			ImGui.textWrapped("No active quests.\n\n");
		}
		else {
			List<ClientQuestData> questData = new ArrayList<>(quests.values());
			for (ClientQuestData quest : questData) {
				if (ImGui.treeNode(quest.getQuestID().toString())) {
					ImGui.indent();
					ImGui.textWrapped(quest.getDescription());
					ImGui.text("Completion Percentage: " + String.format("%.1f", (quest.getPercent() * 100)) + "%");
					if (quest.getPercent() == 1 && ImGui.button("Complete")) {
						ClientLauncher.getGame().sendToServerTcp(new QuestRequestCompletePacket(GameClient.uuid, quest.getQuestID()));
					}
					ImGui.unindent();
					ImGui.treePop();
				}
			}
		}
		ImGui.end();
		return false;
	}

	public void openQuestMenu(){
		ClientLauncher.getWindow().getGuiManager().toggleGui(this);
	}

	public void addQuest(QuestID questID, String description, float percent){
		quests.put(questID, new ClientQuestData(questID, description, percent));
	}

	public void updateQuest(QuestID questID, String description, float percent){
		if(!quests.containsKey(questID)) {
			quests.put(questID, new ClientQuestData(questID, description, percent));
			return;
		}
		quests.get(questID).setDescription(description);
		quests.get(questID).setPercent(percent);
	}

	public void completeQuest(QuestID questID){
		quests.remove(questID);
	}
}
