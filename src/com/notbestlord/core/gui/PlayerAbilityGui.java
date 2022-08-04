package com.notbestlord.core.gui;

import com.notbestlord.ClientLauncher;
import com.notbestlord.GameClient;
import com.notbestlord.core.GuiManager;
import com.notbestlord.core.ObjectLoader;
import com.notbestlord.core.rpg.abilites.ActiveAbility;
import com.notbestlord.core.rpg.abilites.PassiveAbility;
import com.notbestlord.core.utils.Consts;
import com.notbestlord.network.packet.player.rpg.ActiveAbilityActivatePacket;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerAbilityGui implements IGui{
	public final List<ActiveAbility> activeAbilities = new ArrayList<>();
	public final Map<ActiveAbility, Float> activatedActiveAbilities = new HashMap<>();
	public final Map<String, Float> cooldownActiveAbilities = new HashMap<>();
	public final List<PassiveAbility> passiveAbilities = new ArrayList<>();
	private ImVec2 mousePos;
	private ActiveAbility currentActiveAbility = null;
	private final int color1 = new Color(232, 229, 229, 179).getRGB(),
			color2 = new Color(84, 83, 83, 179).getRGB();
	private int maxUsedAbilities = 5;
	public int usedAbilities = 0;

	public void frameUpdate(){
		List<ActiveAbility> lst1 = new ArrayList<>(activatedActiveAbilities.keySet());
		for(ActiveAbility ability : lst1){
			activatedActiveAbilities.put(ability, activatedActiveAbilities.get(ability) - Consts.deltaTime);
			if(activatedActiveAbilities.get(ability) <= 0){
				activatedActiveAbilities.remove(ability);
				if(ability.cooldown() != 0) {
					cooldownActiveAbilities.put(ability.getId(), ability.cooldown() + 0.25f);
				}
			}
		}
		List<String> lst2 = new ArrayList<>(cooldownActiveAbilities.keySet());
		for(String ability : lst2){
			if(cooldownActiveAbilities.get(ability) > 0){
				cooldownActiveAbilities.put(ability, cooldownActiveAbilities.get(ability) - Consts.deltaTime);
			}
			else{
				cooldownActiveAbilities.put(ability, 0f);
			}
		}
	}

	public void open(){
		ClientLauncher.getWindow().getGuiManager().toggleGui(this);
	}

	public void activate(int index){
		int i = index - 1;
		if(activeAbilities.size() > i && !activatedActiveAbilities.containsKey(activeAbilities.get(i)) &&
				!cooldownActiveAbilities.containsKey(activeAbilities.get(i).getId()) && usedAbilities < maxUsedAbilities) {
			ClientLauncher.getGame().sendToServerTcp(new ActiveAbilityActivatePacket(GameClient.uuid, activeAbilities.get(i).getId()));
		}
	}

	public void setActive(String id){
		for(ActiveAbility ability : activeAbilities){
			if(ability.getId().equals(id)){
				usedAbilities++;
				activatedActiveAbilities.put(ability, ability.getDuration() + 0.1f);
			}
		}
	}

	@Override
	public int updateStyle() {
		ImGui.pushStyleColor(ImGuiCol.WindowBg, color1);
		ImGui.pushStyleColor(ImGuiCol.TitleBgActive, color1);
		ImGui.pushStyleColor(ImGuiCol.TitleBg, color1);
		ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, color1);
		ImGui.pushStyleColor(ImGuiCol.Border, color1);
		ImGui.pushStyleColor(ImGuiCol.BorderShadow, color1);
		return 6;
	}

	@Override
	public boolean renderGui(GuiManager gm) {
		if(ClientLauncher.getWindow().isUiOpen()) {
			ImGui.begin("Abilities: ");
		}
		else{
			ImGui.begin("Abilities: ",ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoScrollbar);
		}
		ImGui.text("\n\n1-5:");
		for (int i = 1; i <= activeAbilities.size(); i++) {
			if (i % 5 == 0) {
				ImGui.text("    ");
			}
			ImGui.sameLine();
			ActiveAbility activeAbility = activeAbilities.get(i-1);
			//
			ImVec2 point = ImGui.getCursorScreenPos();
			//
			if(ImGui.imageButton(ObjectLoader.getTexture(activeAbility.textureID()), 64,64,0,0,1,1,0) &&
					currentActiveAbility != activeAbility && ClientLauncher.getWindow().isUiOpen()){
				// open description
				mousePos = ImGui.getMousePos();
				currentActiveAbility = activeAbility;
			}
			else if(!ClientLauncher.getWindow().isUiOpen()){
				currentActiveAbility = null;
			}
			//
			if(activatedActiveAbilities.containsKey(activeAbility)){
				ImGui.getWindowDrawList().addImage(ObjectLoader.getTexture("gui_abilities_active"), point.x, point.y,
						point.x + getDurationPercent(activeAbility, activatedActiveAbilities.get(activeAbility), 64),point.y + 64);
			}
			if(cooldownActiveAbilities.containsKey(activeAbility.getId())){
				ImGui.getWindowDrawList().addImage(ObjectLoader.getTexture("gui_abilities_cooldown"), point.x, point.y,
						point.x + getCooldownPercent(activeAbility, cooldownActiveAbilities.get(activeAbility.getId()), 64),point.y + 64);
			}

			//
			if(ClientLauncher.getWindow().isUiOpen()) {
				if (ImGui.beginDragDropSource()) {
					ImGui.setDragDropPayload("active_ability", activeAbility);
					ImGui.text(activeAbility.getDisplayName());
					ImGui.endDragDropSource();
				}
				if (ImGui.beginDragDropTarget()) {
					Object object = ImGui.acceptDragDropPayload("active_ability");
					if (object != null) {
						ActiveAbility ability = (ActiveAbility) object;
						if (activeAbilities.contains(ability)) {
							activeAbilities.set(activeAbilities.indexOf(ability), activeAbility);
							activeAbilities.set(i - 1, ability);
						}
					}
					ImGui.endDragDropTarget();
				}
			}
		}
		ImGui.end();
		ImGui.pushStyleColor(ImGuiCol.WindowBg, color2);
		ImGui.pushStyleColor(ImGuiCol.TitleBgActive, color2);
		ImGui.pushStyleColor(ImGuiCol.TitleBg, color2);
		ImGui.pushStyleColor(ImGuiCol.TitleBgCollapsed, color2);
		ImGui.pushStyleColor(ImGuiCol.Border, color2);
		ImGui.pushStyleColor(ImGuiCol.BorderShadow, color2);
		if(currentActiveAbility != null && ClientLauncher.getWindow().isUiOpen()){
			ImGui.setNextWindowPos(mousePos.x, mousePos.y);
			ImGui.begin(currentActiveAbility.getDisplayName(), ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize);
			ImGui.setWindowSize(120, Math.max(ImGui.getWindowHeight(), 80));
			ImGui.textWrapped(currentActiveAbility.description());
			if(!ImGui.isWindowFocused()){
				currentActiveAbility = null;
			}
			ImGui.end();
		}
		ImGui.popStyleColor(6);
		return false;
	}

	private float getCooldownPercent(ActiveAbility ability, float cooldown, float mul){
		return mul * (cooldown / ability.cooldown());
	}

	private float getDurationPercent(ActiveAbility ability, float duration, float mul){
		return mul * (duration / ability.duration());
	}
}
